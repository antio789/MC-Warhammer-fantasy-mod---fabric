package warhammermod.Client.Render.Entity.Renders.Layers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.AbstractHorseEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.ColorHelper;
import warhammermod.Client.Render.Entity.Renders.Models.Pegasusmodel;
import warhammermod.Entities.Living.PegasusEntity;

@Environment(EnvType.CLIENT)
public class PegasusArmorFeatureRenderer extends FeatureRenderer<PegasusEntity, Pegasusmodel<PegasusEntity>> {
   private final AbstractHorseEntityModel<PegasusEntity> model;

   public PegasusArmorFeatureRenderer(FeatureRendererContext<PegasusEntity, Pegasusmodel<PegasusEntity>> renderLayerParent, EntityModelLoader entityModelSet) {
      super(renderLayerParent);
      this.model = new AbstractHorseEntityModel<>(entityModelSet.getModelPart(EntityModelLayers.HORSE_ARMOR));
   }

   @Override
   public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PegasusEntity horseEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
      AnimalArmorItem animalArmorItem;
      ItemStack itemStack = horseEntity.getBodyArmor();
      Item item = itemStack.getItem();
      if (!(item instanceof AnimalArmorItem) || (animalArmorItem = (AnimalArmorItem)item).getType() != AnimalArmorItem.Type.EQUESTRIAN) {
         return;
      }
      this.getContextModel().copyStateTo(this.model);
      this.model.animateModel(horseEntity, limbAngle, limbDistance, tickDelta);
      this.model.setAngles(horseEntity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
      int m = itemStack.isIn(ItemTags.DYEABLE) ? ColorHelper.Argb.fullAlpha((int) DyedColorComponent.getColor((ItemStack)itemStack, (int)-6265536)) : -1;
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(animalArmorItem.getEntityTexture()));
      this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, m);
   }
/*
   public void render(MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, PegasusEntity horse, float f, float g, float h, float j, float k, float l) {
      ItemStack itemStack = horse.getArmorType();
      if (itemStack.getItem() instanceof HorseArmorItem) {
         HorseArmorItem horseArmorItem = (HorseArmorItem)itemStack.getItem();
         (this.getContextModel()).copyStateTo(this.model);
         this.model.prepareMobModel(horse, f, g, h);
         this.model.setAngles(horse, f, g, j, k, l);
         float q;
         float r;
         float s;
         if (horseArmorItem instanceof DyeableHorseArmorItem) {
            int m = ((DyeableHorseArmorItem)horseArmorItem).getColor(itemStack);
            q = (float)(m >> 16 & 255) / 255.0F;
            r = (float)(m >> 8 & 255) / 255.0F;
            s = (float)(m & 255) / 255.0F;
         } else {
            q = 1.0F;
            r = 1.0F;
            s = 1.0F;
         }

         VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderLayer.getEntityCutoutNoCull(horseArmorItem.getEntityTexture()));
         this.model.render(poseStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, q, r, s, 1.0F);
      }
   }
   */
}
