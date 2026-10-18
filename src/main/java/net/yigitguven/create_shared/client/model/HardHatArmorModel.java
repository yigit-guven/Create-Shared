package net.yigitguven.create_shared.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.yigitguven.create_shared.client.CreateSharedClient;

@OnlyIn(Dist.CLIENT)
public class HardHatArmorModel extends HumanoidModel<LivingEntity> {
    private static HardHatArmorModel instance;

    public static HardHatArmorModel getModel() {
        if (instance == null && Minecraft.getInstance().getEntityModels() != null) {
            ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(CreateSharedClient.HARD_HAT_LAYER);
            instance = new HardHatArmorModel(root);
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    public HardHatArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.0F), 0.0F);
        PartDefinition root = mesh.getRoot();

        // Attach custom hard hat components directly to the head bone
        root.addOrReplaceChild("head", CubeListBuilder.create()
            // 1. Main Dome (Crown): covers top of head down to brow line, sits outside hair (10x5x10)
            .texOffs(0, 0)
            .addBox(-5.0F, -9.5F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
            // 2. Front Visor: extended peak projecting over forehead and eyes (10x1x2)
            .texOffs(0, 16)
            .addBox(-5.0F, -5.0F, -7.0F, 10.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            // 3. Left Brim Flange: outer side rim along left ear (1x1x11)
            .texOffs(0, 20)
            .addBox(5.0F, -5.0F, -5.0F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
            // 4. Right Brim Flange: outer side rim along right ear (1x1x11)
            .texOffs(0, 20)
            .addBox(-6.0F, -5.0F, -5.0F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
            // 5. Back Brim Flange: connects side rims behind neck (10x1x1)
            .texOffs(26, 16)
            .addBox(-5.0F, -5.0F, 5.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            // 6. Central Reinforcement Ridge (Top Crest Spine) (2x1x10)
            .texOffs(0, 33)
            .addBox(-1.0F, -10.25F, -5.0F, 2.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
            // 7. Left Reinforcement Ridge (1x1x8)
            .texOffs(26, 20)
            .addBox(2.0F, -10.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            // 8. Right Reinforcement Ridge (1x1x8)
            .texOffs(26, 20)
            .addBox(-3.0F, -10.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            // 9. Side Accessory Brackets (Left & Right) (1x2x2)
            .texOffs(0, 46)
            .addBox(5.0F, -7.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 46)
            .addBox(-6.0F, -7.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.ZERO);

        // Clear armor hat layer so it doesn't render default helmet cube
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }
}
