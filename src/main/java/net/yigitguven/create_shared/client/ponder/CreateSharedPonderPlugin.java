package net.yigitguven.create_shared.client.ponder;

import com.tterrag.registrate.util.entry.ItemProviderEntry;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.yigitguven.create_shared.CreateShared;
import net.yigitguven.create_shared.registry.ModBlocks;

public class CreateSharedPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return CreateShared.MODID;
    }

    public static final ResourceLocation VENTILATION_TAG = ResourceLocation.fromNamespaceAndPath(CreateShared.MODID, "ventilation");

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ResourceLocation ventId = ModBlocks.FACTORY_VENT.getId();
        ResourceLocation ductId = ModBlocks.FACTORY_VENT_DUCT.getId();

        helper.forComponents(ventId)
                .addStoryBoard("vent/gate", VentScenes::ventGate, VENTILATION_TAG)
                .addStoryBoard("vent/network", VentScenes::ventDuctNetwork, VENTILATION_TAG);

        helper.forComponents(ductId)
                .addStoryBoard("vent/network", VentScenes::ventDuctNetwork, VENTILATION_TAG)
                .addStoryBoard("vent/gate", VentScenes::ventGate, VENTILATION_TAG);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.registerTag(VENTILATION_TAG)
                .addToIndex()
                .item(ModBlocks.FACTORY_VENT.get(), true, false)
                .title("Factory Ventilation")
                .description("Hollow exhaust vents and multi-directional ductwork for crawl tunnels and air circulation")
                .register();

        helper.addToTag(VENTILATION_TAG)
                .add(ModBlocks.FACTORY_VENT.getId())
                .add(ModBlocks.FACTORY_VENT_DUCT.getId())
                .add(com.simibubi.create.AllBlocks.ENCASED_FAN.getId());
    }
}
