package warhammermod.Client.Render.Entity.Renders.Layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import warhammermod.Client.Render.Entity.Renders.Models.Pegasusmodel;
import warhammermod.Entities.Living.PegasusEntity;

@Environment(EnvType.CLIENT)
public class PegasusArmorFeatureRenderer extends RenderLayer<PegasusEntity, Pegasusmodel<PegasusEntity>> {
   private final HorseModel<PegasusEntity> model;

   public PegasusArmorFeatureRenderer(RenderLayerParent<PegasusEntity, Pegasusmodel<PegasusEntity>> renderLayerParent, EntityModelSet entityModelSet) {
      super(renderLayerParent);
      this.model = new HorseModel<>(entityModelSet.bakeLayer(ModelLayers.HORSE_ARMOR));
   }

   @Override
   public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, PegasusEntity horseEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
      AnimalArmorItem animalArmorItem;
      ItemStack itemStack = horseEntity.getBodyArmorItem();
      Item item = itemStack.getItem();
      if (!(item instanceof AnimalArmorItem) || (animalArmorItem = (AnimalArmorItem)item).getBodyType() != AnimalArmorItem.BodyType.EQUESTRIAN) {
         return;
      }
      this.getParentModel().copyPropertiesTo(this.model);
      this.model.prepareMobModel(horseEntity, limbAngle, limbDistance, tickDelta);
      this.model.setupAnim(horseEntity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
      int m = itemStack.is(ItemTags.DYEABLE) ? FastColor.ARGB32.opaque((int) DyedItemColor.getOrDefault((ItemStack)itemStack, (int)-6265536)) : -1;
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(animalArmorItem.getTexture()));
      this.model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, m);
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
