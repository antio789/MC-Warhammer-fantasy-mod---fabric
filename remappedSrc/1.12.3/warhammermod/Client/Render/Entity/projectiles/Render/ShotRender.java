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
import warhammermod.Client.Render.Entity.projectiles.Render.Model.GrapeshotModel;
import warhammermod.Entities.Projectile.ShotEntity;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class ShotRender extends EntityRenderer<ShotEntity> {
    private static final Identifier BULLET_TEXTURE = Identifier.of(reference.modid,"textures/entity/bullet.png");
    private final GrapeshotModel model;

    public ShotRender(EntityRendererFactory.Context context) {
        super(context);
        this.model = new GrapeshotModel(context.getPart(Clientside.Grapeshot));
    }

    public void render(ShotEntity shotEntity, float f, float g, MatrixStack matrixstack, VertexConsumerProvider VertexconsumerProvider, int i) {
        matrixstack.push();
        matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, shotEntity.prevYaw, shotEntity.getYaw()) - 90.0F));
        matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, shotEntity.prevPitch, shotEntity.getPitch()) + 90.0F));
        VertexConsumer ivertexbuilder = VertexconsumerProvider.getBuffer(this.model.getLayer(BULLET_TEXTURE));
        this.model.render(matrixstack, ivertexbuilder, i, OverlayTexture.DEFAULT_UV);
        matrixstack.pop();
        super.render(shotEntity, f, g, matrixstack, VertexconsumerProvider, i);
    }

    @Override
    public Identifier getTexture(ShotEntity entity) {
        return BULLET_TEXTURE;
    }
}
