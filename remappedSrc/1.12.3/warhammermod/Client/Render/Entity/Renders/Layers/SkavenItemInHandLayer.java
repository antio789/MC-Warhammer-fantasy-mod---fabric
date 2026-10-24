package warhammermod.Client.Render.Entity.Renders.Layers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import warhammermod.Entities.Living.SkavenEntity;

@Environment(EnvType.CLIENT)
public class SkavenItemInHandLayer<T extends SkavenEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
   private final HeldItemRenderer heldItemRenderer;

   public SkavenItemInHandLayer(FeatureRendererContext<T, M> renderLayerParent,HeldItemRenderer heldItemRenderer) {
      super(renderLayerParent);
      this.heldItemRenderer=heldItemRenderer;
   }

   public void render(MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
      boolean bl = livingEntity.getMainArm() == Arm.RIGHT;
      ItemStack itemStack = bl ? livingEntity.getOffHandStack() : livingEntity.getMainHandStack();
      ItemStack itemStack2 = bl ? livingEntity.getMainHandStack() : livingEntity.getOffHandStack();
      if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
         poseStack.push();
         if (this.getContextModel().child) {
            float m = 0.5F;
            poseStack.translate(0.0D, 0.75D, 0.0D);//y=0.75
            poseStack.scale(m, m, m);
         }
         if(livingEntity.getState().equals(SkavenEntity.State.GUN_CHARGE)) {
            this.renderArmWithItemcharging(livingEntity,bl? itemStack2:itemStack, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, livingEntity.getMainArm(), poseStack, multiBufferSource, i);
         }
         else {
            this.renderArmWithItem(livingEntity, itemStack2, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, poseStack, multiBufferSource, i);
            this.renderArmWithItemleft(livingEntity, itemStack, ModelTransformationMode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, poseStack, multiBufferSource, i);
         }
         poseStack.pop();
      }
   }

   protected void renderArmWithItem(SkavenEntity livingEntity, ItemStack itemStack, ModelTransformationMode transformType, Arm humanoidArm, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i) {
      if (!itemStack.isEmpty()) {
         poseStack.push();
         this.getContextModel().setArmAngle(humanoidArm, poseStack);
         poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
         poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

         boolean bl = false;
         poseStack.translate((float) 1 / 16.0F, 0.1, -0.625D);
         float f = 0.8F;
         poseStack.scale(f,f,f);
         this.heldItemRenderer.renderItem(livingEntity, itemStack, transformType, bl, poseStack, multiBufferSource, i);
         poseStack.pop();
      }
   }
   protected void renderArmWithItemleft(SkavenEntity livingEntity, ItemStack itemStack, ModelTransformationMode transformType, Arm humanoidArm, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i) {
      if (!itemStack.isEmpty()) {
         poseStack.push();
         ((ModelWithArms)this.getContextModel()).setArmAngle(humanoidArm, poseStack);
         poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
         poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
         boolean bl = humanoidArm == Arm.LEFT;
         poseStack.translate(-1  / 16.0F, 0, -0.625D);
         float f = 0.8F;
         poseStack.scale(f, f, f);
         this.heldItemRenderer.renderItem(livingEntity, itemStack, transformType, bl, poseStack, multiBufferSource, i);
         poseStack.pop();
      }
   }

   protected void renderArmWithItemcharging(SkavenEntity livingEntity, ItemStack itemStack, ModelTransformationMode transformType, Arm humanoidArm, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i) {
      if (!itemStack.isEmpty()) {
         poseStack.push();
         this.getContextModel().setArmAngle(humanoidArm, poseStack);

         //poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(50.0F));
         boolean bl = humanoidArm == Arm.LEFT;

         if(bl){
            poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-130));
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20));
            poseStack.translate(0.05, -0.2, -0.35);
         }else {
            poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-35));//-55
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-20));//-20
            poseStack.translate(0.05, 0.35, 0.15);
         }

         float f = 0.8F;
         poseStack.scale(f,f,f);
         this.heldItemRenderer.renderItem(livingEntity, itemStack, transformType, bl, poseStack, multiBufferSource, i);
         poseStack.pop();
      }
   }

   public static float translate_x=15;
   public static float translate_y=20;
   public static float translate_z=20;
   public static int rotx=-35;
   public static int roty=-20;
   public static int rotz=0;
}
