package warhammermod.Client.Render.Entity.projectiles.Render;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.BulletModel;
import warhammermod.Entities.Projectile.Bullet;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class BulletRender extends EntityRenderer<Bullet> {
    private static final Identifier BULLET_TEXTURE = Identifier.of(reference.modid,"textures/entity/bullet.png");
    private final BulletModel model;

    public BulletRender(EntityRendererFactory.Context context) {
        super(context);
        this.model = new BulletModel(context.getPart(Clientside.Bullet));
    }

    public void render(Bullet bulletEntity, float f, float g, MatrixStack matrixstack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixstack.push();
        matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, bulletEntity.prevYaw, bulletEntity.getYaw()) - 90.0F));
        matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, bulletEntity.prevPitch, bulletEntity.getPitch()) + 90.0F));
        VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(this.getTexture(bulletEntity)), false,false);
        this.model.render(matrixstack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixstack.pop();
        super.render(bulletEntity, f, g, matrixstack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(Bullet entity) {
        return BULLET_TEXTURE;
    }
}
