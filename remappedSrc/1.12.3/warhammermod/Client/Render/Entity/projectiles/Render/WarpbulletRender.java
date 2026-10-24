package warhammermod.Client.Render.Entity.projectiles.Render;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.WarpBulletModel;
import warhammermod.Entities.Projectile.WarpBulletEntity;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class WarpbulletRender extends EntityRenderer<WarpBulletEntity> {
    private static final Identifier BULLET_TEXTURE = Identifier.of(reference.modid,"textures/entity/bullet.png");
    private final WarpBulletModel model;

    public WarpbulletRender(EntityRendererFactory.Context context) {
        super(context);
        model=new WarpBulletModel(context.getPart(Clientside.WarpBullet));
    }

    public void render(WarpBulletEntity bulletEntity, float f, float g, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerProvider, int i) {
        matrixstack.push();
        matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, bulletEntity.prevYaw, bulletEntity.getYaw()) - 90.0F));
        matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, bulletEntity.prevPitch, bulletEntity.getPitch()) + 90.0F));
        VertexConsumer ivertexbuilder =  vertexconsumerProvider.getBuffer(this.model.getLayer(BULLET_TEXTURE));
        this.model.render(matrixstack, ivertexbuilder, i, OverlayTexture.DEFAULT_UV);
        matrixstack.pop();
        super.render(bulletEntity, f, g, matrixstack, vertexconsumerProvider, i);

    }

    @Override
    public Identifier getTexture(WarpBulletEntity entity) {
        return BULLET_TEXTURE;
    }
}
