package warhammermod.Client.Render.Entity.projectiles.Render.Model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
@Environment(EnvType.CLIENT)
public class GrenadeModel<T extends Entity> extends Model {
    public ModelPart grenadepart1;
    public ModelPart grenadetop;
    public ModelPart root;

    public GrenadeModel(ModelPart modelPart) {
        super(RenderLayer::getEntitySolid);
        this.root = modelPart;
    }

    public static TexturedModelData createLayer() {
        ModelData meshDefinition = new ModelData();
        ModelPartData partDefinition = meshDefinition.getRoot();
        partDefinition.addChild("grenadetop", ModelPartBuilder.create().uv(3, 0).cuboid(-1.5F, -1.5F, -1.5F, 3, 3, 3), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        partDefinition.addChild("grenadepart", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, 1.5F, -0.5F, 1, 1, 1), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(meshDefinition, 64, 32);
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
