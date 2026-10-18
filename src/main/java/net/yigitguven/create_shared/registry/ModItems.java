package net.yigitguven.create_shared.registry;

import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yigitguven.create_shared.CreateShared;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateShared.MODID);

    private static DeferredItem<BlockItem> registerBlockItem(String name, Supplier<? extends Block> block) {
        return ITEMS.registerSimpleBlockItem(name, block);
    }

    // Hazard (Yellow / Black)
    public static final DeferredItem<BlockItem> HAZARD_BLOCK = registerBlockItem("hazard_block", ModBlocks.HAZARD_BLOCK);
    public static final DeferredItem<BlockItem> HAZARD_STAIRS = registerBlockItem("hazard_stairs", ModBlocks.HAZARD_STAIRS);
    public static final DeferredItem<BlockItem> HAZARD_SLAB = registerBlockItem("hazard_slab", ModBlocks.HAZARD_SLAB);
    public static final DeferredItem<BlockItem> HAZARD_WALL = registerBlockItem("hazard_wall", ModBlocks.HAZARD_WALL);

    // Emergency Hazard (Red / White)
    public static final DeferredItem<BlockItem> EMERGENCY_HAZARD_BLOCK = registerBlockItem("emergency_hazard_block", ModBlocks.EMERGENCY_HAZARD_BLOCK);
    public static final DeferredItem<BlockItem> EMERGENCY_HAZARD_STAIRS = registerBlockItem("emergency_hazard_stairs", ModBlocks.EMERGENCY_HAZARD_STAIRS);
    public static final DeferredItem<BlockItem> EMERGENCY_HAZARD_SLAB = registerBlockItem("emergency_hazard_slab", ModBlocks.EMERGENCY_HAZARD_SLAB);
    public static final DeferredItem<BlockItem> EMERGENCY_HAZARD_WALL = registerBlockItem("emergency_hazard_wall", ModBlocks.EMERGENCY_HAZARD_WALL);

    // Diamond Plate
    public static final DeferredItem<BlockItem> DIAMOND_PLATE = registerBlockItem("diamond_plate", ModBlocks.DIAMOND_PLATE);
    public static final DeferredItem<BlockItem> DIAMOND_PLATE_STAIRS = registerBlockItem("diamond_plate_stairs", ModBlocks.DIAMOND_PLATE_STAIRS);
    public static final DeferredItem<BlockItem> DIAMOND_PLATE_SLAB = registerBlockItem("diamond_plate_slab", ModBlocks.DIAMOND_PLATE_SLAB);
    public static final DeferredItem<BlockItem> DIAMOND_PLATE_WALL = registerBlockItem("diamond_plate_wall", ModBlocks.DIAMOND_PLATE_WALL);

    // Riveted Sheet Metal
    public static final DeferredItem<BlockItem> RIVETED_SHEET_METAL = registerBlockItem("riveted_sheet_metal", ModBlocks.RIVETED_SHEET_METAL);
    public static final DeferredItem<BlockItem> RIVETED_SHEET_METAL_STAIRS = registerBlockItem("riveted_sheet_metal_stairs", ModBlocks.RIVETED_SHEET_METAL_STAIRS);
    public static final DeferredItem<BlockItem> RIVETED_SHEET_METAL_SLAB = registerBlockItem("riveted_sheet_metal_slab", ModBlocks.RIVETED_SHEET_METAL_SLAB);
    public static final DeferredItem<BlockItem> RIVETED_SHEET_METAL_WALL = registerBlockItem("riveted_sheet_metal_wall", ModBlocks.RIVETED_SHEET_METAL_WALL);

    // Factory Vent
    public static final DeferredItem<BlockItem> FACTORY_VENT = registerBlockItem("factory_vent", ModBlocks.FACTORY_VENT);

    // Industrial Grate
    public static final DeferredItem<BlockItem> INDUSTRIAL_GRATE = registerBlockItem("industrial_grate", ModBlocks.INDUSTRIAL_GRATE);
}
