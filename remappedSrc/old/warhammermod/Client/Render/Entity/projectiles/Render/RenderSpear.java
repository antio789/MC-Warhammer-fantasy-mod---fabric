package warhammermod.Client.Render.Entity.projectiles.Render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import warhammermod.Entities.Projectile.SpearEntity;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class RenderSpear<T extends SpearEntity> extends ArrowRenderer<SpearEntity> {
   public RenderSpear(EntityRendererProvider.Context context) {
      super(context);
   }

   @Override
   public ResourceLocation getTexture(SpearEntity entity) {
      return SPEAR_TEXTURE;
   }

   public static final ResourceLocation SPEAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/spear.png");

}