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
import warhammermod.Client.Render.Entity.projectiles.Render.Model.GrapeshotModel;
import warhammermod.Entities.Projectile.ShotEntity;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class ShotRender extends EntityRenderer<ShotEntity> {
    private static final ResourceLocation BULLET_TEXTURE = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/bullet.png");
    private final GrapeshotModel model;

    public ShotRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new GrapeshotModel(context.bakeLayer(Clientside.Grapeshot));
    }

    public void render(ShotEntity shotEntity, float f, float g, PoseStack matrixstack, MultiBufferSource VertexconsumerProvider, int i) {
        matrixstack.pushPose();
        matrixstack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, shotEntity.yRotO, shotEntity.getYRot()) - 90.0F));
        matrixstack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, shotEntity.xRotO, shotEntity.getXRot()) + 90.0F));
        VertexConsumer ivertexbuilder = VertexconsumerProvider.getBuffer(this.model.renderType(BULLET_TEXTURE));
        this.model.renderToBuffer(matrixstack, ivertexbuilder, i, OverlayTexture.NO_OVERLAY);
        matrixstack.popPose();
        super.render(shotEntity, f, g, matrixstack, VertexconsumerProvider, i);
    }

    @Override
    public ResourceLocation getTexture(ShotEntity entity) {
        return BULLET_TEXTURE;
    }
}
