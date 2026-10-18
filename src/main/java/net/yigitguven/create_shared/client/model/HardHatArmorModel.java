package net.yigitguven.create_shared.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
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

    private final ModelPart dome;
    private final ModelPart accents;

    public HardHatArmorModel(ModelPart root) {
        super(root);
        this.dome = this.head.getChild("dome");
        this.accents = this.head.getChild("accents");
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        boolean isTrim = buffer instanceof SpriteCoordinateExpander;
        if (isTrim) {
            this.accents.visible = false;
        }
        super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, color);
        if (isTrim) {
            this.accents.visible = true;
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.0F), 0.0F);
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

        // 1. Main Dome (Crown): receives the trim pattern across the head
        head.addOrReplaceChild("dome", CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-5.0F, -9.5F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.ZERO);

        // 2. Accents: Visor, brim flanges, reinforcement ridges, side accessory brackets
        head.addOrReplaceChild("accents", CubeListBuilder.create()
            // Front Visor (10x1x2)
            .texOffs(0, 16)
            .addBox(-5.0F, -5.0F, -7.0F, 10.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            // Left Brim Flange (1x1x11)
            .texOffs(0, 20)
            .addBox(5.0F, -5.0F, -5.0F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
            // Right Brim Flange (1x1x11)
            .texOffs(0, 20)
            .addBox(-6.0F, -5.0F, -5.0F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
            // Back Brim Flange (10x1x1)
            .texOffs(26, 16)
            .addBox(-5.0F, -5.0F, 5.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            // Central Reinforcement Ridge (2x1x10)
            .texOffs(0, 33)
            .addBox(-1.0F, -10.25F, -5.0F, 2.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
            // Left Reinforcement Ridge (1x1x8)
            .texOffs(26, 20)
            .addBox(2.0F, -10.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            // Right Reinforcement Ridge (1x1x8)
            .texOffs(26, 20)
            .addBox(-3.0F, -10.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            // Side Accessory Brackets (Left & Right) (1x2x2)
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
