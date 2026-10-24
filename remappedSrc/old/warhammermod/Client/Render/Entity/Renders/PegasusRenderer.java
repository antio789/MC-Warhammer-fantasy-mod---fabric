package warhammermod.Client.Render.Entity.Renders;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Variant;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.Renders.Layers.PegasusArmorFeatureRenderer;
import warhammermod.Client.Render.Entity.Renders.Layers.PegasusMarkingsLayer;
import warhammermod.Client.Render.Entity.Renders.Models.Pegasusmodel;
import warhammermod.Entities.Living.PegasusEntity;
import warhammermod.utils.reference;

import java.util.Map;

@Environment(EnvType.CLIENT)
public final class PegasusRenderer extends AbstractHorseRenderer<PegasusEntity, Pegasusmodel<PegasusEntity>> {
   private static final Map<Variant, ResourceLocation> COAT_COLORS_RESOURCE_LOCATION_MAP = Util.make(Maps.newEnumMap(Variant.class), (map) -> {
      map.put(Variant.WHITE, ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/pegasus/pegasus_white.png"));
      map.put(Variant.CREAMY, ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/pegasus/pegasus_creamy.png"));
      map.put(Variant.CHESTNUT, ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/pegasus/pegasus_chestnut.png"));
      map.put(Variant.BROWN, ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/pegasus/pegasus_brown.png"));
      map.put(Variant.BLACK, ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/pegasus/pegasus_black.png"));
      map.put(Variant.GRAY, ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/pegasus/pegasus_gray.png"));
      map.put(Variant.DARK_BROWN, ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/pegasus/pegasus_darkbrown.png"));
   });


   private static final ResourceLocation PEGASUS_TEXTURE = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/pegasus.png");

   public PegasusRenderer(EntityRendererProvider.Context context) {
      super(context, new Pegasusmodel<>(context.bakeLayer(Clientside.Pegasus)), 1.1F);
      this.addLayer( new PegasusArmorFeatureRenderer(this, context.getModelSet()));
      this.addLayer(new PegasusMarkingsLayer(this));
   }

   @Override
   public ResourceLocation getTexture(PegasusEntity entity) {
      if(entity.ismixblood()) {
         return COAT_COLORS_RESOURCE_LOCATION_MAP.get(entity.getVariant());
      }
      return PEGASUS_TEXTURE;
   }
}