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
import warhammermod.Client.Render.Entity.projectiles.Render.Model.GrenadeModel;
import warhammermod.Entities.Projectile.GrenadeEntity;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class GrenadeRender extends EntityRenderer<GrenadeEntity> {
    private final GrenadeModel model;
    private static final ResourceLocation grenade_texture = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/grenade.png");

    public GrenadeRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new GrenadeModel(context.bakeLayer(Clientside.Grenade));
    }
    public void render(GrenadeEntity grenadeEntity, float f, float g, PoseStack matrixstack, MultiBufferSource vertexconsumerProvider, int i) {
        matrixstack.pushPose();
        matrixstack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, grenadeEntity.yRotO, grenadeEntity.getYRot()) - 90.0F));
        matrixstack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, grenadeEntity.xRotO, grenadeEntity.getXRot()) + 90.0F));
        VertexConsumer ivertexbuilder = vertexconsumerProvider.getBuffer(this.model.renderType(grenade_texture));
        this.model.renderToBuffer(matrixstack, ivertexbuilder, i, OverlayTexture.NO_OVERLAY);
        matrixstack.popPose();
        super.render(grenadeEntity, f, g, matrixstack, vertexconsumerProvider, i);
    }

    @Override
    public ResourceLocation getTexture(GrenadeEntity entity) {
        return grenade_texture;
    }
}
