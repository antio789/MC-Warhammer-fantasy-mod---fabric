package warhammermod.Client.Render.Entity.Renders.Layers;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import warhammermod.Client.Render.Entity.Renders.Models.Pegasusmodel;
import warhammermod.Entities.Living.PegasusEntity;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class PegasusMarkingsLayer extends FeatureRenderer<PegasusEntity, Pegasusmodel<PegasusEntity>> {
   private static final Map<HorseMarking, Identifier> LOCATION_BY_MARKINGS = Util.make(Maps.newEnumMap(HorseMarking.class), (map) -> {
      map.put(HorseMarking.NONE, null);
      map.put(HorseMarking.WHITE, Identifier.of("textures/entity/horse/horse_markings_white.png"));
      map.put(HorseMarking.WHITE_FIELD, Identifier.of("textures/entity/horse/horse_markings_whitefield.png"));
      map.put(HorseMarking.WHITE_DOTS, Identifier.of("textures/entity/horse/horse_markings_whitedots.png"));
      map.put(HorseMarking.BLACK_DOTS, Identifier.of("textures/entity/horse/horse_markings_blackdots.png"));
   });

   public PegasusMarkingsLayer(FeatureRendererContext<PegasusEntity, Pegasusmodel<PegasusEntity>> context) {
      super(context);
   }

   public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, PegasusEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      Identifier resourcelocation = LOCATION_BY_MARKINGS.get(entitylivingbaseIn.getMarking());
      if (resourcelocation != null && !entitylivingbaseIn.isInvisible()) {
         VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(resourcelocation));
         this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlay((LivingEntity) entitylivingbaseIn, 0.0F));
      }
   }
}