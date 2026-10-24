package warhammermod.Client.Render.Entity.Renders.Models;


import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import warhammermod.Entities.Living.SkavenEntity;


/**
 * Created using Tabula 7.0.1
 */
@Environment(EnvType.CLIENT)
public class SkavenModel<T extends SkavenEntity> extends HumanoidModel<T> {
    public ModelPart bipedLeftArm;
    public ModelPart bipedRightLeg;
    public ModelPart bipedHead;
    public ModelPart bipedBody;
    public ModelPart bipedRightArm;
    public ModelPart bipedLeftLeg;

    public ModelPart tail1;
    public ModelPart tail2;
    public ModelPart tailend;

    public ModelPart chestplate;
    public ModelPart Armplate;
    public ModelPart legplate;



    public SkavenModel(ModelPart model) {
        super(model);
        this.bipedBody = model.getChild("body");
        this.bipedHead = model.getChild("head");
        this.bipedLeftArm = model.getChild("left_arm");
        this.bipedRightArm =model.getChild("right_arm");
        this.bipedLeftLeg = model.getChild("left_leg");
        this.bipedRightLeg = model.getChild("right_leg");
        this.tail1 = model.getChild("tail1");
        this.tail2 = tail1.getChild("tail2");
        this.tailend = tail2.getChild("tailend");
        //this.chestplate = bipedBody.getChild("chestplate");
        //this.Armplate = bipedLeftArm.getChild("armplatesl");
        //this.legplate = bipedLeftLeg.getChild("legplatel");
    }

    public static LayerDefinition createBodyLayer() {
        CubeDeformation cubeDeformation = CubeDeformation.NONE;
        MeshDefinition meshDefinition = createMesh(cubeDeformation, 0.0F);
        PartDefinition partDefinition = meshDefinition.getRoot();
        CubeDeformation f_04 = new CubeDeformation(-0.4F);
        CubeDeformation f_03 = new CubeDeformation(-0.3F);
        CubeDeformation f_02 = new CubeDeformation(-0.2F);
        CubeDeformation f_01 = new CubeDeformation(-0.1F);
        CubeDeformation f01 = new CubeDeformation(0.1F);
        CubeDeformation f02 = new CubeDeformation(0.2F);

        PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(20, 0).addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3), PartPose.offset(0.0F, 3.4F, -6.0F));
        PartDefinition mouthup = head.addOrReplaceChild("mouthup", CubeListBuilder.create().texOffs(35, 0).addBox(-1.5F, -2.0F, -2.5F, 3, 2, 3, f_03), PartPose.offset(0.0F, -1.1F, -1.7F));
        head.addOrReplaceChild("mouthdown", CubeListBuilder.create().texOffs(20, 8).addBox(-1.5F, -2.0F, -2.5F, 3, 2, 3, f_04), PartPose.offset(0.0F, 0.2F, -1.6F));
        head.addOrReplaceChild("earleft", CubeListBuilder.create().texOffs(50, 0).addBox(-0.5F, -2.0F, -0.5F, 1, 2, 1, f_02), PartPose.offset(1.4F, -1.5F, 0.7F));
        head.addOrReplaceChild("earright", CubeListBuilder.create().texOffs(50, 0).addBox(-0.5F, -2.0F, -0.5F, 1, 2, 1, f_02), PartPose.offset(-1.4F, -1.5F, 0.7F));
        PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -1.5F, 6, 10, 3), PartPose.offsetAndRotation(0.0F, 2.5F, -3.5F,0.31869712141416456F, 0.0F, 0.0F));
        PartDefinition rightarm =partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, -1.0F, -0.5F, 1, 10, 1,f01), PartPose.offset(-3.5F, 4.5F, -3.4F));
        PartDefinition leftarm =partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, -1.0F, -0.5F, 1, 10, 1,f01), PartPose.offset(3.5F, 4.5F, -3.4F));
        PartDefinition leftleg =partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 30).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2), PartPose.offset(2.0F, 12.0F, -1.0F));
        PartDefinition rightleg =partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 30).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2), PartPose.offset(-2.0F, 12.0F, -1.0F));
        PartDefinition tail = partDefinition.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(10,16).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 8,f_02), PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F,-0.4553564018453205F, 0.0F, 0.0F));
        PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(10, 30).addBox(-1.0F, -1.0F, -0.7F, 2, 2, 7,f_03), PartPose.offset(0.0F, 0F, 6.9F));
        tail2.addOrReplaceChild("tailend", CubeListBuilder.create().texOffs(10, 45).addBox(-0.5F, -0.5F, -0.5F, 1, 1, 7), PartPose.offset(0.0F, 0F, 6F));

        body.addOrReplaceChild("chestplate", CubeListBuilder.create().texOffs(35, 10).addBox(-3F, 0.0F, -1.5F, 6, 10, 3,f01), PartPose.ZERO);
        leftarm.addOrReplaceChild("armplatesl", CubeListBuilder.create().texOffs(35, 24).addBox(-0.5F, -2.0F, -0.5F, 1, 10, 1,f02), PartPose.ZERO);
        rightarm.addOrReplaceChild("armplatesr", CubeListBuilder.create().texOffs(35, 24).addBox(-0.5F, -2.0F, -0.5F, 1, 10, 1,f02), PartPose.ZERO);
        leftleg.addOrReplaceChild("legplatel", CubeListBuilder.create().texOffs(35, 36).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2,f01), PartPose.ZERO);
        rightleg.addOrReplaceChild("legplater", CubeListBuilder.create().texOffs(35, 36).addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2,f01), PartPose.ZERO);
        head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(44, 24).addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, f01), PartPose.ZERO);
        mouthup.addOrReplaceChild("helmetfront", CubeListBuilder.create().texOffs(44,30).addBox(-1.5F, -2.0F, -2.5F, 3, 2, 3, f_02), PartPose.ZERO);
        head.addOrReplaceChild("crest", CubeListBuilder.create().texOffs(44,35).addBox(-0.5F, -5F, -1.5F, 1, 2, 3, f_01), PartPose.ZERO);
        body.addOrReplaceChild("pagne", CubeListBuilder.create().texOffs(35, 50).addBox(-3.0F, 9F, 1.5F, 6, 5, 3,f01), PartPose.offsetAndRotation(0,0,0,-0.31869712141416456F, 0.0F, 0.0F));
        mouthup.addOrReplaceChild("helmetfrontfull",CubeListBuilder.create().texOffs(0, 55).addBox(-1.5F, -2.1F, -0.8F, 3, 3, 3,f_01), PartPose.offset(0.0F, 0.2F, -1.6F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }


    public static float pitch = -2;
    public static float yaw = 0;
    public void setAngles(T entityIn,float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.hat.visible=false;
        SkavenEntity.State state = entityIn.getState();
        this.bipedHead.yRot = netHeadYaw * 0.017453292F;
        this.bipedHead.xRot = headPitch * 0.017453292F;
        if (state.equals(SkavenEntity.State.ATTACKING)) {
            float f = Mth.sin(this.attackTime * (float) Math.PI);
            float f1 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float) Math.PI);
            this.bipedRightArm.zRot = 0.0F;
            this.bipedLeftArm.zRot = 0.0F;
            this.bipedRightArm.yRot = -(0.1F - f * 0.6F);
            this.bipedLeftArm.yRot = 0.1F - f * 0.6F;
            this.bipedRightArm.xRot = -((float) Math.PI / 2.2F);
            this.bipedLeftArm.xRot = -((float) Math.PI / 2.2F);
            this.bipedRightArm.xRot -= f * 1.2F - f1 * 0.4F;
            this.bipedLeftArm.xRot -= f * 1.2F - f1 * 0.4F;
            this.bipedRightArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.bipedLeftArm.zRot -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.bipedRightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
            this.bipedLeftArm.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.05F;

        }else if(state.equals(SkavenEntity.State.AIMING)){
            hold(this.bipedRightArm,this.bipedLeftArm,this.bipedHead,!entityIn.isLeftHanded());
        }else if(state.equals(SkavenEntity.State.GUN_CHARGE)){
            bipedHead.xRot=0F;
            bipedHead.yRot=0;
            bipedRightArm.zRot=0F;
            bipedRightArm.zRot += Mth.cos(ageInTicks*1.1F)*0.01F;
            bipedLeftArm.zRot=0F;
            bipedLeftArm.zRot -= Mth.cos(ageInTicks*1.1F)*0.01F;
            if(entityIn.getMainArm().equals(HumanoidArm.RIGHT)){
                bipedLeftArm.yRot=0.5F;
                bipedRightArm.yRot=0F;
                bipedRightArm.xRot=-0.5F;
                bipedLeftArm.xRot=-1.5F + Mth.cos(ageInTicks*0.8F)*0.05F;
            }else{
                bipedRightArm.yRot=-0.5F;
                bipedLeftArm.yRot=0F;
                bipedLeftArm.xRot=-0.5F;
                bipedRightArm.xRot=-1.5F + Mth.cos(ageInTicks*0.8F)*0.05F;
            }
            bipedHead.xRot += Mth.cos(ageInTicks*1.1F)*0.03F;
        }
        else{
            bipedHead.xRot=0F;
            bipedHead.yRot=0;
            bipedRightArm.zRot=0F;
            bipedRightArm.zRot += Mth.cos(ageInTicks*1.1F)*0.04F;
            bipedLeftArm.zRot=0F;
            bipedLeftArm.zRot -= Mth.cos(ageInTicks*1.1F)*0.04F;
            bipedLeftArm.yRot=0F;
            bipedRightArm.yRot=0F;
            bipedRightArm.xRot=0F;
            bipedLeftArm.xRot=0F;
            bipedHead.xRot += Mth.cos(ageInTicks*1.1F)*0.04F;
        }


        this.bipedRightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
        this.bipedLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI)  * limbSwingAmount;
        if(limbSwing<0.05) {
            tail1.yRot = Mth.cos(ageInTicks * 0.3F) * 0.2F;
            tail2.yRot = Mth.cos(ageInTicks * 0.3F) * 0.15F;
            tailend.yRot = Mth.cos(ageInTicks * 0.3F) * 0.15F;
        }else{
            tail1.yRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount*0.8F;
            tail2.yRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount*0.8F;
            tailend.yRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount*0.8F;

        }
        this.bipedRightLeg.yRot = 0.0F;
        this.bipedLeftLeg.yRot = 0.0F;
        this.bipedRightLeg.zRot = 0.0F;
        this.bipedLeftLeg.zRot = 0.0F;


    }

    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        float f = humanoidArm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        ModelPart modelPart = this.getArm(humanoidArm);
        modelPart.x += f;
        modelPart.translateAndRotate(poseStack);
        modelPart.x -= f;
    }

    public static void hold(ModelPart holdingArm, ModelPart otherArm, ModelPart head, boolean rightArmed) {
        ModelPart modelPart = rightArmed ? holdingArm : otherArm;
        ModelPart modelPart2 = rightArmed ? otherArm : holdingArm;
        modelPart.yRot = (rightArmed ? -0.01f : 0.01f) + head.yRot;
        modelPart2.yRot = (rightArmed ? 0.6f : -0.6f) + head.yRot;
        modelPart.xRot = -1.5707964f + head.xRot + 0.1f;
        modelPart2.xRot = -1.5f + head.xRot;
    }




/*
    public void func_225599_a_(HandSide p_225599_1_, MatrixStack p_225599_2_) {
        float f = p_225599_1_ == HandSide.RIGHT ? 1.0F : -1.0F;
        ModelPart modelrenderer = this.getArmForSide(p_225599_1_);
        modelrenderer.rotationPointX += f;
        modelrenderer.func_228307_a_(p_225599_2_);
        modelrenderer.rotationPointX -= f;
    }

*/

}
