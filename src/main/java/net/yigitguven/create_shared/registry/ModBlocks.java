package net.yigitguven.create_shared.registry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yigitguven.create_shared.CreateShared;
import net.yigitguven.create_shared.block.FactoryBlock;
import net.yigitguven.create_shared.block.FactorySlabBlock;
import net.yigitguven.create_shared.block.FactoryStairsBlock;
import net.yigitguven.create_shared.block.FactoryVentBlock;
import net.yigitguven.create_shared.block.FactoryVentDuctBlock;
import net.yigitguven.create_shared.block.FactoryWallBlock;
import net.yigitguven.create_shared.block.HazardPillarBlock;
import net.yigitguven.create_shared.block.IndustrialGrateBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateShared.MODID);

    private static BlockBehaviour.Properties industrialMetalProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
            .mapColor(color)
            .requiresCorrectToolForDrops()
            .strength(2.5F, 6.0F)
            .sound(SoundType.NETHERITE_BLOCK);
    }

    // --- Hazard (Yellow / Black Caution) ---
    public static final DeferredBlock<HazardPillarBlock> HAZARD_BLOCK = BLOCKS.registerBlock("hazard_block",
        HazardPillarBlock::new, industrialMetalProperties(MapColor.COLOR_YELLOW));

    public static final DeferredBlock<FactoryStairsBlock> HAZARD_STAIRS = BLOCKS.registerBlock("hazard_stairs",
        props -> new FactoryStairsBlock(HAZARD_BLOCK.get().defaultBlockState(), props),
        industrialMetalProperties(MapColor.COLOR_YELLOW));

    public static final DeferredBlock<FactorySlabBlock> HAZARD_SLAB = BLOCKS.registerBlock("hazard_slab",
        FactorySlabBlock::new, industrialMetalProperties(MapColor.COLOR_YELLOW));

    public static final DeferredBlock<FactoryWallBlock> HAZARD_WALL = BLOCKS.registerBlock("hazard_wall",
        FactoryWallBlock::new, industrialMetalProperties(MapColor.COLOR_YELLOW));

    // --- Emergency Hazard (Red / White Warning) ---
    public static final DeferredBlock<HazardPillarBlock> EMERGENCY_HAZARD_BLOCK = BLOCKS.registerBlock("emergency_hazard_block",
        HazardPillarBlock::new, industrialMetalProperties(MapColor.COLOR_RED));

    public static final DeferredBlock<FactoryStairsBlock> EMERGENCY_HAZARD_STAIRS = BLOCKS.registerBlock("emergency_hazard_stairs",
        props -> new FactoryStairsBlock(EMERGENCY_HAZARD_BLOCK.get().defaultBlockState(), props),
        industrialMetalProperties(MapColor.COLOR_RED));

    public static final DeferredBlock<FactorySlabBlock> EMERGENCY_HAZARD_SLAB = BLOCKS.registerBlock("emergency_hazard_slab",
        FactorySlabBlock::new, industrialMetalProperties(MapColor.COLOR_RED));

    public static final DeferredBlock<FactoryWallBlock> EMERGENCY_HAZARD_WALL = BLOCKS.registerBlock("emergency_hazard_wall",
        FactoryWallBlock::new, industrialMetalProperties(MapColor.COLOR_RED));

    // --- Diamond Plate ---
    public static final DeferredBlock<FactoryBlock> DIAMOND_PLATE = BLOCKS.registerBlock("diamond_plate",
        FactoryBlock::new, industrialMetalProperties(MapColor.METAL));

    public static final DeferredBlock<FactoryStairsBlock> DIAMOND_PLATE_STAIRS = BLOCKS.registerBlock("diamond_plate_stairs",
        props -> new FactoryStairsBlock(DIAMOND_PLATE.get().defaultBlockState(), props),
        industrialMetalProperties(MapColor.METAL));

    public static final DeferredBlock<FactorySlabBlock> DIAMOND_PLATE_SLAB = BLOCKS.registerBlock("diamond_plate_slab",
        FactorySlabBlock::new, industrialMetalProperties(MapColor.METAL));

    public static final DeferredBlock<FactoryWallBlock> DIAMOND_PLATE_WALL = BLOCKS.registerBlock("diamond_plate_wall",
        FactoryWallBlock::new, industrialMetalProperties(MapColor.METAL));

    // --- Riveted Sheet Metal ---
    public static final DeferredBlock<FactoryBlock> RIVETED_SHEET_METAL = BLOCKS.registerBlock("riveted_sheet_metal",
        FactoryBlock::new, industrialMetalProperties(MapColor.METAL));

    public static final DeferredBlock<FactoryStairsBlock> RIVETED_SHEET_METAL_STAIRS = BLOCKS.registerBlock("riveted_sheet_metal_stairs",
        props -> new FactoryStairsBlock(RIVETED_SHEET_METAL.get().defaultBlockState(), props),
        industrialMetalProperties(MapColor.METAL));

    public static final DeferredBlock<FactorySlabBlock> RIVETED_SHEET_METAL_SLAB = BLOCKS.registerBlock("riveted_sheet_metal_slab",
        FactorySlabBlock::new, industrialMetalProperties(MapColor.METAL));

    public static final DeferredBlock<FactoryWallBlock> RIVETED_SHEET_METAL_WALL = BLOCKS.registerBlock("riveted_sheet_metal_wall",
        FactoryWallBlock::new, industrialMetalProperties(MapColor.METAL));

    // --- Factory Vent ---
    public static final DeferredBlock<FactoryVentBlock> FACTORY_VENT = BLOCKS.registerBlock("factory_vent",
        FactoryVentBlock::new, industrialMetalProperties(MapColor.METAL).noOcclusion());

    // --- Factory Vent Duct ---
    public static final DeferredBlock<FactoryVentDuctBlock> FACTORY_VENT_DUCT = BLOCKS.registerBlock("factory_vent_duct",
        FactoryVentDuctBlock::new, industrialMetalProperties(MapColor.METAL).noOcclusion());

    // --- Industrial Grate ---
    public static final DeferredBlock<IndustrialGrateBlock> INDUSTRIAL_GRATE = BLOCKS.registerBlock("industrial_grate",
        IndustrialGrateBlock::new, industrialMetalProperties(MapColor.METAL).noOcclusion());
}
