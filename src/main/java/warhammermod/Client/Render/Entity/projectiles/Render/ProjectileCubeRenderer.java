package warhammermod.Client.Render.Entity.projectiles.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.GrapeshotModel;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.GrenadeModel;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.ProjectileModel;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.WarpBulletModel;

/**
 * Renderers for the mod's cube-shaped projectiles (bullet, warp bullet,
 * grapeshot, grenade), following the vanilla LlamaSpitRenderer pattern:
 * rotate to the projectile's flight direction, submit the baked cube model.
 */
public final class ProjectileCubeRenderer {

    private ProjectileCubeRenderer() {
    }

    public static class ProjectileRenderState extends EntityRenderState {
        public float yRot;
        public float xRot;
        public float age;
    }

    private abstract static class Base<T extends Projectile> extends EntityRenderer<T, ProjectileRenderState> {
        protected final Model<? super ProjectileModel.State> model;
        protected final Identifier texture;

        protected Base(EntityRendererProvider.Context context, ModelLayerLocation layer,
                       java.util.function.Function<ModelPart, ? extends Model<? super ProjectileModel.State>> modelFactory,
                       Identifier texture) {
            super(context);
            this.model = modelFactory.apply(context.bakeLayer(layer));
            this.texture = texture;
        }

        @Override
        public ProjectileRenderState createRenderState() {
            return new ProjectileRenderState();
        }

        @Override
        public void extractRenderState(T entity, ProjectileRenderState state, float partialTick) {
            super.extractRenderState(entity, state, partialTick);
            state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
            state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
            state.age = entity.tickCount + partialTick;
        }

        @Override
        public void submit(ProjectileRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot + 90.0F));
            submitModel(state, poseStack, collector);
            poseStack.popPose();
            super.submit(state, poseStack, collector, cameraState);
        }

        protected void submitModel(ProjectileRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
            ProjectileModel.State modelState = new ProjectileModel.State();
            fillModelState(state, modelState);
            collector.submitModel(model, modelState, poseStack, texture, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }

        protected void fillModelState(ProjectileRenderState state, ProjectileModel.State modelState) {
        }
    }

    public static class BulletRenderer extends Base<warhammermod.Entities.Projectile.Bullet> {
        public BulletRenderer(EntityRendererProvider.Context context) {
            super(context, Clientside.Bullet, ProjectileModel::new,
                    Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/bullet.png"));
        }
    }

    public static class WarpBulletRenderer extends Base<warhammermod.Entities.Projectile.WarpBulletEntity> {
        public WarpBulletRenderer(EntityRendererProvider.Context context) {
            super(context, Clientside.WarpBullet, WarpBulletModel::new,
                    Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/bullet.png"));
        }
    }

    public static class GrenadeRenderer extends Base<warhammermod.Entities.Projectile.GrenadeEntity> {
        public GrenadeRenderer(EntityRendererProvider.Context context) {
            super(context, Clientside.Grenade, GrenadeModel::new,
                    Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/grenade.png"));
        }
    }

    public static class ShotRenderer extends Base<warhammermod.Entities.Projectile.ShotEntity> {
        public ShotRenderer(EntityRendererProvider.Context context) {
            super(context, Clientside.Grapeshot, GrapeshotModel::new,
                    Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/bullet.png"));
        }
    }
}
