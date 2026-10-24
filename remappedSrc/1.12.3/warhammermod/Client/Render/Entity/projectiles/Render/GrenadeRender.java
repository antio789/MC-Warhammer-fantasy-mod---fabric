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
import warhammermod.Client.Render.Entity.projectiles.Render.Model.GrenadeModel;
import warhammermod.Entities.Projectile.GrenadeEntity;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class GrenadeRender extends EntityRenderer<GrenadeEntity> {
    private final GrenadeModel model;
    private static final Identifier grenade_texture = Identifier.of(reference.modid,"textures/entity/grenade.png");

    public GrenadeRender(EntityRendererFactory.Context context) {
        super(context);
        this.model = new GrenadeModel(context.getPart(Clientside.Grenade));
    }
    public void render(GrenadeEntity grenadeEntity, float f, float g, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerProvider, int i) {
        matrixstack.push();
        matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, grenadeEntity.prevYaw, grenadeEntity.getYaw()) - 90.0F));
        matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, grenadeEntity.prevPitch, grenadeEntity.getPitch()) + 90.0F));
        VertexConsumer ivertexbuilder = vertexconsumerProvider.getBuffer(this.model.getLayer(grenade_texture));
        this.model.render(matrixstack, ivertexbuilder, i, OverlayTexture.DEFAULT_UV);
        matrixstack.pop();
        super.render(grenadeEntity, f, g, matrixstack, vertexconsumerProvider, i);
    }

    @Override
    public Identifier getTexture(GrenadeEntity entity) {
        return grenade_texture;
    }
}
