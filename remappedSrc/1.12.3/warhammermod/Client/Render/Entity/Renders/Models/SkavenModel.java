package warhammermod.Client.Render.Entity.Renders.Models;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import warhammermod.Entities.Living.SkavenEntity;


/**
 * Created using Tabula 7.0.1
 */
@Environment(EnvType.CLIENT)
public class SkavenModel<T extends SkavenEntity> extends BipedEntityModel<T> {
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

    public static TexturedModelData createBodyLayer() {
        Dilation cubeDeformation = Dilation.NONE;
        ModelData meshDefinition = getModelData(cubeDeformation, 0.0F);
        ModelPartData partDefinition = meshDefinition.getRoot();
        Dilation f_04 = new Dilation(-0.4F);
        Dilation f_03 = new Dilation(-0.3F);
        Dilation f_02 = new Dilation(-0.2F);
        Dilation f_01 = new Dilation(-0.1F);
        Dilation f01 = new Dilation(0.1F);
        Dilation f02 = new Dilation(0.2F);

        ModelPartData head = partDefinition.addChild("head", ModelPartBuilder.create().uv(20, 0).cuboid(-1.5F, -3.0F, -1.5F, 3, 3, 3), ModelTransform.pivot(0.0F, 3.4F, -6.0F));
        ModelPartData mouthup = head.addChild("mouthup", ModelPartBuilder.create().uv(35, 0).cuboid(-1.5F, -2.0F, -2.5F, 3, 2, 3, f_03), ModelTransform.pivot(0.0F, -1.1F, -1.7F));
        head.addChild("mouthdown", ModelPartBuilder.create().uv(20, 8).cuboid(-1.5F, -2.0F, -2.5F, 3, 2, 3, f_04), ModelTransform.pivot(0.0F, 0.2F, -1.6F));
        head.addChild("earleft", ModelPartBuilder.create().uv(50, 0).cuboid(-0.5F, -2.0F, -0.5F, 1, 2, 1, f_02), ModelTransform.pivot(1.4F, -1.5F, 0.7F));
        head.addChild("earright", ModelPartBuilder.create().uv(50, 0).cuboid(-0.5F, -2.0F, -0.5F, 1, 2, 1, f_02), ModelTransform.pivot(-1.4F, -1.5F, 0.7F));
        ModelPartData body = partDefinition.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, 0.0F, -1.5F, 6, 10, 3), ModelTransform.of(0.0F, 2.5F, -3.5F,0.31869712141416456F, 0.0F, 0.0F));
        ModelPartData rightarm =partDefinition.addChild("right_arm", ModelPartBuilder.create().uv(0, 16).cuboid(-0.5F, -1.0F, -0.5F, 1, 10, 1,f01), ModelTransform.pivot(-3.5F, 4.5F, -3.4F));
        ModelPartData leftarm =partDefinition.addChild("left_arm", ModelPartBuilder.create().uv(0, 16).cuboid(-0.5F, -1.0F, -0.5F, 1, 10, 1,f01), ModelTransform.pivot(3.5F, 4.5F, -3.4F));
        ModelPartData leftleg =partDefinition.addChild("left_leg", ModelPartBuilder.create().uv(0, 30).cuboid(-1.0F, 0.0F, -1.0F, 2, 12, 2), ModelTransform.pivot(2.0F, 12.0F, -1.0F));
        ModelPartData rightleg =partDefinition.addChild("right_leg", ModelPartBuilder.create().uv(0, 30).cuboid(-1.0F, 0.0F, -1.0F, 2, 12, 2), ModelTransform.pivot(-2.0F, 12.0F, -1.0F));
        ModelPartData tail = partDefinition.addChild("tail1", ModelPartBuilder.create().uv(10,16).cuboid(-1.0F, -1.0F, 0.0F, 2, 2, 8,f_02), ModelTransform.of(0.0F, 11.0F, 0.0F,-0.4553564018453205F, 0.0F, 0.0F));
        ModelPartData tail2 = tail.addChild("tail2", ModelPartBuilder.create().uv(10, 30).cuboid(-1.0F, -1.0F, -0.7F, 2, 2, 7,f_03), ModelTransform.pivot(0.0F, 0F, 6.9F));
        tail2.addChild("tailend", ModelPartBuilder.create().uv(10, 45).cuboid(-0.5F, -0.5F, -0.5F, 1, 1, 7), ModelTransform.pivot(0.0F, 0F, 6F));

        body.addChild("chestplate", ModelPartBuilder.create().uv(35, 10).cuboid(-3F, 0.0F, -1.5F, 6, 10, 3,f01), ModelTransform.NONE);
        leftarm.addChild("armplatesl", ModelPartBuilder.create().uv(35, 24).cuboid(-0.5F, -2.0F, -0.5F, 1, 10, 1,f02), ModelTransform.NONE);
        rightarm.addChild("armplatesr", ModelPartBuilder.create().uv(35, 24).cuboid(-0.5F, -2.0F, -0.5F, 1, 10, 1,f02), ModelTransform.NONE);
        leftleg.addChild("legplatel", ModelPartBuilder.create().uv(35, 36).cuboid(-1.0F, 0.0F, -1.0F, 2, 12, 2,f01), ModelTransform.NONE);
        rightleg.addChild("legplater", ModelPartBuilder.create().uv(35, 36).cuboid(-1.0F, 0.0F, -1.0F, 2, 12, 2,f01), ModelTransform.NONE);
        head.addChild("helmet", ModelPartBuilder.create().uv(44, 24).cuboid(-1.5F, -3.0F, -1.5F, 3, 3, 3, f01), ModelTransform.NONE);
        mouthup.addChild("helmetfront", ModelPartBuilder.create().uv(44,30).cuboid(-1.5F, -2.0F, -2.5F, 3, 2, 3, f_02), ModelTransform.NONE);
        head.addChild("crest", ModelPartBuilder.create().uv(44,35).cuboid(-0.5F, -5F, -1.5F, 1, 2, 3, f_01), ModelTransform.NONE);
        body.addChild("pagne", ModelPartBuilder.create().uv(35, 50).cuboid(-3.0F, 9F, 1.5F, 6, 5, 3,f01), ModelTransform.of(0,0,0,-0.31869712141416456F, 0.0F, 0.0F));
        mouthup.addChild("helmetfrontfull",ModelPartBuilder.create().uv(0, 55).cuboid(-1.5F, -2.1F, -0.8F, 3, 3, 3,f_01), ModelTransform.pivot(0.0F, 0.2F, -1.6F));

        return TexturedModelData.of(meshDefinition, 64, 64);
    }


    public static float pitch = -2;
    public static float yaw = 0;
    public void setAngles(T entityIn,float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.hat.visible=false;
        SkavenEntity.State state = entityIn.getState();
        this.bipedHead.yaw = netHeadYaw * 0.017453292F;
        this.bipedHead.pitch = headPitch * 0.017453292F;
        if (state.equals(SkavenEntity.State.ATTACKING)) {
            float f = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
            float f1 = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
            this.bipedRightArm.roll = 0.0F;
            this.bipedLeftArm.roll = 0.0F;
            this.bipedRightArm.yaw = -(0.1F - f * 0.6F);
            this.bipedLeftArm.yaw = 0.1F - f * 0.6F;
            this.bipedRightArm.pitch = -((float) Math.PI / 2.2F);
            this.bipedLeftArm.pitch = -((float) Math.PI / 2.2F);
            this.bipedRightArm.pitch -= f * 1.2F - f1 * 0.4F;
            this.bipedLeftArm.pitch -= f * 1.2F - f1 * 0.4F;
            this.bipedRightArm.roll += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.bipedLeftArm.roll -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.bipedRightArm.pitch += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            this.bipedLeftArm.pitch -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;

        }else if(state.equals(SkavenEntity.State.AIMING)){
            hold(this.bipedRightArm,this.bipedLeftArm,this.bipedHead,!entityIn.isLeftHanded());
        }else if(state.equals(SkavenEntity.State.GUN_CHARGE)){
            bipedHead.pitch=0F;
            bipedHead.yaw=0;
            bipedRightArm.roll=0F;
            bipedRightArm.roll += MathHelper.cos(ageInTicks*1.1F)*0.01F;
            bipedLeftArm.roll=0F;
            bipedLeftArm.roll -= MathHelper.cos(ageInTicks*1.1F)*0.01F;
            if(entityIn.getMainArm().equals(Arm.RIGHT)){
                bipedLeftArm.yaw=0.5F;
                bipedRightArm.yaw=0F;
                bipedRightArm.pitch=-0.5F;
                bipedLeftArm.pitch=-1.5F + MathHelper.cos(ageInTicks*0.8F)*0.05F;
            }else{
                bipedRightArm.yaw=-0.5F;
                bipedLeftArm.yaw=0F;
                bipedLeftArm.pitch=-0.5F;
                bipedRightArm.pitch=-1.5F + MathHelper.cos(ageInTicks*0.8F)*0.05F;
            }
            bipedHead.pitch += MathHelper.cos(ageInTicks*1.1F)*0.03F;
        }
        else{
            bipedHead.pitch=0F;
            bipedHead.yaw=0;
            bipedRightArm.roll=0F;
            bipedRightArm.roll += MathHelper.cos(ageInTicks*1.1F)*0.04F;
            bipedLeftArm.roll=0F;
            bipedLeftArm.roll -= MathHelper.cos(ageInTicks*1.1F)*0.04F;
            bipedLeftArm.yaw=0F;
            bipedRightArm.yaw=0F;
            bipedRightArm.pitch=0F;
            bipedLeftArm.pitch=0F;
            bipedHead.pitch += MathHelper.cos(ageInTicks*1.1F)*0.04F;
        }


        this.bipedRightLeg.pitch = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
        this.bipedLeftLeg.pitch = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)  * limbSwingAmount;
        if(limbSwing<0.05) {
            tail1.yaw = MathHelper.cos(ageInTicks * 0.3F) * 0.2F;
            tail2.yaw = MathHelper.cos(ageInTicks * 0.3F) * 0.15F;
            tailend.yaw = MathHelper.cos(ageInTicks * 0.3F) * 0.15F;
        }else{
            tail1.yaw = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount*0.8F;
            tail2.yaw = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount*0.8F;
            tailend.yaw = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount*0.8F;

        }
        this.bipedRightLeg.yaw = 0.0F;
        this.bipedLeftLeg.yaw = 0.0F;
        this.bipedRightLeg.roll = 0.0F;
        this.bipedLeftLeg.roll = 0.0F;


    }

    public void setArmAngle(Arm humanoidArm, MatrixStack poseStack) {
        float f = humanoidArm == Arm.RIGHT ? 1.0F : -1.0F;
        ModelPart modelPart = this.getArm(humanoidArm);
        modelPart.pivotX += f;
        modelPart.rotate(poseStack);
        modelPart.pivotX -= f;
    }

    public static void hold(ModelPart holdingArm, ModelPart otherArm, ModelPart head, boolean rightArmed) {
        ModelPart modelPart = rightArmed ? holdingArm : otherArm;
        ModelPart modelPart2 = rightArmed ? otherArm : holdingArm;
        modelPart.yaw = (rightArmed ? -0.01f : 0.01f) + head.yaw;
        modelPart2.yaw = (rightArmed ? 0.6f : -0.6f) + head.yaw;
        modelPart.pitch = -1.5707964f + head.pitch + 0.1f;
        modelPart2.pitch = -1.5f + head.pitch;
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
