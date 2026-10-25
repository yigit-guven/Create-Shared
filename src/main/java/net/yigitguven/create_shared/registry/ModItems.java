package net.yigitguven.create_shared.registry;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.SmithingTemplateItem;
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

    // Factory Vent & Duct
    public static final DeferredItem<BlockItem> FACTORY_VENT = registerBlockItem("factory_vent", ModBlocks.FACTORY_VENT);
    public static final DeferredItem<BlockItem> FACTORY_VENT_DUCT = registerBlockItem("factory_vent_duct", ModBlocks.FACTORY_VENT_DUCT);

    // Industrial Grate
    public static final DeferredItem<BlockItem> INDUSTRIAL_GRATE = registerBlockItem("industrial_grate", ModBlocks.INDUSTRIAL_GRATE);

    // Equipment
    public static final DeferredItem<net.yigitguven.create_shared.item.HardHatItem> HARD_HAT =
        ITEMS.registerItem("hard_hat", props -> new net.yigitguven.create_shared.item.HardHatItem(ModArmorMaterials.HARD_HAT, props.durability(385)));

    // Armor Trim Smithing Template
    public static final DeferredItem<SmithingTemplateItem> CREATE_ARMOR_TRIM_SMITHING_TEMPLATE =
        ITEMS.register("create_armor_trim_smithing_template",
            () -> new SmithingTemplateItem(
                Component.translatable("item.create_shared.smithing_template.create_armor_trim.applies_to").withStyle(ChatFormatting.BLUE),
                Component.translatable("item.create_shared.smithing_template.create_armor_trim.ingredients").withStyle(ChatFormatting.BLUE),
                Component.translatable("trim_pattern.create_shared.create").withStyle(ChatFormatting.GRAY),
                Component.translatable("item.minecraft.smithing_template.armor_trim.base_slot_description"),
                Component.translatable("item.minecraft.smithing_template.armor_trim.additions_slot_description"),
                List.of(
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots")
                ),
                List.of(
                    ResourceLocation.withDefaultNamespace("item/empty_slot_ingot"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_redstone_dust"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_quartz"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_diamond"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_emerald"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_amethyst_shard")
                )
            )
        );
}
