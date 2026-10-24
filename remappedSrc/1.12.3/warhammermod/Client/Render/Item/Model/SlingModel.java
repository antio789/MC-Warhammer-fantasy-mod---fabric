package warhammermod.Client.Render.Item.Model;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import warhammermod.Items.firecomponent;
import warhammermod.Items.ranged.SlingTemplate;
import warhammermod.utils.Registry.WHRegistry;

@Environment(EnvType.CLIENT)
public class SlingModel extends Model {

    ModelPart cord1;
    ModelPart Back1;
    ModelPart Cord2;
    ModelPart Back2;
    ModelPart Stone;
    public SlingModel(ModelPart modelPart)
    {
        super(RenderLayer::getEntitySolid);

        this.cord1 =modelPart.getChild("cord1");
        this.Cord2 = modelPart.getChild("cord2");
    }

    public static TexturedModelData createLayer() {
        ModelData meshDefinition = new ModelData();
        ModelPartData partDefinition = meshDefinition.getRoot();
        ModelPartData cord1 = partDefinition.addChild("cord1", ModelPartBuilder.create().uv(0,0)
                .cuboid(0.0F, 0.0F, 0.0F, 2, 1, 1)
                .cuboid(-1F, 1F, 0.0F, 2, 1, 1)
                        .cuboid(0.0F, 2F, 0.0F, 1, 2, 1)
                        .cuboid(-2F, 2F, 0.0F, 1, 1, 1)
                        .cuboid(-1F, 4F, 0.0F, 1, 8, 1)
                        .cuboid(-3F, 3F, 0.0F, 1, 7, 1)
                        .cuboid(-4F, 10F, 0.0F, 1, 2, 1), ModelTransform.NONE);
        cord1.addChild("back1",ModelPartBuilder.create().uv(15, 0).cuboid(-4F, 12F, 0.0F, 4, 1, 1).cuboid(-3F, 13F, 0.0F, 2, 1, 1),ModelTransform.NONE);

        ModelPartData cord2 = partDefinition.addChild("cord2", ModelPartBuilder.create().uv(0,0).cuboid(0,0,0,1,11,1)
                .cuboid(-1,4,0,1,6,1)
                .cuboid(1,11,0,1,2,1)
                .cuboid(-2,10,0,1,3,1)
                , ModelTransform.NONE);
        cord2.addChild("back2",ModelPartBuilder.create().uv(15, 0)
                .cuboid(1,13,0,1,1,1)
                .cuboid(-2,13,0,1,1,1)
                .cuboid(-1,14,0,2,1,1),ModelTransform.NONE);
        cord2.addChild("stone",ModelPartBuilder.create().uv(0,0).cuboid(-1,12,0,2,2,1),ModelTransform.NONE);

        return TexturedModelData.of(meshDefinition, 64, 64);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color, ItemStack stack) {
        int usetick = stack.getOrDefault(WHRegistry.Fireorder, firecomponent.DEFAULT).firecount();
        if(stack.getOrDefault(WHRegistry.Fireorder, firecomponent.DEFAULT).firecount()>0){
            this.Cord2.render(matrices, vertices, light, overlay, color);
            Cord2.roll+=0.25*((float)usetick /20);
        }
        else{
            this.cord1.render(matrices, vertices, light, overlay, color);
        }
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.cord1.render(matrices, vertices, light, overlay, color);
    }
}
