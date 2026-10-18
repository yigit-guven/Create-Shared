package net.yigitguven.create_shared.event;

import com.simibubi.create.AllDamageTypes;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.yigitguven.create_shared.CreateShared;
import net.yigitguven.create_shared.registry.ModItems;

@EventBusSubscriber(modid = CreateShared.MODID)
public class HardHatEvents {

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (!headItem.is(ModItems.HARD_HAT.get())) {
            return;
        }

        DamageSource source = event.getSource();

        // 1. 100% Falling Block, Falling Anvil, and Falling Stalactite Immunity & Deflection
        if (source.is(DamageTypes.FALLING_ANVIL) || source.is(DamageTypes.FALLING_BLOCK) || source.is(DamageTypes.FALLING_STALACTITE)) {
            event.setCanceled(true);
            Level level = entity.level();
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.8F, 1.6F);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.1F);

            if (entity instanceof ServerPlayer serverPlayer) {
                headItem.hurtAndBreak(3, serverPlayer, EquipmentSlot.HEAD);
            }
            return;
        }

        // 2. 75% Damage Reduction against Kinetic Crushing, Run Over, In-Wall Suffocation, and Cramming
        if (source.is(AllDamageTypes.CRUSH) || source.is(AllDamageTypes.RUN_OVER)
                || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FLY_INTO_WALL) || source.is(DamageTypes.CRAMMING)) {
            float originalDamage = event.getAmount();
            event.setAmount(originalDamage * 0.25F);

            Level level = entity.level();
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ARMOR_EQUIP_IRON.value(), SoundSource.PLAYERS, 1.0F, 1.2F);

            if (entity instanceof ServerPlayer serverPlayer && originalDamage >= 4.0F) {
                headItem.hurtAndBreak(1, serverPlayer, EquipmentSlot.HEAD);
            }
        }
    }
}
