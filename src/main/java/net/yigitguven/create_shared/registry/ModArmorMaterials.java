package net.yigitguven.create_shared.registry;

import java.util.List;
import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yigitguven.create_shared.CreateShared;

public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
        DeferredRegister.create(Registries.ARMOR_MATERIAL, CreateShared.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> HARD_HAT =
        ARMOR_MATERIALS.register("hard_hat", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 3),
            15,
            SoundEvents.ARMOR_EQUIP_IRON,
            () -> Ingredient.of(Items.IRON_INGOT),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(CreateShared.MODID, "hard_hat"), "", true)),
            2.0F,
            0.1F
        ));
}
