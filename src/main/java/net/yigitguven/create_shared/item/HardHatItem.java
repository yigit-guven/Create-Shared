package net.yigitguven.create_shared.item;

import java.util.function.Consumer;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.yigitguven.create_shared.CreateShared;
import net.yigitguven.create_shared.client.HardHatClientExtensions;

public class HardHatItem extends ArmorItem {
    public static final int DEFAULT_HARD_HAT_COLOR = 0xFFF4C430; // Classic Construction Safety Yellow

    public HardHatItem(Holder<ArmorMaterial> material, Properties properties) {
        super(material, Type.HELMET, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(HardHatClientExtensions.INSTANCE);
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.fromNamespaceAndPath(CreateShared.MODID, "textures/models/armor/hard_hat_layer_1.png");
    }
}
