package net.yigitguven.create_shared.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.yigitguven.create_shared.client.model.HardHatArmorModel;
import net.yigitguven.create_shared.item.HardHatItem;

@OnlyIn(Dist.CLIENT)
public class HardHatClientExtensions implements IClientItemExtensions {
    public static final HardHatClientExtensions INSTANCE = new HardHatClientExtensions();

    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        if (equipmentSlot == EquipmentSlot.HEAD) {
            HardHatArmorModel model = HardHatArmorModel.getModel();
            if (model != null) {
                ClientHooks.copyModelProperties(original, model);
                return model;
            }
        }
        return original;
    }

    @Override
    public int getDefaultDyeColor(ItemStack stack) {
        return HardHatItem.DEFAULT_HARD_HAT_COLOR;
    }

    @Override
    public int getArmorLayerTintColor(ItemStack stack, LivingEntity entity, ArmorMaterial.Layer layer, int layerIdx, int fallbackColor) {
        if (layer.dyeable()) {
            if (CreateSharedClient.isRainbowStack(stack)) {
                return CreateSharedClient.getRainbowColor();
            }
            DyedItemColor dyedColor = stack.get(DataComponents.DYED_COLOR);
            if (dyedColor != null) {
                return FastColor.ARGB32.opaque(dyedColor.rgb());
            }
            return HardHatItem.DEFAULT_HARD_HAT_COLOR;
        }
        return 0xFFFFFFFF;
    }
}
