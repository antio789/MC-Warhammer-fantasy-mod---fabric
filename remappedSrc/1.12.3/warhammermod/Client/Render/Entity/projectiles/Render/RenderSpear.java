package warhammermod.Client.Render.Entity.projectiles.Render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import warhammermod.Entities.Projectile.SpearEntity;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class RenderSpear<T extends SpearEntity> extends ProjectileEntityRenderer<SpearEntity> {
   public RenderSpear(EntityRendererFactory.Context context) {
      super(context);
   }

   @Override
   public Identifier getTexture(SpearEntity entity) {
      return SPEAR_TEXTURE;
   }

   public static final Identifier SPEAR_TEXTURE = Identifier.of(reference.modid,"textures/entity/spear.png");

}