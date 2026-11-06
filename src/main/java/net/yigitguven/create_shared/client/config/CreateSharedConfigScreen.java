package net.yigitguven.create_shared.client.config;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import com.mojang.realmsclient.RealmsMainScreen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationListScreen;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.ListValueSpec;
import net.neoforged.neoforge.common.ModConfigSpec.RestartType;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;
import net.yigitguven.create_shared.CreateShared;
import net.yigitguven.create_shared.config.CreateSharedConfigs;
import net.yigitguven.create_shared.config.CreateSharedConfigs.ModConfigGroup;

/**
 * Shared configuration screen for Create: Shared and all mods that depend on it.
 * <p>
 * Groups configurations by {@link ModConfig.Type} (Client, Server, Common, Startup).
 * Inside each type screen, presents Create: Shared at the top followed by dependent
 * mods in alphabetical order (A-Z). If Create: Shared does not have a config of a given
 * type (e.g. Client), a screen for that type is automatically created when any dependent
 * mod in the modpack provides one.
 */
public class CreateSharedConfigScreen extends OptionsSubScreen {
    private static final String LANG_PREFIX = "neoforge.configuration.uitext.";
    private static final String SECTION = LANG_PREFIX + "section";
    private static final String SECTION_TEXT = LANG_PREFIX + "sectiontext";
    private static final String CRUMB = LANG_PREFIX + "breadcrumb.order";
    private static final String FILENAME_TOOLTIP = LANG_PREFIX + "filenametooltip";
    private static final MutableComponent EMPTY_LINE = Component.literal("\n\n");

    protected final ModContainer mod;
    public RestartType needsRestart = RestartType.NONE;
    private boolean autoClose = false;

    public CreateSharedConfigScreen(final ModContainer mod, final Screen parent) {
        super(parent, Minecraft.getInstance().options, resolveScreenTitle(mod));
        this.mod = mod;
    }

    /**
     * Registers (and overwrites) {@link IConfigScreenFactory} on Create: Shared and all
     * participating dependent mods so clicking "Config" on any of them opens this shared screen.
     */
    public static void registerForAllParticipatingMods() {
        for (ModContainer container : CreateSharedConfigs.getParticipatingMods()) {
            container.registerExtensionPoint(
                IConfigScreenFactory.class,
                CreateSharedConfigScreen::new
            );
        }
    }

    private static Component resolveScreenTitle(ModContainer fallbackMod) {
        String displayName = ModList.get()
            .getModContainerById(CreateShared.MODID)
            .map(c -> c.getModInfo().getDisplayName())
            .orElseGet(() -> fallbackMod.getModInfo().getDisplayName());
        String key = CreateShared.MODID + ".configuration.title";
        if (I18n.exists(key)) {
            return Component.translatable(key, displayName);
        }
        return Component.translatable(LANG_PREFIX + "title", displayName);
    }

    @Override
    protected void addOptions() {
        Button singleButton = null;
        int count = 0;

        for (final Type type : ModConfig.Type.values()) {
            List<ModConfigGroup> groups = CreateSharedConfigs.getModConfigGroups(type);
            if (groups.isEmpty()) {
                continue;
            }

            list.addSmall(new StringWidget(
                ConfigurationScreen.BIG_BUTTON_WIDTH,
                Button.DEFAULT_HEIGHT,
                Component.translatable(LANG_PREFIX + type.name().toLowerCase(Locale.ENGLISH)).withStyle(ChatFormatting.UNDERLINE),
                font
            ).alignLeft(), null);

            Component typeLabel = Component.translatable(LANG_PREFIX + "type." + type.name().toLowerCase(Locale.ROOT));
            Component sectionTitle = Component.translatable(
                LANG_PREFIX + "title." + type.name().toLowerCase(Locale.ROOT),
                ModList.get().getModContainerById(CreateShared.MODID)
                    .map(c -> c.getModInfo().getDisplayName())
                    .orElseGet(() -> mod.getModInfo().getDisplayName())
            );

            Button btn = Button.builder(
                Component.translatable(SECTION, typeLabel),
                button -> minecraft.setScreen(new CreateSharedSectionScreen(this, type, groups, sectionTitle).rebuild())
            ).width(ConfigurationScreen.BIG_BUTTON_WIDTH).build();

            boolean anyLoaded = groups.stream()
                .flatMap(g -> g.configs().stream())
                .anyMatch(cfg -> ((ModConfigSpec) cfg.getSpec()).isLoaded());

            MutableComponent tooltip = Component.empty();
            if (!anyLoaded) {
                tooltip.append(ConfigurationScreen.TOOLTIP_CANNOT_EDIT_NOT_LOADED).append(EMPTY_LINE);
                btn.active = false;
                count = 99; // prevent autoClose
            } else if (type == Type.SERVER && minecraft.getCurrentServer() != null && !minecraft.isSingleplayer()) {
                tooltip.append(ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_ONLINE).append(EMPTY_LINE);
                btn.active = false;
                count = 99; // prevent autoClose
            } else if (type == Type.SERVER && minecraft.hasSingleplayerServer() && minecraft.getSingleplayerServer().isPublished()) {
                tooltip.append(ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_OPEN_TO_LAN).append(EMPTY_LINE);
                btn.active = false;
                count = 99; // prevent autoClose
            }

            String fileNames = groups.stream()
                .flatMap(g -> g.configs().stream())
                .map(ModConfig::getFileName)
                .collect(Collectors.joining(", "));
            tooltip.append(Component.translatable(FILENAME_TOOLTIP, fileNames).withStyle(ChatFormatting.GRAY));

            btn.setTooltip(Tooltip.create(tooltip));
            list.addSmall(btn, null);
            singleButton = btn;
            count++;
        }

        if (count == 1 && singleButton != null && singleButton.active) {
            autoClose = true;
            singleButton.onPress();
        }
    }

    @Override
    public void added() {
        super.added();
        if (autoClose) {
            autoClose = false;
            onClose();
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void onClose() {
        switch (needsRestart) {
            case GAME -> {
                minecraft.setScreen(new TooltipConfirmScreen(b -> {
                    if (b) {
                        minecraft.stop();
                    } else {
                        super.onClose();
                    }
                }, ConfigurationScreen.GAME_RESTART_TITLE, ConfigurationScreen.GAME_RESTART_MESSAGE, ConfigurationScreen.GAME_RESTART_YES, ConfigurationScreen.RESTART_NO));
                return;
            }
            case WORLD -> {
                if (minecraft.level != null) {
                    minecraft.setScreen(new TooltipConfirmScreen(b -> {
                        if (b) {
                            onDisconnect();
                        } else {
                            super.onClose();
                        }
                    }, ConfigurationScreen.SERVER_RESTART_TITLE, ConfigurationScreen.SERVER_RESTART_MESSAGE,
                        minecraft.isLocalServer() ? ConfigurationScreen.RETURN_TO_MENU : CommonComponents.GUI_DISCONNECT,
                        ConfigurationScreen.RESTART_NO));
                    return;
                }
            }
        }
        super.onClose();
    }

    private void onDisconnect() {
        boolean localServer = this.minecraft.isLocalServer();
        ServerData serverData = this.minecraft.getCurrentServer();
        this.minecraft.level.disconnect();
        if (localServer) {
            this.minecraft.disconnect(new GenericMessageScreen(ConfigurationScreen.SAVING_LEVEL));
        } else {
            this.minecraft.disconnect();
        }

        TitleScreen titleScreen = new TitleScreen();
        if (localServer) {
            this.minecraft.setScreen(titleScreen);
        } else if (serverData != null && serverData.isRealm()) {
            this.minecraft.setScreen(new RealmsMainScreen(titleScreen));
        } else {
            this.minecraft.setScreen(new JoinMultiplayerScreen(titleScreen));
        }
    }

    private static final class TooltipConfirmScreen extends ConfirmScreen {
        private boolean seenYes = false;

        private TooltipConfirmScreen(BooleanConsumer callback, Component title, Component message, Component yesButton, Component noButton) {
            super(callback, title, message, yesButton, noButton);
        }

        @Override
        protected void init() {
            seenYes = false;
            super.init();
        }

        @Override
        protected void addExitButton(Button button) {
            if (seenYes) {
                button.setTooltip(Tooltip.create(ConfigurationScreen.RESTART_NO_TOOLTIP));
            } else {
                seenYes = true;
            }
            super.addExitButton(button);
        }
    }

    /**
     * Combined section screen for a single {@link ModConfig.Type} that displays Create: Shared
     * at the top followed by all dependent mods (sorted A-Z) and their respective config sections/entries.
     */
    public static class CreateSharedSectionScreen extends ConfigurationSectionScreen {
        private final List<ModConfigGroup> groups;
        private final List<ModConfigSectionHelper> helpers = new ArrayList<>();
        private final Deque<List<ModConfigSectionHelper>> undoHistory = new ArrayDeque<>();
        private boolean performingUndoOrReset = false;

        public CreateSharedSectionScreen(
            final Screen parent,
            final ModConfig.Type type,
            final List<ModConfigGroup> groups,
            final Component title
        ) {
            super(parent, type, groups.getFirst().configs().getFirst(), title);
            this.groups = groups;
            for (ModConfigGroup group : groups) {
                for (ModConfig modConfig : group.configs()) {
                    if (((ModConfigSpec) modConfig.getSpec()).isLoaded()) {
                        this.helpers.add(new ModConfigSectionHelper(this, type, group, modConfig, title));
                    }
                }
            }
        }

        Minecraft getMinecraftInstance() {
            return this.minecraft != null ? this.minecraft : Minecraft.getInstance();
        }

        void onHelperTopLevelValueChanged(ModConfigSectionHelper helper, RestartType helperRestart) {
            this.changed = true;
            this.needsRestart = this.needsRestart.with(helperRestart);
            if (!performingUndoOrReset) {
                undoHistory.push(List.of(helper));
            }
        }

        void markHelperChanged(ModConfigSectionHelper helper, RestartType helperRestart) {
            this.changed = true;
            this.needsRestart = this.needsRestart.with(helperRestart);
        }

        @Override
        protected CreateSharedSectionScreen rebuild() {
            if (list != null) {
                list.children().clear();
                boolean hasUndoableElements = false;

                for (ModConfigGroup group : groups) {
                    List<ModConfigSectionHelper> groupHelpers = helpers.stream()
                        .filter(h -> h.group == group)
                        .toList();
                    if (groupHelpers.isEmpty()) {
                        continue;
                    }

                    // Mod Title Header
                    MutableComponent modHeaderTitle = Component.literal(group.displayName())
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD, ChatFormatting.UNDERLINE);
                    StringWidget modHeader = new StringWidget(
                        ConfigurationScreen.BIG_BUTTON_WIDTH,
                        Button.DEFAULT_HEIGHT,
                        modHeaderTitle,
                        font
                    ).alignCenter();

                    MutableComponent modTooltip = Component.literal(group.displayName()).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                        .append(Component.literal("\nMod ID: " + group.modId()).withStyle(ChatFormatting.DARK_GRAY));
                    for (ModConfig cfg : group.configs()) {
                        modTooltip.append(Component.literal("\n").append(
                            Component.translatable(FILENAME_TOOLTIP, cfg.getFileName()).withStyle(ChatFormatting.GRAY)
                        ));
                    }
                    modHeader.setTooltip(Tooltip.create(modTooltip));
                    list.addSmall(modHeader, null);

                    // Mod Config Entries & Sections
                    for (ModConfigSectionHelper helper : groupHelpers) {
                        List<@Nullable Element> elements = helper.buildModElements();
                        for (final Element element : elements) {
                            if (element != null) {
                                if (element.name() == null) {
                                    list.addSmall(
                                        new StringWidget(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.empty(), font),
                                        element.getWidget(options)
                                    );
                                } else {
                                    final StringWidget label = new StringWidget(
                                        Button.DEFAULT_WIDTH,
                                        Button.DEFAULT_HEIGHT,
                                        element.name(),
                                        font
                                    ).alignLeft();
                                    if (element.tooltip() != null) {
                                        label.setTooltip(Tooltip.create(element.tooltip()));
                                    }
                                    list.addSmall(label, element.getWidget(options));
                                }
                                hasUndoableElements |= element.undoable();
                            }
                        }
                    }
                }

                if (hasUndoableElements && undoButton == null) {
                    createUndoButton();
                    createResetButton();
                }
            }
            return this;
        }

        @Override
        protected boolean isAnyNondefault() {
            for (ModConfigSectionHelper helper : helpers) {
                if (helper.hasNonDefaultTopLevelValues()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void createUndoButton() {
            undoButton = Button.builder(ConfigurationScreen.UNDO, button -> {
                if (!undoHistory.isEmpty()) {
                    performingUndoOrReset = true;
                    try {
                        List<ModConfigSectionHelper> stepHelpers = undoHistory.pop();
                        for (ModConfigSectionHelper helper : stepHelpers) {
                            helper.undoLastStep();
                        }
                    } finally {
                        performingUndoOrReset = false;
                    }
                    rebuild();
                }
            }).tooltip(Tooltip.create(ConfigurationScreen.UNDO_TOOLTIP)).width(Button.SMALL_WIDTH).build();
            undoButton.active = false;
        }

        @Override
        protected void setUndoButtonstate(boolean state) {
            if (undoButton != null) {
                undoButton.active = !undoHistory.isEmpty();
            }
        }

        @Override
        protected void createResetButton() {
            resetButton = Button.builder(ConfigurationScreen.RESET, button -> {
                List<ModConfigSectionHelper> affected = new ArrayList<>();
                performingUndoOrReset = true;
                try {
                    for (ModConfigSectionHelper helper : helpers) {
                        if (helper.resetTopLevelValues()) {
                            affected.add(helper);
                        }
                    }
                } finally {
                    performingUndoOrReset = false;
                }
                if (!affected.isEmpty()) {
                    undoHistory.push(affected);
                    rebuild();
                }
            }).tooltip(Tooltip.create(ConfigurationScreen.RESET_TOOLTIP)).width(Button.SMALL_WIDTH).build();
        }

        @Override
        public void onClose() {
            if (this.list != null) {
                this.list.applyUnsavedChanges();
            }
            boolean anySaved = false;
            for (ModConfigSectionHelper helper : helpers) {
                if (helper.isSpecChanged()) {
                    helper.saveSpec();
                    anySaved = true;
                }
            }
            if (changed && !anySaved) {
                for (ModConfigSectionHelper helper : helpers) {
                    helper.saveSpec();
                }
            }
            if (changed || anySaved) {
                if (lastScreen instanceof CreateSharedConfigScreen parent) {
                    parent.needsRestart = parent.needsRestart.with(needsRestart);
                } else if (lastScreen instanceof ConfigurationScreen parent) {
                    parent.needsRestart = parent.needsRestart.with(needsRestart);
                }
            }
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    /**
     * Per-{@link ModConfig} helper that builds configuration widgets and subsections scoped
     * to a specific mod's {@link Context} while displaying inside {@link CreateSharedSectionScreen}.
     */
    private static class ModConfigSectionHelper extends ConfigurationSectionScreen {
        private final CreateSharedSectionScreen ownerScreen;
        private final ModConfigGroup group;

        ModConfigSectionHelper(
            final CreateSharedSectionScreen ownerScreen,
            final ModConfig.Type type,
            final ModConfigGroup group,
            final ModConfig modConfig,
            final Component title
        ) {
            super(ownerScreen, type, modConfig, title);
            this.ownerScreen = ownerScreen;
            this.group = group;
            this.minecraft = Minecraft.getInstance();
            this.font = this.minecraft.font;
        }

        @Override
        protected void onChanged(final String key) {
            super.onChanged(key);
            ownerScreen.onHelperTopLevelValueChanged(this, this.needsRestart);
        }

        void markChanged(RestartType childRestart) {
            this.changed = true;
            this.needsRestart = this.needsRestart.with(childRestart);
        }

        boolean isSpecChanged() {
            return this.changed;
        }

        void saveSpec() {
            this.context.modSpec().save();
        }

        boolean hasNonDefaultTopLevelValues() {
            return super.isAnyNondefault();
        }

        void undoLastStep() {
            this.undoManager.undo();
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        boolean resetTopLevelValues() {
            List<ConfigurationScreen.UndoManager.Step<?>> steps = new ArrayList<>();
            for (final Entry entry : context.entries()) {
                if (entry.getRawValue() instanceof final ModConfigSpec.ConfigValue cv
                        && !(getValueSpec(entry.getKey()) instanceof ListValueSpec)
                        && isNonDefault(cv)) {
                    final String key = entry.getKey();
                    ValueSpec valueSpec = getValueSpec(key);
                    if (valueSpec != null) {
                        steps.add(undoManager.step(v -> {
                            cv.set(v);
                            onChanged(key);
                        }, valueSpec.correct(null), v -> {
                            cv.set(v);
                            onChanged(key);
                        }, cv.getRaw()));
                    }
                }
            }
            if (steps.isEmpty()) {
                return false;
            }
            undoManager.add(steps);
            return true;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<@Nullable Element> buildModElements() {
            this.minecraft = ownerScreen.getMinecraftInstance();
            this.font = this.minecraft.font;

            final List<@Nullable Element> elements = new ArrayList<>();
            for (final Entry entry : context.entries()) {
                final String key = entry.getKey();
                final Object rawValue = entry.getRawValue();
                switch (rawValue) {
                    case ConfigValue cv -> {
                        var valueSpec = getValueSpec(key);
                        var element = switch (valueSpec) {
                            case ListValueSpec listValueSpec -> createList(key, listValueSpec, cv);
                            case ValueSpec spec when cv.getClass() == ConfigValue.class && spec.getDefault() instanceof String ->
                                createStringValue(key, valueSpec::test, () -> (String) cv.getRaw(), cv::set);
                            case ValueSpec spec when cv.getClass() == ConfigValue.class && spec.getDefault() instanceof Integer ->
                                createIntegerValue(key, valueSpec, () -> (Integer) cv.getRaw(), cv::set);
                            case ValueSpec spec when cv.getClass() == ConfigValue.class && spec.getDefault() instanceof Long ->
                                createLongValue(key, valueSpec, () -> (Long) cv.getRaw(), cv::set);
                            case ValueSpec spec when cv.getClass() == ConfigValue.class && spec.getDefault() instanceof Double ->
                                createDoubleValue(key, valueSpec, () -> (Double) cv.getRaw(), cv::set);
                            case ValueSpec spec when cv.getClass() == ConfigValue.class && spec.getDefault() instanceof Enum<?> ->
                                createEnumValue(key, valueSpec, (Supplier) cv::getRaw, (Consumer) cv::set);
                            case null -> null;
                            default -> switch (cv) {
                                case ModConfigSpec.BooleanValue value -> createBooleanValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.IntValue value -> createIntegerValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.LongValue value -> createLongValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.DoubleValue value -> createDoubleValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.EnumValue value -> createEnumValue(key, valueSpec, (Supplier) value::getRaw, (Consumer) value::set);
                                default -> createOtherValue(key, cv);
                            };
                        };
                        elements.add(context.filter().filterEntry(context, key, element));
                    }
                    case UnmodifiableConfig subsection when context.valueSpecs().get(key) instanceof UnmodifiableConfig subconfig ->
                        elements.add(createSection(key, subconfig, subsection));
                    default -> elements.add(context.filter().filterEntry(context, key, createOtherSection(key, rawValue)));
                }
            }
            elements.addAll(createSyntheticValues());
            return elements;
        }

        @Nullable
        @Override
        protected Element createSection(final String key, final UnmodifiableConfig subconfig, final UnmodifiableConfig subsection) {
            if (subconfig.isEmpty()) {
                return null;
            }
            Component subTitle = Component.translatable(
                CRUMB,
                Component.literal(group.displayName()),
                ConfigurationScreen.CRUMB_SEPARATOR,
                Component.translatable(getTranslationKey(key))
            );
            String buttonKey = getTranslationKey(key) + ".button";
            Component buttonLabel = Component.translatable(I18n.exists(buttonKey) ? buttonKey : SECTION_TEXT);
            return new Element(
                Component.translatable(SECTION, getTranslationComponent(key)),
                getTooltipComponent(key, null),
                Button.builder(
                    Component.translatable(SECTION, buttonLabel),
                    button -> ownerScreen.getMinecraftInstance().setScreen(sectionCache.computeIfAbsent(
                        key,
                        k -> new SubSectionScreen(context, ownerScreen, this, subconfig.valueMap(), key, subsection.entrySet(), subTitle).rebuild()
                    ))
                )
                .tooltip(Tooltip.create(getTooltipComponent(key, null)))
                .width(Button.DEFAULT_WIDTH)
                .build(),
                false
            );
        }

        @Nullable
        @Override
        protected <T> Element createList(final String key, final ListValueSpec spec, final ModConfigSpec.ConfigValue<List<T>> list) {
            Component listTitle = Component.translatable(
                CRUMB,
                ownerScreen.getTitle(),
                ConfigurationScreen.CRUMB_SEPARATOR,
                Component.translatable(
                    CRUMB,
                    Component.literal(group.displayName()),
                    ConfigurationScreen.CRUMB_SEPARATOR,
                    getTranslationComponent(key)
                )
            );
            String buttonKey = getTranslationKey(key) + ".button";
            Component buttonLabel = Component.translatable(I18n.exists(buttonKey) ? buttonKey : SECTION_TEXT);
            return new Element(
                Component.translatable(SECTION, getTranslationComponent(key)),
                getTooltipComponent(key, null),
                Button.builder(
                    Component.translatable(SECTION, buttonLabel),
                    button -> ownerScreen.getMinecraftInstance().setScreen(sectionCache.computeIfAbsent(
                        key,
                        k -> new SubListScreen<>(Context.list(context, ownerScreen), this, key, listTitle, spec, list).rebuild()
                    ))
                )
                .tooltip(Tooltip.create(getTooltipComponent(key, null)))
                .build(),
                false
            );
        }
    }

    private static class SubSectionScreen extends ConfigurationSectionScreen {
        private final CreateSharedSectionScreen rootScreen;
        private final ModConfigSectionHelper helper;

        SubSectionScreen(
            final Context parentContext,
            final Screen parent,
            final ModConfigSectionHelper helper,
            final Map<String, Object> valueSpecs,
            final String key,
            final Set<? extends Entry> entrySet,
            final Component title
        ) {
            super(parentContext, parent, valueSpecs, key, entrySet, title);
            this.rootScreen = helper.ownerScreen;
            this.helper = helper;
        }

        @Override
        protected SubSectionScreen rebuild() {
            super.rebuild();
            return this;
        }

        @Override
        public void onClose() {
            if (this.list != null) {
                this.list.applyUnsavedChanges();
            }
            if (changed) {
                helper.markChanged(needsRestart);
                rootScreen.markHelperChanged(helper, needsRestart);
            }
            super.onClose();
        }
    }

    private static class SubListScreen<T> extends ConfigurationListScreen<T> {
        private final CreateSharedSectionScreen rootScreen;
        private final ModConfigSectionHelper helper;

        SubListScreen(
            final Context context,
            final ModConfigSectionHelper helper,
            final String key,
            final Component title,
            final ListValueSpec spec,
            final ModConfigSpec.ConfigValue<List<T>> valueList
        ) {
            super(context, key, title, spec, valueList);
            this.rootScreen = helper.ownerScreen;
            this.helper = helper;
        }

        @Override
        protected SubListScreen<T> rebuild() {
            super.rebuild();
            return this;
        }

        @Override
        public void onClose() {
            if (this.list != null) {
                this.list.applyUnsavedChanges();
            }
            if (changed) {
                helper.markChanged(needsRestart);
                rootScreen.markHelperChanged(helper, needsRestart);
            }
            super.onClose();
        }
    }
}

