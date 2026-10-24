package warhammermod.Client.Render.Entity.projectiles.Render;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.WarpBulletModel;
import warhammermod.Entities.Projectile.WarpBulletEntity;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class WarpbulletRender extends EntityRenderer<WarpBulletEntity> {
    private static final ResourceLocation BULLET_TEXTURE = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/bullet.png");
    private final WarpBulletModel model;

    public WarpbulletRender(EntityRendererProvider.Context context) {
        super(context);
        model=new WarpBulletModel(context.bakeLayer(Clientside.WarpBullet));
    }

    public void render(WarpBulletEntity bulletEntity, float f, float g, PoseStack matrixstack, MultiBufferSource vertexconsumerProvider, int i) {
        matrixstack.pushPose();
        matrixstack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, bulletEntity.yRotO, bulletEntity.getYRot()) - 90.0F));
        matrixstack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, bulletEntity.xRotO, bulletEntity.getXRot()) + 90.0F));
        VertexConsumer ivertexbuilder =  vertexconsumerProvider.getBuffer(this.model.renderType(BULLET_TEXTURE));
        this.model.renderToBuffer(matrixstack, ivertexbuilder, i, OverlayTexture.NO_OVERLAY);
        matrixstack.popPose();
        super.render(bulletEntity, f, g, matrixstack, vertexconsumerProvider, i);

    }

    @Override
    public ResourceLocation getTexture(WarpBulletEntity entity) {
        return BULLET_TEXTURE;
    }
}
