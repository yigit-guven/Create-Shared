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

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ResourceLocation ventId = ModBlocks.FACTORY_VENT.getId();
        ResourceLocation ductId = ModBlocks.FACTORY_VENT_DUCT.getId();

        helper.forComponents(ventId)
                .addStoryBoard("vent/gate", VentScenes::ventGate)
                .addStoryBoard("vent/network", VentScenes::ventDuctNetwork);

        helper.forComponents(ductId)
                .addStoryBoard("vent/network", VentScenes::ventDuctNetwork)
                .addStoryBoard("vent/gate", VentScenes::ventGate);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        // Can tag components if desired
    }
}
