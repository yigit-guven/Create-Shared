package net.yigitguven.create_shared.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.StringUtil;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforgespi.language.IModInfo;
import net.yigitguven.create_shared.CreateShared;

/**
 * Central registry and discovery utility for aggregating configurations of {@code create_shared}
 * and any mods that depend on {@code create_shared}.
 */
public final class CreateSharedConfigs {
    private static final Set<String> EXPLICIT_MOD_IDS = ConcurrentHashMap.newKeySet();

    public record ModConfigGroup(ModContainer modContainer, List<ModConfig> configs) {
        public String modId() {
            return modContainer.getModId();
        }

        public String displayName() {
            return modContainer.getModInfo().getDisplayName();
        }
    }

    private CreateSharedConfigs() {}

    /**
     * Explicitly registers a mod ID so its configurations are presented alongside Create: Shared's
     * configuration screen. Mods that declare {@code create_shared} as a dependency in their
     * mod metadata file are detected automatically without needing to call this method.
     *
     * @param modId The mod ID to include in the shared configuration screen.
     */
    public static void registerMod(String modId) {
        if (modId != null && !modId.isBlank()) {
            EXPLICIT_MOD_IDS.add(modId);
        }
    }

    /**
     * Convenience method for dependent mods to register a {@link ModConfig} with their
     * {@link ModContainer} while ensuring the mod is included in Create: Shared's config screen.
     */
    public static void registerConfig(ModContainer container, ModConfig.Type type, IConfigSpec spec) {
        registerMod(container.getModId());
        container.registerConfig(type, spec);
    }

    /**
     * Convenience method for dependent mods to register a named {@link ModConfig} with their
     * {@link ModContainer} while ensuring the mod is included in Create: Shared's config screen.
     */
    public static void registerConfig(ModContainer container, ModConfig.Type type, IConfigSpec spec, String fileName) {
        registerMod(container.getModId());
        container.registerConfig(type, spec, fileName);
    }

    /**
     * Returns true if the given mod is {@code create_shared}, was explicitly registered via
     * {@link #registerMod(String)}, or declares {@code create_shared} as a required/optional
     * dependency in its mod metadata file.
     */
    public static boolean isParticipatingMod(IModInfo modInfo) {
        if (modInfo == null) {
            return false;
        }
        String modId = modInfo.getModId();
        if (CreateShared.MODID.equals(modId) || EXPLICIT_MOD_IDS.contains(modId)) {
            return true;
        }
        for (IModInfo.ModVersion dependency : modInfo.getDependencies()) {
            if (CreateShared.MODID.equals(dependency.getModId())) {
                IModInfo.DependencyType depType = dependency.getType();
                if (depType == IModInfo.DependencyType.REQUIRED || depType == IModInfo.DependencyType.OPTIONAL) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns all participating {@link ModContainer}s ordered with {@code create_shared} first,
     * followed by all dependent mods sorted alphabetically (A-Z) by display name.
     */
    public static List<ModContainer> getParticipatingMods() {
        ModList modList = ModList.get();
        if (modList == null) {
            return Collections.emptyList();
        }

        ModContainer createSharedContainer = null;
        List<ModContainer> dependentContainers = new ArrayList<>();

        for (ModContainer container : modList.getSortedMods()) {
            if (!isParticipatingMod(container.getModInfo())) {
                continue;
            }
            if (CreateShared.MODID.equals(container.getModId())) {
                createSharedContainer = container;
            } else {
                dependentContainers.add(container);
            }
        }

        dependentContainers.sort(Comparator
            .comparing((ModContainer c) -> stripFormatting(c.getModInfo().getDisplayName()), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(ModContainer::getModId, String.CASE_INSENSITIVE_ORDER));

        List<ModContainer> result = new ArrayList<>(dependentContainers.size() + 1);
        if (createSharedContainer != null) {
            result.add(createSharedContainer);
        }
        result.addAll(dependentContainers);
        return result;
    }

    /**
     * Returns all participating mods that have at least one {@link ModConfig} of the given {@link ModConfig.Type},
     * ordered with {@code create_shared} at the top followed by dependent mods alphabetically (A-Z).
     */
    public static List<ModConfigGroup> getModConfigGroups(ModConfig.Type type) {
        Set<ModConfig> configsForType = ModConfigs.getConfigSet(type);
        if (configsForType.isEmpty()) {
            return Collections.emptyList();
        }

        List<ModContainer> participatingMods = getParticipatingMods();
        List<ModConfigGroup> groups = new ArrayList<>();

        for (ModContainer container : participatingMods) {
            String modId = container.getModId();
            List<ModConfig> modConfigs = new ArrayList<>();
            for (ModConfig modConfig : configsForType) {
                if (modId.equals(modConfig.getModId()) && modConfig.getSpec() instanceof ModConfigSpec spec && !spec.isEmpty()) {
                    modConfigs.add(modConfig);
                }
            }
            if (!modConfigs.isEmpty()) {
                modConfigs.sort(Comparator.comparing(ModConfig::getFileName, String.CASE_INSENSITIVE_ORDER));
                groups.add(new ModConfigGroup(container, Collections.unmodifiableList(modConfigs)));
            }
        }

        return groups;
    }

    private static String stripFormatting(String text) {
        if (text == null) {
            return "";
        }
        return StringUtil.stripColor(text).trim();
    }
}

