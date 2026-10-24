package warhammermod.Client.Render.Entity.Renders.Models;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.AbstractHorseEntityModel;
import warhammermod.Entities.Living.PegasusEntity;


@Environment(EnvType.CLIENT)
public class Pegasusmodel<T extends PegasusEntity> extends AbstractHorseEntityModel<T> {

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

   public static TexturedModelData createBodyLayer() {
      Dilation cubeDeformation = Dilation.NONE;
      ModelData meshDefinition = getModelData(cubeDeformation);
      ModelPartData partDefinition = meshDefinition.getRoot();

      ModelPartData rightwing =partDefinition.addChild("rwing", ModelPartBuilder.create().uv(0, 65).cuboid(-21.0F, -1.0F, -1.0F, 21, 2, 2, cubeDeformation), ModelTransform.pivot(-4.5F, 5.0F, -6.5F));
      ModelPartData leftwing =partDefinition.addChild("lwing", ModelPartBuilder.create().uv(0, 65).cuboid(0F, -1F, -1F, 21, 2, 2, cubeDeformation), ModelTransform.pivot(4.5F, 5.0F, -6.5F));
      leftwing.addChild("llarge", ModelPartBuilder.create().uv(0, 70).cuboid(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), ModelTransform.pivot(0F, 0F, 0F));
      rightwing.addChild("rlarge", ModelPartBuilder.create().uv(0, 70).cuboid(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), ModelTransform.pivot(-20F, 0F, 0F));
      leftwing.addChild("leftend", ModelPartBuilder.create().uv(8 , 87).cuboid(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), ModelTransform.pivot(20F, 0F, 0F));
      rightwing.addChild("rightend", ModelPartBuilder.create().uv(8, 104).cuboid(-15F, -0.5F, 0.5F, 15, 1, 15, cubeDeformation), ModelTransform.pivot(-20F, 0F, 0F));

      return TexturedModelData.of(meshDefinition, 128, 128);
   }

   public static TexturedModelData createarmorLayer() {
      Dilation cubeDeformation = new Dilation(0.1F);
      ModelData meshDefinition = getModelData(cubeDeformation);
      ModelPartData partDefinition = meshDefinition.getRoot();

      ModelPartData rightwing =partDefinition.addChild("rwing", ModelPartBuilder.create().uv(0, 65).cuboid(0F, -1F, -1F, 21, 2, 2, cubeDeformation), ModelTransform.pivot(-4.5F, 5.0F, -6.5F));
      ModelPartData leftwing =partDefinition.addChild("lwing", ModelPartBuilder.create().uv(0, 65).cuboid(-21.0F, -1.0F, -1.0F, 21, 2, 2, cubeDeformation), ModelTransform.pivot(4.5F, 5.0F, -6.5F));
      leftwing.addChild("llarge", ModelPartBuilder.create().uv(0, 70).cuboid(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), ModelTransform.pivot(0F, 0F, 0F));
      rightwing.addChild("rlarge", ModelPartBuilder.create().uv(0, 70).cuboid(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), ModelTransform.pivot(-20F, 0F, 0F));
      leftwing.addChild("leftend", ModelPartBuilder.create().uv(8 , 87).cuboid(0F, -0.5F, 0.5F, 20, 1, 15, cubeDeformation), ModelTransform.pivot(20F, 0F, 0F));
      rightwing.addChild("rightend", ModelPartBuilder.create().uv(8, 104).cuboid(-15F, -0.5F, 0.5F, 15, 1, 15, cubeDeformation), ModelTransform.pivot(-20F, 0F, 0F));

      return TexturedModelData.of(meshDefinition, 64, 64);
   }

   public Iterable<ModelPart> getBodyParts() {
      return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.WingLBone, this.WingRbone));
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
      super.animateModel(entityIn,limbSwing,limbSwingAmount,partialTick);
      if(entityIn.isIsstationaryflying()) {
         if (WingAngle > 0.6) {
            WingDirection=-Math.abs(WingDirection);
         }else if (WingAngle <-0.6){WingDirection= Math.abs(WingDirection);}
         WingLBone.roll=WingAngle;
         wingLend.roll=WingAngle/2.1F;
         WingRbone.roll=-WingAngle;
         wingRend.roll=-WingAngle/2.1F;
         WingAngle+=WingDirection*timeinterval(partialTick);
         WingRbone.pitch=0F;
         WingRbone.yaw=0F;
         WingLBone.pitch=0F;
         WingLBone.yaw=0F;
         wingLend.yaw=0F;
         wingRend.yaw=0F;
         wingRend.pitch=0F;
         wingLend.pitch=0F;
      }
      else if(entityIn.isIselytrafly()){
         if(entityIn.getPitch()>0){WingAngleElytra=0.1F;}
         else if(Math.abs(WingAngleElytra) > 0.2) {
            WingDirection*=-1;
         }
         WingLBone.roll=-WingAngleElytra;
         wingLend.roll=WingAngleElytra;
         WingRbone.roll=WingAngleElytra;
         wingRend.roll=-WingAngleElytra;
         WingLBone.yaw=-0.15F;
         WingRbone.yaw=0.15F;
         wingLend.yaw=0F;
         wingRend.yaw=0F;
         wingRend.pitch=0F;
         wingLend.pitch=0F;
         WingRbone.pitch=0F;
         WingLBone.pitch=0F;
         WingAngleElytra+=WingDirection*0.2F;

      }
      //should only activate when flyingdownsafely rework ongroundtimer not working when half of the entity not on a block
      else if(entityIn.getVelocity().y < -0.745 && !entityIn.hasPassengers()){
         WingLBone.roll=-0.1F;
         wingLend.roll=0.1F;
         WingRbone.roll=0.1F;
         wingRend.roll=-0.1F;
         WingLBone.yaw=-0.15F;
         WingRbone.yaw=0.15F;
         wingLend.yaw=0F;
         wingRend.yaw=0F;
         wingRend.pitch=0F;
         wingLend.pitch=0F;
         WingRbone.pitch=0F;
         WingLBone.pitch=0F;
      }
      else{ //on ground
         WingRbone.pitch=0.61F;
         WingRbone.yaw=0.94F;
         WingRbone.roll=-1.04F;
         WingLBone.pitch=0.61F;
         WingLBone.yaw=-0.94F;
         WingLBone.roll=1.04F;
         wingLend.yaw=-0.82F;
         wingRend.yaw=0.82F;
         wingRend.pitch=-0.06F;
         wingLend.pitch=-0.06F;
      }


   }



   }