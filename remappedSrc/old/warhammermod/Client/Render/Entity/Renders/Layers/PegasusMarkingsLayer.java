package warhammermod.Client.Render.Entity.Renders.Layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Markings;
import warhammermod.Client.Render.Entity.Renders.Models.Pegasusmodel;
import warhammermod.Entities.Living.PegasusEntity;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class PegasusMarkingsLayer extends RenderLayer<PegasusEntity, Pegasusmodel<PegasusEntity>> {
   private static final Map<Markings, ResourceLocation> LOCATION_BY_MARKINGS = Util.make(Maps.newEnumMap(Markings.class), (map) -> {
      map.put(Markings.NONE, null);
      map.put(Markings.WHITE, ResourceLocation.parse("textures/entity/horse/horse_markings_white.png"));
      map.put(Markings.WHITE_FIELD, ResourceLocation.parse("textures/entity/horse/horse_markings_whitefield.png"));
      map.put(Markings.WHITE_DOTS, ResourceLocation.parse("textures/entity/horse/horse_markings_whitedots.png"));
      map.put(Markings.BLACK_DOTS, ResourceLocation.parse("textures/entity/horse/horse_markings_blackdots.png"));
   });

   public PegasusMarkingsLayer(RenderLayerParent<PegasusEntity, Pegasusmodel<PegasusEntity>> context) {
      super(context);
   }

   public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, PegasusEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      ResourceLocation resourcelocation = LOCATION_BY_MARKINGS.get(entitylivingbaseIn.getMarkings());
      if (resourcelocation != null && !entitylivingbaseIn.isInvisible()) {
         VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(resourcelocation));
         this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords((LivingEntity) entitylivingbaseIn, 0.0F));
      }
   }
}