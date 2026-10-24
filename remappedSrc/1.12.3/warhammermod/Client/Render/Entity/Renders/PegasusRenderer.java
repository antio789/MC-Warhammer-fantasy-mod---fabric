package warhammermod.Client.Render.Entity.Renders;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.Renders.Layers.PegasusArmorFeatureRenderer;
import warhammermod.Client.Render.Entity.Renders.Layers.PegasusMarkingsLayer;
import warhammermod.Client.Render.Entity.Renders.Models.Pegasusmodel;
import warhammermod.Entities.Living.PegasusEntity;
import warhammermod.utils.reference;

import java.util.Map;

@Environment(EnvType.CLIENT)
public final class PegasusRenderer extends AbstractHorseEntityRenderer<PegasusEntity, Pegasusmodel<PegasusEntity>> {
   private static final Map<HorseColor, Identifier> COAT_COLORS_RESOURCE_LOCATION_MAP = Util.make(Maps.newEnumMap(HorseColor.class), (map) -> {
      map.put(HorseColor.WHITE, Identifier.of(reference.modid,"textures/entity/pegasus/pegasus_white.png"));
      map.put(HorseColor.CREAMY, Identifier.of(reference.modid,"textures/entity/pegasus/pegasus_creamy.png"));
      map.put(HorseColor.CHESTNUT, Identifier.of(reference.modid,"textures/entity/pegasus/pegasus_chestnut.png"));
      map.put(HorseColor.BROWN, Identifier.of(reference.modid,"textures/entity/pegasus/pegasus_brown.png"));
      map.put(HorseColor.BLACK, Identifier.of(reference.modid,"textures/entity/pegasus/pegasus_black.png"));
      map.put(HorseColor.GRAY, Identifier.of(reference.modid,"textures/entity/pegasus/pegasus_gray.png"));
      map.put(HorseColor.DARK_BROWN, Identifier.of(reference.modid,"textures/entity/pegasus/pegasus_darkbrown.png"));
   });


   private static final Identifier PEGASUS_TEXTURE = Identifier.of(reference.modid,"textures/entity/pegasus.png");

   public PegasusRenderer(EntityRendererFactory.Context context) {
      super(context, new Pegasusmodel<>(context.getPart(Clientside.Pegasus)), 1.1F);
      this.addFeature( new PegasusArmorFeatureRenderer(this, context.getModelLoader()));
      this.addFeature(new PegasusMarkingsLayer(this));
   }

   @Override
   public Identifier getTexture(PegasusEntity entity) {
      if(entity.ismixblood()) {
         return COAT_COLORS_RESOURCE_LOCATION_MAP.get(entity.getVariant());
      }
      return PEGASUS_TEXTURE;
   }
}