package warhammermod.Client.Render.Entity.Renders.Models;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
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
import warhammermod.Entities.Living.PegasusEntity;


@Environment(EnvType.CLIENT)
public class Pegasusmodel<T extends PegasusEntity> extends HorseModel<T> {

   public ModelPart WingRbone;
   public ModelPart WingLBone;
   public ModelPart wingLlarge;
   public ModelPart wingRlarge;
   public ModelPart wingRend;
   public ModelPart wingLend;

   public Pegasusmodel(ModelPart model) {
      super(model);
      this.WingRbone = model.getChild("rwing");
      this.WingLBone = model.getChild("lwing");
      this.wingLlarge = this.WingLBone.getChild("llarge");
      this.wingLend = this.WingLBone.getChild("leftend");
      this.wingRlarge = this.WingRbone.getChild("rlarge");
      this.wingRend = this.WingRbone.getChild("rightend");
   }

   public static LayerDefinition createBodyLayer() {
      CubeDeformation cubeDeformation = CubeDeformation.NONE;
      MeshDefinition meshDefinition = createBodyMesh(cubeDeformation);
      PartDefinition partDefinition = meshDefinition.getRoot();

      PartDefinition rightwing =partDefinition.addOrReplaceChild("rwing", CubeListBuilder.create().texOffs(0, 65).addBox(-21.0F, -1.0F, -1.0F, 21, 2, 2, cubeDeformation), PartPose.offset(-4.5F, 5.0F, -6.5F));
      PartDefinition leftwing =partDefinition.addOrReplaceChild("lwing", CubeListBuilder.create().texOffs(0, 65).addBox(0F, -1F, -1F, 21, 2, 2, cubeDeformation), PartPose.offset(4.5F, 5.0F, -6.5F));
      leftwing.addOrReplaceChild("llarge", CubeListBuilder.create().texOffs(0, 70).addBox(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), PartPose.offset(0F, 0F, 0F));
      rightwing.addOrReplaceChild("rlarge", CubeListBuilder.create().texOffs(0, 70).addBox(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), PartPose.offset(-20F, 0F, 0F));
      leftwing.addOrReplaceChild("leftend", CubeListBuilder.create().texOffs(8 , 87).addBox(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), PartPose.offset(20F, 0F, 0F));
      rightwing.addOrReplaceChild("rightend", CubeListBuilder.create().texOffs(8, 104).addBox(-15F, -0.5F, 0.5F, 15, 1, 15, cubeDeformation), PartPose.offset(-20F, 0F, 0F));

      return LayerDefinition.create(meshDefinition, 128, 128);
   }

   public static LayerDefinition createarmorLayer() {
      CubeDeformation cubeDeformation = new CubeDeformation(0.1F);
      MeshDefinition meshDefinition = createBodyMesh(cubeDeformation);
      PartDefinition partDefinition = meshDefinition.getRoot();

      PartDefinition rightwing =partDefinition.addOrReplaceChild("rwing", CubeListBuilder.create().texOffs(0, 65).addBox(0F, -1F, -1F, 21, 2, 2, cubeDeformation), PartPose.offset(-4.5F, 5.0F, -6.5F));
      PartDefinition leftwing =partDefinition.addOrReplaceChild("lwing", CubeListBuilder.create().texOffs(0, 65).addBox(-21.0F, -1.0F, -1.0F, 21, 2, 2, cubeDeformation), PartPose.offset(4.5F, 5.0F, -6.5F));
      leftwing.addOrReplaceChild("llarge", CubeListBuilder.create().texOffs(0, 70).addBox(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), PartPose.offset(0F, 0F, 0F));
      rightwing.addOrReplaceChild("rlarge", CubeListBuilder.create().texOffs(0, 70).addBox(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), PartPose.offset(-20F, 0F, 0F));
      leftwing.addOrReplaceChild("leftend", CubeListBuilder.create().texOffs(8 , 87).addBox(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), PartPose.offset(20F, 0F, 0F));
      rightwing.addOrReplaceChild("rightend", CubeListBuilder.create().texOffs(8, 104).addBox(-15F, -0.5F, 0.5F, 15, 1, 15, cubeDeformation), PartPose.offset(-20F, 0F, 0F));

      return LayerDefinition.create(meshDefinition, 64, 64);
   }

   public Iterable<ModelPart> bodyParts() {
      return Iterables.concat(super.bodyParts(), ImmutableList.of(this.WingLBone, this.WingRbone));
   }

   private float WingAngle=0.2F;
   private float WingDirection=0.08F;
   private float WingAngleElytra=0.1F;
   private Byte ongroundtimer=0;
   private float prevpartial;
   private float timeinterval(float tick){
      float result = Math.abs(tick-prevpartial);
      prevpartial=tick;
      return result;
   }

   public void animateModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      super.prepareMobModel(entityIn,limbSwing,limbSwingAmount,partialTick);
      if(entityIn.isIsstationaryflying()) {
         if (WingAngle > 0.6) {
            WingDirection=-Math.abs(WingDirection);
         }else if (WingAngle <-0.6){WingDirection= Math.abs(WingDirection);}
         WingLBone.zRot=WingAngle;
         wingLend.zRot=WingAngle/2.1F;
         WingRbone.zRot=-WingAngle;
         wingRend.zRot=-WingAngle/2.1F;
         WingAngle+=WingDirection*timeinterval(partialTick);
         WingRbone.xRot=0F;
         WingRbone.yRot=0F;
         WingLBone.xRot=0F;
         WingLBone.yRot=0F;
         wingLend.yRot=0F;
         wingRend.yRot=0F;
         wingRend.xRot=0F;
         wingLend.xRot=0F;
      }
      else if(entityIn.isIselytrafly()){
         if(entityIn.getXRot()>0){WingAngleElytra=0.1F;}
         else if(Math.abs(WingAngleElytra) > 0.2) {
            WingDirection*=-1;
         }
         WingLBone.zRot=-WingAngleElytra;
         wingLend.zRot=WingAngleElytra;
         WingRbone.zRot=WingAngleElytra;
         wingRend.zRot=-WingAngleElytra;
         WingLBone.yRot=-0.15F;
         WingRbone.yRot=0.15F;
         wingLend.yRot=0F;
         wingRend.yRot=0F;
         wingRend.xRot=0F;
         wingLend.xRot=0F;
         WingRbone.xRot=0F;
         WingLBone.xRot=0F;
         WingAngleElytra+=WingDirection*0.2F;

      }
      //should only activate when flyingdownsafely rework ongroundtimer not working when half of the entity not on a block
      else if(entityIn.getDeltaMovement().y < -0.745 && !entityIn.isVehicle()){
         WingLBone.zRot=-0.1F;
         wingLend.zRot=0.1F;
         WingRbone.zRot=0.1F;
         wingRend.zRot=-0.1F;
         WingLBone.yRot=-0.15F;
         WingRbone.yRot=0.15F;
         wingLend.yRot=0F;
         wingRend.yRot=0F;
         wingRend.xRot=0F;
         wingLend.xRot=0F;
         WingRbone.xRot=0F;
         WingLBone.xRot=0F;
      }
      else{ //on ground
         WingRbone.xRot=0.61F;
         WingRbone.yRot=0.94F;
         WingRbone.zRot=-1.04F;
         WingLBone.xRot=0.61F;
         WingLBone.yRot=-0.94F;
         WingLBone.zRot=1.04F;
         wingLend.yRot=-0.82F;
         wingRend.yRot=0.82F;
         wingRend.xRot=-0.06F;
         wingLend.xRot=-0.06F;
      }


   }



   }