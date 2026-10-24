package warhammermod.Entities.Living.Renders.Models;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;

public class SkavenModel extends HumanoidModel<warhammermod.Entities.Living.Renders.SkavenRenderer.SkavenRenderState> {
    public ModelPart tail1;
    public ModelPart tail2;
    public ModelPart tailend;

    public SkavenModel(ModelPart model) {
        super(model);
        this.tail1 = model.getChild("tail1");
        this.tail2 = tail1.getChild("tail2");
        this.tailend = tail2.getChild("tailend");
    }

    public static LayerDefinition createBodyLayer() {
        CubeDeformation f_04 = new CubeDeformation(-0.4F);
        CubeDeformation f_03 = new CubeDeformation(-0.3F);
        CubeDeformation f_02 = new CubeDeformation(-0.2F);
        CubeDeformation f_01 = new CubeDeformation(-0.1F);
        CubeDeformation f01 = new CubeDeformation(0.1F);
        CubeDeformation f02 = new CubeDeformation(0.2F);

        MeshDefinition meshDefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(20, 0).addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3), PartPose.offset(0.0F, 3.4F, -6.0F));
        PartDefinition mouthup = head.addOrReplaceChild("mouthup", CubeListBuilder.create().texOffs(35, 0).addBox(-1.5F, -2.0F, -2.5F, 3, 2, 3, f_03), PartPose.offset(0.0F, -1.1F, -1.7F));
        head.addOrReplaceChild("mouthdown", CubeListBuilder.create().texOffs(20, 8).addBox(-1.5F, -2.0F, -2.5F, 3, 2, 3, f_04), PartPose.offset(0.0F, 0.2F, -1.6F));
        head.addOrReplaceChild("earleft", CubeListBuilder.create().texOffs(50, 0).addBox(-0.5F, -2.0F, -0.5F, 1, 2, 1, f_02), PartPose.offset(1.4F, -1.5F, 0.7F));
        head.addOrReplaceChild("earright", CubeListBuilder.create().texOffs(50, 0).addBox(-0.5F, -2.0F, -0.5F, 1, 2, 1, f_02), PartPose.offset(-1.4F, -1.5F, 0.7F));
        PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -1.5F, 6, 10, 3), PartPose.offsetAndRotation(0.0F, 2.5F, -3.5F, 0.31869712141416456F, 0.0F, 0.0F));
        PartDefinition rightarm = partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, -1.0F, -0.5F, 1, 10, 1, f01), PartPose.offset(-3.5F, 4.5F, -3.4F));
        PartDefinition leftarm = partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, -1.0F, -0.5F, 1, 10, 1, f01), PartPose.offset(3.5F, 4.5F, -3.4F));
        PartDefinition leftleg = partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 30).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2), PartPose.offset(2.0F, 12.0F, -1.0F));
        PartDefinition rightleg = partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 30).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2), PartPose.offset(-2.0F, 12.0F, -1.0F));
        PartDefinition tail = partDefinition.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(10, 16).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 8, f_02), PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F, -0.4553564018453205F, 0.0F, 0.0F));
        PartDefinition tail2d = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(10, 30).addBox(-1.0F, -1.0F, -0.7F, 2, 2, 7, f_03), PartPose.offset(0.0F, 0F, 6.9F));
        tail2d.addOrReplaceChild("tailend", CubeListBuilder.create().texOffs(10, 45).addBox(-0.5F, -0.5F, -0.5F, 1, 1, 7), PartPose.offset(0.0F, 0F, 6F));

        body.addOrReplaceChild("chestplate", CubeListBuilder.create().texOffs(35, 10).addBox(-3F, 0.0F, -1.5F, 6, 10, 3, f01), PartPose.ZERO);
        leftarm.addOrReplaceChild("armplatesl", CubeListBuilder.create().texOffs(35, 24).addBox(-0.5F, -2.0F, -0.5F, 1, 10, 1, f02), PartPose.ZERO);
        rightarm.addOrReplaceChild("armplatesr", CubeListBuilder.create().texOffs(35, 24).addBox(-0.5F, -2.0F, -0.5F, 1, 10, 1, f02), PartPose.ZERO);
        leftleg.addOrReplaceChild("legplatel", CubeListBuilder.create().texOffs(35, 36).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f01), PartPose.ZERO);
        rightleg.addOrReplaceChild("legplater", CubeListBuilder.create().texOffs(35, 36).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f01), PartPose.ZERO);
        head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(44, 24).addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, f01), PartPose.ZERO);
        mouthup.addOrReplaceChild("helmetfront", CubeListBuilder.create().texOffs(44, 30).addBox(-1.5F, -2.0F, -2.5F, 3, 2, 3, f_02), PartPose.ZERO);
        head.addOrReplaceChild("crest", CubeListBuilder.create().texOffs(44, 35).addBox(-0.5F, -5F, -1.5F, 1, 2, 3, f_01), PartPose.ZERO);
        body.addOrReplaceChild("pagne", CubeListBuilder.create().texOffs(35, 50).addBox(-3.0F, 9F, 1.5F, 6, 5, 3, f01), PartPose.offsetAndRotation(0, 0, 0, -0.31869712141416456F, 0.0F, 0.0F));
        mouthup.addOrReplaceChild("helmetfrontfull", CubeListBuilder.create().texOffs(0, 55).addBox(-1.5F, -2.1F, -0.8F, 3, 3, 3, f_01), PartPose.offset(0.0F, 0.2F, -1.6F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(warhammermod.Entities.Living.Renders.SkavenRenderer.SkavenRenderState state) {
        super.setupAnim(state);
        float limbSwing = state.walkAnimationPos;
        float limbSwingAmount = state.walkAnimationSpeed;
        float ageInTicks = state.ageInTicks;

        this.hat.visible = false;

        float tailAnim = limbSwing < 0.05F ? Mth.cos(ageInTicks * 0.3F) * 0.2F : Mth.cos(limbSwing * 0.6662F) * limbSwingAmount * 0.8F;
        this.tail1.yRot = tailAnim;
        this.tail2.yRot = tailAnim;
        this.tailend.yRot = tailAnim;
    }
}
