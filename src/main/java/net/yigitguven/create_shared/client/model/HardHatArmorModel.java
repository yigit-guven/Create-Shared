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

        // Custom Hard Hat attached to the player head
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

        // 1. Dome (Crown)
        head.addOrReplaceChild("dome", CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.5F, -9.5F, -4.5F, 9.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.ZERO);

        // 2. Wide Brim & Extended Front Visor
        head.addOrReplaceChild("brim", CubeListBuilder.create()
            .texOffs(0, 15)
            .addBox(-5.5F, -5.2F, -7.0F, 11.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.ZERO);

        // 3. Central Reinforcement Ridge (Top Spine Crest)
        head.addOrReplaceChild("center_ridge", CubeListBuilder.create()
            .texOffs(0, 29)
            .addBox(-1.0F, -10.3F, -4.5F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.ZERO);

        // 4. Left Reinforcement Ridge
        head.addOrReplaceChild("left_ridge", CubeListBuilder.create()
            .texOffs(24, 29)
            .addBox(2.0F, -10.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.ZERO);

        // 5. Right Reinforcement Ridge
        head.addOrReplaceChild("right_ridge", CubeListBuilder.create()
            .texOffs(24, 29)
            .addBox(-3.0F, -10.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.ZERO);

        // 6. Side Accessory Mount Brackets
        head.addOrReplaceChild("bracket_left", CubeListBuilder.create()
            .texOffs(0, 40)
            .addBox(5.0F, -6.5F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.ZERO);
        head.addOrReplaceChild("bracket_right", CubeListBuilder.create()
            .texOffs(0, 40)
            .addBox(-6.0F, -6.5F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.ZERO);

        // Clear hat layer so it doesn't clip
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }
}
