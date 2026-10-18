package net.yigitguven.create_shared.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yigitguven.create_shared.CreateShared;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateShared.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FACTORY_DECORATIONS_TAB =
        CREATIVE_MODE_TABS.register("factory_decorations", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_shared.factory_decorations"))
            .icon(() -> new ItemStack(ModBlocks.HAZARD_BLOCK.get()))
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
            })
            .build());

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
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
