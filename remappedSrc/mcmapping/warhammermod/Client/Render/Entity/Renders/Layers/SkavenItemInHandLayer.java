package warhammermod.Client.Render.Entity.Renders.Layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import warhammermod.Entities.Living.SkavenEntity;

@Environment(EnvType.CLIENT)
public class SkavenItemInHandLayer<T extends SkavenEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M> {
   private final ItemInHandRenderer heldItemRenderer;

   public SkavenItemInHandLayer(RenderLayerParent<T, M> renderLayerParent,ItemInHandRenderer heldItemRenderer) {
      super(renderLayerParent);
      this.heldItemRenderer=heldItemRenderer;
   }

   public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
      boolean bl = livingEntity.getMainArm() == HumanoidArm.RIGHT;
      ItemStack itemStack = bl ? livingEntity.getOffhandItem() : livingEntity.getMainHandItem();
      ItemStack itemStack2 = bl ? livingEntity.getMainHandItem() : livingEntity.getOffhandItem();
      if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
         poseStack.pushPose();
         if (this.getParentModel().young) {
            float m = 0.5F;
            poseStack.translate(0.0D, 0.75D, 0.0D);//y=0.75
            poseStack.scale(m, m, m);
         }
         if(livingEntity.getState().equals(SkavenEntity.State.GUN_CHARGE)) {
            this.renderArmWithItemcharging(livingEntity,bl? itemStack2:itemStack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, livingEntity.getMainArm(), poseStack, multiBufferSource, i);
         }
         else {
            this.renderArmWithItem(livingEntity, itemStack2, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, poseStack, multiBufferSource, i);
            this.renderArmWithItemleft(livingEntity, itemStack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, poseStack, multiBufferSource, i);
         }
         poseStack.popPose();
      }
   }

   protected void renderArmWithItem(SkavenEntity livingEntity, ItemStack itemStack, ItemDisplayContext transformType, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
      if (!itemStack.isEmpty()) {
         poseStack.pushPose();
         this.getParentModel().translateToHand(humanoidArm, poseStack);
         poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
         poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

         boolean bl = false;
         poseStack.translate((float) 1 / 16.0F, 0.1, -0.625D);
         float f = 0.8F;
         poseStack.scale(f,f,f);
         this.heldItemRenderer.renderItem(livingEntity, itemStack, transformType, bl, poseStack, multiBufferSource, i);
         poseStack.popPose();
      }
   }
   protected void renderArmWithItemleft(SkavenEntity livingEntity, ItemStack itemStack, ItemDisplayContext transformType, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
      if (!itemStack.isEmpty()) {
         poseStack.pushPose();
         ((ArmedModel)this.getParentModel()).translateToHand(humanoidArm, poseStack);
         poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
         poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
         boolean bl = humanoidArm == HumanoidArm.LEFT;
         poseStack.translate(-1  / 16.0F, 0, -0.625D);
         float f = 0.8F;
         poseStack.scale(f, f, f);
         this.heldItemRenderer.renderItem(livingEntity, itemStack, transformType, bl, poseStack, multiBufferSource, i);
         poseStack.popPose();
      }
   }

   protected void renderArmWithItemcharging(SkavenEntity livingEntity, ItemStack itemStack, ItemDisplayContext transformType, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
      if (!itemStack.isEmpty()) {
         poseStack.pushPose();
         this.getParentModel().translateToHand(humanoidArm, poseStack);

         //poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(50.0F));
         boolean bl = humanoidArm == HumanoidArm.LEFT;

         if(bl){
            poseStack.mulPose(Axis.XP.rotationDegrees(-130));
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-20));
            poseStack.translate(0.05, -0.2, -0.35);
         }else {
            poseStack.mulPose(Axis.XP.rotationDegrees(-35));//-55
            poseStack.mulPose(Axis.YP.rotationDegrees(-20));//-20
            poseStack.translate(0.05, 0.35, 0.15);
         }

         float f = 0.8F;
         poseStack.scale(f,f,f);
         this.heldItemRenderer.renderItem(livingEntity, itemStack, transformType, bl, poseStack, multiBufferSource, i);
         poseStack.popPose();
      }
   }

   public static float translate_x=15;
   public static float translate_y=20;
   public static float translate_z=20;
   public static int rotx=-35;
   public static int roty=-20;
   public static int rotz=0;
}
