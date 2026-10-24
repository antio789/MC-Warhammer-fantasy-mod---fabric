package warhammermod.Client.Render.Entity.Renders.Models;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import warhammermod.Entities.Living.DwarfEntity;

@Environment(EnvType.CLIENT)
public class DwarfModel<T extends DwarfEntity> extends SinglePartEntityModel<T> implements ModelWithArms, ModelWithHead
{
    public ModelPart head;
    public ModelPart villagerBody;
    public ModelPart villagerArms;
    public ModelPart rightLeg;
    public ModelPart leftLeg;
    public ModelPart rightArm;
    public ModelPart leftArm;

    protected ModelPart hat;
    protected ModelPart hatRim;

    protected ModelPart root;



    public DwarfModel(ModelPart modelPart) {
        this.root = modelPart;
        this.head = modelPart.getChild("head");
        this.hat = this.head.getChild("hat");
        this.hatRim = this.hat.getChild("hat_rim");
        this.rightLeg = modelPart.getChild("right_leg");
        this.leftLeg = modelPart.getChild("left_leg");
        this.rightArm = modelPart.getChild("rightarm");
        this.leftArm = modelPart.getChild("leftarm");
        this.villagerArms = modelPart.getChild("arms");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData meshDefinition = new ModelData();
        ModelPartData partDefinition = meshDefinition.getRoot();
        Dilation f05 = new Dilation(0.5F);
        Dilation f09 = new Dilation(0.009F);
        ModelPartData head = partDefinition.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -6.0F, -4.0F, 8, 10, 8), ModelTransform.NONE);
        ModelPartData hat = head.addChild("hat", ModelPartBuilder.create().uv(65, 0).cuboid(-4.0F, -6.0F, -4.0F, 8.0F, 10.0F, 8.0F,f05), ModelTransform.NONE);
        hat.addChild("hat_rim", ModelPartBuilder.create().uv(65, 25).cuboid(-8.0F, -8.0F, -3.0F, 16.0F, 16.0F, 1.0F), ModelTransform.of(0,0,0,-(float) Math.PI / 2F,0,0));

        head.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, 2.0F, -6.0F, 2, 4, 2), ModelTransform.pivot(0.0F, -2, -0));
        head.addChild("beard", ModelPartBuilder.create().uv(36,38).cuboid(-4.0F,6.0F,-5.0F,8,6,1), ModelTransform.pivot(0.0F, -2, -0));
        head.addChild("mustache1", ModelPartBuilder.create().uv(36,38).cuboid(-4.0F,3.0F,-5.0F,2,3,1)
                        .cuboid(2.0F,3.0F,-5.0F,2,3,1)
                        .cuboid(-2.0F,3.0F,-5.0F,1,1,1)
                        .cuboid(1.0F,3.0F,-5.0F,1,1,1), ModelTransform.pivot(0, -2, 0));
        partDefinition.addChild("body", ModelPartBuilder.create().uv(0, 19).cuboid(-5.0F, 4.0F, -3.0F, 10, 10, 7), ModelTransform.NONE);
        partDefinition.addChild("jacket", ModelPartBuilder.create().uv(0, 38).cuboid(-5.0F, 4.0F, -3.0F, 10, 16, 7,f05), ModelTransform.NONE);
        ModelPartData villagerarms = partDefinition.addChild("arms", ModelPartBuilder.create()
                        .uv(40, 16).cuboid(-8.0F, 1.0F, 1.0F, 4, 8, 4,f09)
                .uv(40,16).cuboid(4.0F, 1.0F, 1.0F, 4, 8, 4,f09)
                .uv(36, 29).cuboid(-4.0F, 5.0F, 1.0F, 8, 4, 4,f09)
                , ModelTransform.pivot(0,4,0));
        partDefinition.addChild("right_leg", ModelPartBuilder.create().uv(34, 0).cuboid(-3F, 2.0F, -2.0F, 5, 10, 5), ModelTransform.pivot(-2.0F, 12.0F, 0.0F));
        partDefinition.addChild("left_leg", ModelPartBuilder.create().uv(34, 0).cuboid(-2F, 2.0F, -2.0F, 5, 10, 5).mirrored(), ModelTransform.pivot(2.0F, 12.0F, 0.0F));
        partDefinition.addChild("rightarm", ModelPartBuilder.create().uv(35, 46).cuboid(-3F, -2F, 2F, 4, 10, 4).mirrored(), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));
        partDefinition.addChild("leftarm", ModelPartBuilder.create().uv(35, 46).cuboid(-1.0F, 2.0F, -2F, 4, 10, 4).mirrored(), ModelTransform.pivot(5.0F, 2.0F, 0.0F));
        return TexturedModelData.of(meshDefinition, 128, 128);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }


    /**
     * Sets the models various rotation angles then renders the model.
     */


    /**
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.head.render(scale);
        this.villagerBody.render(scale);
        this.rightVillagerLeg.render(scale);
        this.leftVillagerLeg.render(scale);

        if(entityIn instanceof EntityDwarf){
        EntityDwarf entityDwarf = (EntityDwarf)entityIn;
        if (entityDwarf.getArmPose()== AbstractIllager.IllagerArmPose.CROSSED)
        {
            this.villagerArms.render(scale+0.009F);

        }
        else
        {

            this.rightArm.render(scale+0.009F);
            this.leftArm.render(scale+0.009F);
        }
        }
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */

    public void setAngles(DwarfEntity entityIn,float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = entityIn.getHeadRollingTimeLeft()>0;

        this.head.yaw = netHeadYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
        if (flag) {
            this.head.roll = 0.3F * MathHelper.sin(0.45F * ageInTicks);
            this.head.pitch = 0.4F;
        } else {
            this.head.roll = 0.0F;
        }
        this.villagerArms.yaw = 0F;
        this.villagerArms.roll = 0F;
        this.villagerArms.pitch = -0.75F;

        this.villagerArms.pivotZ=-0.5F;
        this.villagerArms.pivotX=0F;
        this.villagerArms.pivotY=4.5F;

        this.rightLeg.pitch = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.pitch = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.rightLeg.yaw = 0.0F;
        this.leftLeg.yaw = 0.0F;


            DwarfEntity entityDwarf = (DwarfEntity) entityIn;
            if (entityDwarf.getArmPose()== IllagerEntity.State.ATTACKING)
            {
                float f = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
                float f1 = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
                this.rightArm.roll = 0.0F;
                this.leftArm.roll = 0.0F;
                this.rightArm.yaw = 0.15707964F;
                this.leftArm.yaw = -0.15707964F;

                if(entityDwarf.getProfession().name().equals("slayer")){
                    this.rightArm.pitch = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                    this.leftArm.pitch = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                    this.rightArm.pitch += f * 2.2F - f1 * 0.4F;
                    this.leftArm.pitch += f * 2.2F - f1 * 0.4F;
                }
                else if ((entityIn).getMainArm() == Arm.RIGHT)
                {
                    this.rightArm.pitch = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                    this.leftArm.pitch = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
                    this.rightArm.pitch += f * 2.2F - f1 * 0.4F;
                    this.leftArm.pitch += f * 1.2F - f1 * 0.4F;
                }
                else
                {
                    this.rightArm.pitch = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
                    this.leftArm.pitch = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                    this.rightArm.pitch += f * 1.2F - f1 * 0.4F;
                    this.leftArm.pitch += f * 2.2F - f1 * 0.4F;
                }

                this.rightArm.roll += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
                this.leftArm.roll -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
                this.rightArm.pitch += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
                this.leftArm.pitch -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            }

        boolean attacking = (entityIn.getArmPose() == IllagerEntity.State.ATTACKING);
        this.villagerArms.visible = !attacking;
        this.leftArm.visible = attacking;
        this.rightArm.visible = attacking;

    }

    protected ModelPart getArm(Arm humanoidArm) {
        return humanoidArm == Arm.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelPart getHead() {
        return this.head;
    }


    public void setArmAngle(Arm humanoidArm, MatrixStack poseStack) {
        this.getArm(humanoidArm).rotate(poseStack);
    }

}
