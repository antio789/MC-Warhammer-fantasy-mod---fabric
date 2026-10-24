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
import net.minecraft.world.entity.monster.AbstractIllager;
import warhammermod.Entities.Living.DwarfEntity;

@Environment(EnvType.CLIENT)
public class DwarfModel<T extends DwarfEntity> extends HierarchicalModel<T> implements ArmedModel, HeadedModel
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

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        CubeDeformation f05 = new CubeDeformation(0.5F);
        CubeDeformation f09 = new CubeDeformation(0.009F);
        PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -6.0F, -4.0F, 8, 10, 8), PartPose.ZERO);
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(65, 0).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 10.0F, 8.0F,f05), PartPose.ZERO);
        hat.addOrReplaceChild("hat_rim", CubeListBuilder.create().texOffs(65, 25).addBox(-8.0F, -8.0F, -3.0F, 16.0F, 16.0F, 1.0F), PartPose.offsetAndRotation(0,0,0,-(float) Math.PI / 2F,0,0));

        head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 2.0F, -6.0F, 2, 4, 2), PartPose.offset(0.0F, -2, -0));
        head.addOrReplaceChild("beard", CubeListBuilder.create().texOffs(36,38).addBox(-4.0F,6.0F,-5.0F,8,6,1), PartPose.offset(0.0F, -2, -0));
        head.addOrReplaceChild("mustache1", CubeListBuilder.create().texOffs(36,38).addBox(-4.0F,3.0F,-5.0F,2,3,1)
                        .addBox(2.0F,3.0F,-5.0F,2,3,1)
                        .addBox(-2.0F,3.0F,-5.0F,1,1,1)
                        .addBox(1.0F,3.0F,-5.0F,1,1,1), PartPose.offset(0, -2, 0));
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 19).addBox(-5.0F, 4.0F, -3.0F, 10, 10, 7), PartPose.ZERO);
        partDefinition.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 38).addBox(-5.0F, 4.0F, -3.0F, 10, 16, 7,f05), PartPose.ZERO);
        PartDefinition villagerarms = partDefinition.addOrReplaceChild("arms", CubeListBuilder.create()
                        .texOffs(40, 16).addBox(-8.0F, 1.0F, 1.0F, 4, 8, 4,f09)
                .texOffs(40,16).addBox(4.0F, 1.0F, 1.0F, 4, 8, 4,f09)
                .texOffs(36, 29).addBox(-4.0F, 5.0F, 1.0F, 8, 4, 4,f09)
                , PartPose.offset(0,4,0));
        partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(34, 0).addBox(-3F, 2.0F, -2.0F, 5, 10, 5), PartPose.offset(-2.0F, 12.0F, 0.0F));
        partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(34, 0).addBox(-2F, 2.0F, -2.0F, 5, 10, 5).mirror(), PartPose.offset(2.0F, 12.0F, 0.0F));
        partDefinition.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(35, 46).addBox(-3F, -2F, 2F, 4, 10, 4).mirror(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partDefinition.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(35, 46).addBox(-1.0F, 2.0F, -2F, 4, 10, 4).mirror(), PartPose.offset(5.0F, 2.0F, 0.0F));
        return LayerDefinition.create(meshDefinition, 128, 128);
    }

    @Override
    public ModelPart root() {
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
        boolean flag = entityIn.getUnhappyCounter()>0;

        this.head.yRot = netHeadYaw * 0.017453292F;
        this.head.xRot = headPitch * 0.017453292F;
        if (flag) {
            this.head.zRot = 0.3F * Mth.sin(0.45F * ageInTicks);
            this.head.xRot = 0.4F;
        } else {
            this.head.zRot = 0.0F;
        }
        this.villagerArms.yRot = 0F;
        this.villagerArms.zRot = 0F;
        this.villagerArms.xRot = -0.75F;

        this.villagerArms.z=-0.5F;
        this.villagerArms.x=0F;
        this.villagerArms.y=4.5F;

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.yRot = 0.0F;


            DwarfEntity entityDwarf = (DwarfEntity) entityIn;
            if (entityDwarf.getArmPose()== AbstractIllager.IllagerArmPose.ATTACKING)
            {
                float f = Mth.sin(this.attackTime * (float) Math.PI);
                float f1 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float) Math.PI);
                this.rightArm.zRot = 0.0F;
                this.leftArm.zRot = 0.0F;
                this.rightArm.yRot = 0.15707964F;
                this.leftArm.yRot = -0.15707964F;

                if(entityDwarf.getProfession().name().equals("slayer")){
                    this.rightArm.xRot = -1.8849558F + Mth.cos(ageInTicks * 0.09F) * 0.15F;
                    this.leftArm.xRot = -1.8849558F + Mth.cos(ageInTicks * 0.09F) * 0.15F;
                    this.rightArm.xRot += f * 2.2F - f1 * 0.4F;
                    this.leftArm.xRot += f * 2.2F - f1 * 0.4F;
                }
                else if ((entityIn).getMainArm() == HumanoidArm.RIGHT)
                {
                    this.rightArm.xRot = -1.8849558F + Mth.cos(ageInTicks * 0.09F) * 0.15F;
                    this.leftArm.xRot = -0.0F + Mth.cos(ageInTicks * 0.19F) * 0.5F;
                    this.rightArm.xRot += f * 2.2F - f1 * 0.4F;
                    this.leftArm.xRot += f * 1.2F - f1 * 0.4F;
                }
                else
                {
                    this.rightArm.xRot = -0.0F + Mth.cos(ageInTicks * 0.19F) * 0.5F;
                    this.leftArm.xRot = -1.8849558F + Mth.cos(ageInTicks * 0.09F) * 0.15F;
                    this.rightArm.xRot += f * 1.2F - f1 * 0.4F;
                    this.leftArm.xRot += f * 2.2F - f1 * 0.4F;
                }

                this.rightArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
                this.leftArm.zRot -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
                this.rightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
                this.leftArm.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
            }

        boolean attacking = (entityIn.getArmPose() == AbstractIllager.IllagerArmPose.ATTACKING);
        this.villagerArms.visible = !attacking;
        this.leftArm.visible = attacking;
        this.rightArm.visible = attacking;

    }

    protected ModelPart getArm(HumanoidArm humanoidArm) {
        return humanoidArm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelPart getHead() {
        return this.head;
    }


    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.getArm(humanoidArm).translateAndRotate(poseStack);
    }

}
