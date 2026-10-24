package warhammermod.Client.Render.Entity.projectiles.Render;



import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.BulletModel;
import warhammermod.Entities.Projectile.Bullet;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class BulletRender extends EntityRenderer<Bullet> {
    private static final ResourceLocation BULLET_TEXTURE = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/bullet.png");
    private final BulletModel model;

    public BulletRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BulletModel(context.bakeLayer(Clientside.Bullet));
    }

    public void render(Bullet bulletEntity, float f, float g, PoseStack matrixstack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixstack.pushPose();
        matrixstack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, bulletEntity.yRotO, bulletEntity.getYRot()) - 90.0F));
        matrixstack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, bulletEntity.xRotO, bulletEntity.getXRot()) + 90.0F));
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(vertexConsumerProvider, this.model.renderType(this.getTexture(bulletEntity)), false,false);
        this.model.renderToBuffer(matrixstack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
        matrixstack.popPose();
        super.render(bulletEntity, f, g, matrixstack, vertexConsumerProvider, i);
    }

    @Override
    public ResourceLocation getTexture(Bullet entity) {
        return BULLET_TEXTURE;
    }
}
