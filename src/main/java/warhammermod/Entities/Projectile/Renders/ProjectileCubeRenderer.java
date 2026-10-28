package warhammermod.Entities.Projectile.Renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;

/**
 * Simple renderer for the mod's cube-shaped projectiles (bullet, warp bullet, grapeshot).
 * Follows the vanilla LlamaSpitRenderer pattern: rotate to the projectile's flight
 * direction and submit the {@link ProjectileModel} cube.
 */
public class ProjectileCubeRenderer<T extends Projectile> extends EntityRenderer<T, ProjectileCubeRenderer.CubeRenderState> {

    public static class CubeRenderState extends EntityRenderState {
        public float yRot;
        public float xRot;
    }

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/bullet.png");
    private final ProjectileModel model;

    public ProjectileCubeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ProjectileModel(context.bakeLayer(warhammermod.utils.Clientside.Bullet));
    }

    @Override
    public CubeRenderState createRenderState() {
        return new CubeRenderState();
    }

    @Override
    public void extractRenderState(T entity, CubeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
    }

    @Override
    public void submit(CubeRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(state.yRot - 90.0F));
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(state.xRot + 90.0F));
        collector.submitModel(this.model, new ProjectileModel.State(), poseStack, TEXTURE,
                state.lightCoords, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poseStack.popPose();
        super.submit(state, poseStack, collector, cameraState);
    }
}
