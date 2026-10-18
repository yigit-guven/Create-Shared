package net.yigitguven.create_shared;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllItems;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.yigitguven.create_shared.client.CreateSharedClient;
import net.yigitguven.create_shared.registry.ModArmorMaterials;
import net.yigitguven.create_shared.registry.ModBlocks;
import net.yigitguven.create_shared.registry.ModCreativeTabs;
import net.yigitguven.create_shared.registry.ModItems;

@Mod(CreateShared.MODID)
public class CreateShared {
    public static final String MODID = "create_shared";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateShared(IEventBus modEventBus, ModContainer modContainer) {
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModCreativeTabs::addCreative);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            CreateSharedClient.init(modEventBus);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CauldronInteraction cleanDyed = (state, level, pos, player, hand, stack) -> {
                if (!stack.has(DataComponents.DYED_COLOR)) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                if (!level.isClientSide()) {
                    stack.remove(DataComponents.DYED_COLOR);
                    player.awardStat(Stats.CLEAN_ARMOR);
                    LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            };

            CauldronInteraction.WATER.map().put(AllItems.WRENCH.get(), cleanDyed);
            CauldronInteraction.WATER.map().put(AllItems.GOGGLES.get(), cleanDyed);
            CauldronInteraction.WATER.map().put(ModItems.HARD_HAT.get(), cleanDyed);
        });
    }
}
