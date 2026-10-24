package warhammermod.Client.Render.Item.Model;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

/**
 * ModelPlayer
 * Created using Tabula 7.0.1
 */
@Environment(EnvType.CLIENT)
public class RatlingGunModel extends Model {
    public ModelPart cannonback;
    private final ModelPart root;

    public RatlingGunModel(ModelPart modelPart)
    {
        super(RenderLayer::getEntitySolid);
        this.root = modelPart;
        this.cannonback =root.getChild("cannonback");
    }

    public static TexturedModelData createLayer() {
        ModelData meshDefinition = new ModelData();
        ModelPartData partDefinition = meshDefinition.getRoot();

        Dilation barrelsize = new Dilation(0.1F);
        Dilation f03 = new Dilation(0.3F);
        Dilation f02 = new Dilation(0.2F);

        ModelPartData cannonback = partDefinition.addChild("cannonback", ModelPartBuilder.create().uv(45, 16).cuboid(-2.0F, -2.0F, 0.0F, 4, 4, 1,f03), ModelTransform.pivot(0.0F, 10.8F, -15.0F));

        partDefinition.addChild("recipient",ModelPartBuilder.create().uv(0, 26).cuboid(-1.5F, -2.5F, -1.5F, 3, 5, 3), ModelTransform.pivot(0.0F, 12.0F, -9.5F));
        cannonback.addChild("cannon1", ModelPartBuilder.create().uv(35,0).cuboid(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), ModelTransform.pivot(0.0F, -1.7F, -8.0F));
        cannonback.addChild("cannon2", ModelPartBuilder.create().uv(35,0).cuboid(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), ModelTransform.of(1.4F, -0.9F, -8.0F,0.0F, 0.0F, -0.5235987755982988F));
        cannonback.addChild("cannon3", ModelPartBuilder.create().uv(35,0).cuboid(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), ModelTransform.of(1.4F, 0.7F, -8.0F,0.0F, 0.0F, 0.5235987755982988F));
        cannonback.addChild("cannon4", ModelPartBuilder.create().uv(35,0).cuboid(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), ModelTransform.pivot(0.0F, 1.5F, -8.0F));
        cannonback.addChild("cannon5", ModelPartBuilder.create().uv(35,0).cuboid(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), ModelTransform.of(-1.4F, 0.7F, -8.0F,0.0F, 0.0F, -0.5235987755982988F));
        cannonback.addChild("cannon6", ModelPartBuilder.create().uv(35,0).cuboid(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), ModelTransform.of(-1.4F, -0.9F, -8.0F,0.0F, 0.0F, 0.5235987755982988F));
        cannonback.addChild("cannonfront", ModelPartBuilder.create().uv(45, 16).cuboid(-2.0F, -2.0F, 0.0F, 4, 4, 1,f03), ModelTransform.pivot(0.0F, -0.1F, -10.0F));

        partDefinition.addChild("core1", ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -2.5F, -3.0F, 5, 5, 6), ModelTransform.pivot(0.0F, 10.0F, -5.0F));
        partDefinition.addChild("core2", ModelPartBuilder.create().uv(0, 12).cuboid(-2.5F, -2.5F, -2.0F, 4, 5, 1), ModelTransform.pivot(0.5F, 10.0F, -12.0F));
        partDefinition.addChild("core3", ModelPartBuilder.create().uv(0, 19).cuboid(-1.5F, -1.5F, -1.0F, 3, 4, 2), ModelTransform.pivot(0F, 10.2F, -12.0F));

        partDefinition.addChild("tube1", ModelPartBuilder.create().uv(25, 35).cuboid(0.0F, 0.0F, 0.0F, 1, 1, 1), ModelTransform.pivot(-0.5F, 8.5F, -10.0F));
        partDefinition.addChild("tube2", ModelPartBuilder.create().uv(25, 37).cuboid(0.0F, 0.0F, 0.0F, 1, 1, 4), ModelTransform.pivot(-0.5F, 7.5F, -13.0F));
        partDefinition.addChild("tube3", ModelPartBuilder.create().uv(23, 45).cuboid(0.0F, 0.0F, -2.0F, 1, 1, 6,f02), ModelTransform.pivot(-5.1F, 10.4F, -3.0F));
        partDefinition.addChild("tube4", ModelPartBuilder.create().uv(25, 35).cuboid(0.0F, 0.0F, 0.0F, 1, 1, 1,f02), ModelTransform.pivot(-3.7F, 10.4F, -5.0F));
        partDefinition.addChild("tube5", ModelPartBuilder.create().uv(25, 35).cuboid(0.0F, 0.0F, 0.0F, 1, 1, 1,f02), ModelTransform.pivot(-3.7F, 10.4F, 0.0F));
        partDefinition.addChild("handle1", ModelPartBuilder.create().uv(0, 50).cuboid(-0.5F, -1.0F, -0.5F, 1, 1, 1), ModelTransform.pivot(1.5F, 7.5F, -13.5F));
        partDefinition.addChild("handle2", ModelPartBuilder.create().uv(0,50).cuboid(-0.5F, -1.0F, -0.5F, 1, 1, 1), ModelTransform.pivot(-1.5F, 7.5F, -13.5F));
        partDefinition.addChild("handlebar", ModelPartBuilder.create().uv(0,50).cuboid(-2.0F, 0.0F, 0.0F, 4, 1, 1), ModelTransform.pivot(0.0F, 5.5F, -14.0F));
        partDefinition.addChild("handleback", ModelPartBuilder.create().uv(14,12).cuboid(0,0,0,2,2,4), ModelTransform.pivot(-1,13,3));
        partDefinition.addChild("container", ModelPartBuilder.create().uv(0, 35).cuboid(-2.5F, -4.0F, -2.5F, 5, 8, 5), ModelTransform.pivot(0.0F, 14.0F, 0.5F));


        return TexturedModelData.of(meshDefinition, 64, 64);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
