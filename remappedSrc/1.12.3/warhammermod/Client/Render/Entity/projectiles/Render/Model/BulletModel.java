package warhammermod.Client.Render.Entity.projectiles.Render.Model;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class BulletModel<T extends Entity> extends Model {
    public ModelPart bullet;

    public BulletModel(ModelPart modelPart) {
        super(RenderLayer::getEntitySolid);
        this.bullet = modelPart;
    }

    public static TexturedModelData createLayer() {
        ModelData meshDefinition = new ModelData();
        ModelPartData partDefinition = meshDefinition.getRoot();
        partDefinition.addChild("bullet", ModelPartBuilder.create().uv(0,0).cuboid(-1.0F, -1.0F, -1.0F, 2, 2, 2), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(meshDefinition, 64, 32);
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.bullet.render(matrices, vertices, light, overlay, color);
    }
}