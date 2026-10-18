package net.yigitguven.create_shared.registry;

import com.simibubi.create.AllItems;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yigitguven.create_shared.CreateShared;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateShared.MODID);

    public static ItemStack RAINBOW_GOGGLES_ICON;

    public static ItemStack getTabIcon() {
        if (RAINBOW_GOGGLES_ICON == null) {
            ItemStack stack = new ItemStack(AllItems.GOGGLES.get());
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("CreativeTabIconRainbow", true);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            RAINBOW_GOGGLES_ICON = stack;
        }
        return RAINBOW_GOGGLES_ICON;
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
        CREATIVE_MODE_TABS.register("create_shared", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_shared"))
            .icon(ModCreativeTabs::getTabIcon)
            .displayItems((parameters, output) -> {
                // Hazard (Yellow / Black)
                output.accept(ModItems.HAZARD_BLOCK.get());
                output.accept(ModItems.HAZARD_STAIRS.get());
                output.accept(ModItems.HAZARD_SLAB.get());
                output.accept(ModItems.HAZARD_WALL.get());

                // Emergency Hazard (Red / White)
                output.accept(ModItems.EMERGENCY_HAZARD_BLOCK.get());
                output.accept(ModItems.EMERGENCY_HAZARD_STAIRS.get());
                output.accept(ModItems.EMERGENCY_HAZARD_SLAB.get());
                output.accept(ModItems.EMERGENCY_HAZARD_WALL.get());

                // Diamond Plate
                output.accept(ModItems.DIAMOND_PLATE.get());
                output.accept(ModItems.DIAMOND_PLATE_STAIRS.get());
                output.accept(ModItems.DIAMOND_PLATE_SLAB.get());
                output.accept(ModItems.DIAMOND_PLATE_WALL.get());

                // Riveted Sheet Metal
                output.accept(ModItems.RIVETED_SHEET_METAL.get());
                output.accept(ModItems.RIVETED_SHEET_METAL_STAIRS.get());
                output.accept(ModItems.RIVETED_SHEET_METAL_SLAB.get());
                output.accept(ModItems.RIVETED_SHEET_METAL_WALL.get());

                // Vent & Grate
                output.accept(ModItems.FACTORY_VENT.get());
                output.accept(ModItems.INDUSTRIAL_GRATE.get());

                // Safety Gear
                output.accept(ModItems.HARD_HAT.get());

                // Smithing Template
                output.accept(ModItems.CREATE_ARMOR_TRIM_SMITHING_TEMPLATE.get());
            })
            .build());

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.CREATE_ARMOR_TRIM_SMITHING_TEMPLATE);
        }
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.HARD_HAT);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.HAZARD_BLOCK);
            event.accept(ModItems.HAZARD_STAIRS);
            event.accept(ModItems.HAZARD_SLAB);
            event.accept(ModItems.HAZARD_WALL);

            event.accept(ModItems.EMERGENCY_HAZARD_BLOCK);
            event.accept(ModItems.EMERGENCY_HAZARD_STAIRS);
            event.accept(ModItems.EMERGENCY_HAZARD_SLAB);
            event.accept(ModItems.EMERGENCY_HAZARD_WALL);

            event.accept(ModItems.DIAMOND_PLATE);
            event.accept(ModItems.DIAMOND_PLATE_STAIRS);
            event.accept(ModItems.DIAMOND_PLATE_SLAB);
            event.accept(ModItems.DIAMOND_PLATE_WALL);

            event.accept(ModItems.RIVETED_SHEET_METAL);
            event.accept(ModItems.RIVETED_SHEET_METAL_STAIRS);
            event.accept(ModItems.RIVETED_SHEET_METAL_SLAB);
            event.accept(ModItems.RIVETED_SHEET_METAL_WALL);

            event.accept(ModItems.FACTORY_VENT);
            event.accept(ModItems.INDUSTRIAL_GRATE);
        }
    }
}
