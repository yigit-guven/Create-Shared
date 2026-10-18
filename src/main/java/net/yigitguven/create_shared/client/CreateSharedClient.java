package net.yigitguven.create_shared.client;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.goggles.GogglesModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.yigitguven.create_shared.CreateShared;

public class CreateSharedClient {

    // Note: Colors MUST be fully opaque ARGB (0xFF alpha)!
    public static final int DEFAULT_WRENCH_HANDLE_COLOR = 0xFF704E31;
    public static final int DEFAULT_GOGGLES_STRAP_COLOR = 0xFF805436;

    public static final ModelResourceLocation GOGGLES_ITEM_MODEL =
        ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(CreateShared.MODID, "item/goggles"));
    public static final ModelResourceLocation GOGGLES_HEAD_MODEL =
        ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(CreateShared.MODID, "block/goggles"));
    public static final ModelResourceLocation WRENCH_ITEM_MODEL =
        ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(CreateShared.MODID, "item/wrench/item"));

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(CreateSharedClient::onRegisterAdditionalModels);
        modEventBus.addListener(EventPriority.LOWEST, CreateSharedClient::onModifyBakingResult);
        modEventBus.addListener(CreateSharedClient::onRegisterItemColors);
    }

    private static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(GOGGLES_ITEM_MODEL);
        event.register(GOGGLES_HEAD_MODEL);
        event.register(WRENCH_ITEM_MODEL);
    }

    private static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        BakedModel ourGogglesHead = event.getModels().get(GOGGLES_HEAD_MODEL);
        BakedModel ourGogglesItem = event.getModels().get(GOGGLES_ITEM_MODEL);
        BakedModel ourWrenchItem = event.getModels().get(WRENCH_ITEM_MODEL);

        // 1. Swap Create's standalone block goggles model so Flywheel's AllPartialModels.GOGGLES uses our tinted model
        if (ourGogglesHead != null) {
            ModelResourceLocation createGogglesHeadLoc =
                ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("create", "block/goggles"));
            event.getModels().put(createGogglesHeadLoc, ourGogglesHead);
        }

        // 2. Wrap our 2D item model with Create's GogglesModel so it switches to our head model when worn on head
        if (ourGogglesItem != null) {
            ModelResourceLocation createGogglesItemLoc =
                new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("create", "goggles"), "inventory");
            event.getModels().put(createGogglesItemLoc, new GogglesModel(ourGogglesItem));
        }

        // 3. Wrap our wrench model with Create's CustomRenderedItemModel so WrenchItemRenderer renders our tinted base
        if (ourWrenchItem != null) {
            ModelResourceLocation createWrenchItemLoc =
                new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("create", "wrench"), "inventory");
            event.getModels().put(createWrenchItemLoc, new CustomRenderedItemModel(ourWrenchItem));
        }
    }

    private static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        // Wrench handle tinting
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                DyedItemColor dyedColor = stack.get(DataComponents.DYED_COLOR);
                if (dyedColor != null) {
                    return FastColor.ARGB32.opaque(dyedColor.rgb());
                }
                return DEFAULT_WRENCH_HANDLE_COLOR;
            }
            return -1;
        }, AllItems.WRENCH.get());

        // Goggles strap tinting (layer0 on 2D item, element 8 on 3D head model)
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                DyedItemColor dyedColor = stack.get(DataComponents.DYED_COLOR);
                if (dyedColor != null) {
                    return FastColor.ARGB32.opaque(dyedColor.rgb());
                }
                return DEFAULT_GOGGLES_STRAP_COLOR;
            }
            return -1;
        }, AllItems.GOGGLES.get());
    }
}
