package warhammermod.Client.Render.Entity.projectiles.Render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import warhammermod.Entities.Projectile.HalberdEntity;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class HalberdRender extends EntityRenderer<HalberdEntity> {
    private static final Identifier BULLET_TEXTURE = Identifier.of(reference.modid,"textures/entity/bullet.png");


    public HalberdRender(EntityRendererFactory.Context renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(HalberdEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, VertexConsumerProvider p_225623_5_, int p_225623_6_) {

    }

    @Override
    public Identifier getTexture(HalberdEntity entity) {
        return BULLET_TEXTURE;
    }

}
