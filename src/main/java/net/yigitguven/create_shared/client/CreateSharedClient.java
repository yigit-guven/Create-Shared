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

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.yigitguven.create_shared.registry.ModBlocks;

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
        modEventBus.addListener(CreateSharedClient::onClientSetup);
        modEventBus.addListener(CreateSharedClient::onRegisterAdditionalModels);
        modEventBus.addListener(EventPriority.LOWEST, CreateSharedClient::onModifyBakingResult);
        modEventBus.addListener(CreateSharedClient::onRegisterItemColors);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.INDUSTRIAL_GRATE.get(), RenderType.cutout());
        });
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

    public static boolean isRainbowStack(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (net.yigitguven.create_shared.registry.ModCreativeTabs.RAINBOW_GOGGLES_ICON != null
                && stack == net.yigitguven.create_shared.registry.ModCreativeTabs.RAINBOW_GOGGLES_ICON) {
            return true;
        }
        net.minecraft.world.item.component.CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null && (customData.contains("CreativeTabIconRainbow") || customData.contains("Rainbow"));
    }

    public static int getRainbowColor() {
        long time = System.currentTimeMillis();
        float hue = (time % 4000L) / 4000.0f;
        return hsvToRgb(hue, 0.85f, 1.0f);
    }

    private static int hsvToRgb(float h, float s, float v) {
        int hInt = (int) (h * 6.0f);
        float f = h * 6.0f - hInt;
        float p = v * (1.0f - s);
        float q = v * (1.0f - f * s);
        float t = v * (1.0f - (1.0f - f) * s);
        float r = 0, g = 0, b = 0;
        switch (hInt % 6) {
            case 0 -> { r = v; g = t; b = p; }
            case 1 -> { r = q; g = v; b = p; }
            case 2 -> { r = p; g = v; b = t; }
            case 3 -> { r = p; g = q; b = v; }
            case 4 -> { r = t; g = p; b = v; }
            case 5 -> { r = v; g = p; b = q; }
        }
        int red = Math.min(255, Math.max(0, (int) (r * 255.0f)));
        int green = Math.min(255, Math.max(0, (int) (g * 255.0f)));
        int blue = Math.min(255, Math.max(0, (int) (b * 255.0f)));
        return FastColor.ARGB32.color(255, red, green, blue);
    }

    private static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        // Wrench handle tinting
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                if (isRainbowStack(stack)) {
                    return getRainbowColor();
                }
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
                if (isRainbowStack(stack)) {
                    return getRainbowColor();
                }
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
