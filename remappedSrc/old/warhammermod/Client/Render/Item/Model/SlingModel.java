package warhammermod.Client.Render.Item.Model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
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
        super(RenderType::entitySolid);

        this.cord1 =modelPart.getChild("cord1");
        this.Cord2 = modelPart.getChild("cord2");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition cord1 = partDefinition.addOrReplaceChild("cord1", CubeListBuilder.create().texOffs(0,0)
                .addBox(0.0F, 0.0F, 0.0F, 2, 1, 1)
                .addBox(-1F, 1F, 0.0F, 2, 1, 1)
                        .addBox(0.0F, 2F, 0.0F, 1, 2, 1)
                        .addBox(-2F, 2F, 0.0F, 1, 1, 1)
                        .addBox(-1F, 4F, 0.0F, 1, 8, 1)
                        .addBox(-3F, 3F, 0.0F, 1, 7, 1)
                        .addBox(-4F, 10F, 0.0F, 1, 2, 1), PartPose.ZERO);
        cord1.addOrReplaceChild("back1",CubeListBuilder.create().texOffs(15, 0).addBox(-4F, 12F, 0.0F, 4, 1, 1).addBox(-3F, 13F, 0.0F, 2, 1, 1),PartPose.ZERO);

        PartDefinition cord2 = partDefinition.addOrReplaceChild("cord2", CubeListBuilder.create().texOffs(0,0).addBox(0,0,0,1,11,1)
                .addBox(-1,4,0,1,6,1)
                .addBox(1,11,0,1,2,1)
                .addBox(-2,10,0,1,3,1)
                , PartPose.ZERO);
        cord2.addOrReplaceChild("back2",CubeListBuilder.create().texOffs(15, 0)
                .addBox(1,13,0,1,1,1)
                .addBox(-2,13,0,1,1,1)
                .addBox(-1,14,0,2,1,1),PartPose.ZERO);
        cord2.addOrReplaceChild("stone",CubeListBuilder.create().texOffs(0,0).addBox(-1,12,0,2,2,1),PartPose.ZERO);

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, ItemStack stack) {
        int usetick = stack.getOrDefault(WHRegistry.Fireorder, firecomponent.DEFAULT).firecount();
        if(stack.getOrDefault(WHRegistry.Fireorder, firecomponent.DEFAULT).firecount()>0){
            this.Cord2.render(matrices, vertices, light, overlay, color);
            Cord2.zRot+=0.25*((float)usetick /20);
        }
        else{
            this.cord1.render(matrices, vertices, light, overlay, color);
        }
    }


    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.cord1.render(matrices, vertices, light, overlay, color);
    }
}
