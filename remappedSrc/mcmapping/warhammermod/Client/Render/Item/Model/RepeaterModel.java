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

/**
 *
 * Created using Tabula 7.0.1
 */
@Environment(EnvType.CLIENT)
public class RepeaterModel extends Model {
    public ModelPart barrel_holder1;
    private final ModelPart root;

    public RepeaterModel(ModelPart modelPart) {
        super(RenderType::entitySolid);
        this.root = modelPart;
        this.barrel_holder1 =root.getChild("barrelholder1");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition barrelholder1 = partDefinition.addOrReplaceChild("barrelholder1", CubeListBuilder.create().texOffs(35, 25).addBox(-2, -2F, 0.0F, 4, 4, 1), PartPose.offset(0.5F, -2.0F, 8.0F));

        partDefinition.addOrReplaceChild("stock1", CubeListBuilder.create().texOffs(0, 25).addBox(0.0F, 0.0F, 0.0F, 1, 2, 2), PartPose.offset(0.0F, 0.0F, -2.0F));
        partDefinition.addOrReplaceChild("handle1", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, 0.0F, 0.0F, 1, 1, 8), PartPose.offset(0.0F, 0.0F, 0F));
        partDefinition.addOrReplaceChild("handle2", CubeListBuilder.create().texOffs(0, 25).addBox(0.0F, 0.0F, 0.0F, 1, 1, 1), PartPose.offset(0.0F, -1.0F, 1.0F));
        partDefinition.addOrReplaceChild("stockbase", CubeListBuilder.create().texOffs(0, 25).addBox(-1.0F, -2.0F, 0.0F, 3, 3, 1), PartPose.offset(0.0F, -1.0F, 2.0F));
        partDefinition.addOrReplaceChild("flint", CubeListBuilder.create().texOffs(57, 20).addBox(0.0F, 0.0F, 0.0F, 1, 1, 1), PartPose.offset(0.0F, -4.0F, 2.0F));
        partDefinition.addOrReplaceChild("stock", CubeListBuilder.create().texOffs(45, 0).addBox(-1.5F, 0.0F, 0.0F, 4, 4, 2), PartPose.offset(0.0F, -4.0F, 3.0F));
        barrelholder1.addOrReplaceChild("barrelholder2", CubeListBuilder.create().texOffs(35, 25).addBox(-1.5F, 0.0F, 0.0F, 4, 4, 1), PartPose.offset(-0.5F, -2F, 4.0F));
        barrelholder1.addOrReplaceChild("barrel1", CubeListBuilder.create().texOffs(0,0).addBox(0.0F, 0.0F, 0.0F, 1, 1, 9), PartPose.offset(-0.5F, -1.9F, -3F));
        barrelholder1.addOrReplaceChild("barrel2", CubeListBuilder.create().texOffs(0,0).addBox(-0.5F, -0.5F, 0.0F, 1, 1, 9), PartPose.offsetAndRotation(-1.2F, -0.7F, -3F,0.0F, 0.0F, 0.5235987755982988F));
        barrelholder1.addOrReplaceChild("barrel3", CubeListBuilder.create().texOffs(0,0).addBox(-0.5F, -0.5F, 0.0F, 1, 1, 9), PartPose.offsetAndRotation(-1.2F, 0.7F, -3F,0.0F, 0.0F, -0.5235987755982988F));
        barrelholder1.addOrReplaceChild("barrel4", CubeListBuilder.create().texOffs(0,0).addBox(0.0F, 0.0F, 0.0F, 1, 1, 9), PartPose.offset(-0.5F, 0.9F, -3F));
        barrelholder1.addOrReplaceChild("barrel5", CubeListBuilder.create().texOffs(0,0).addBox(-0.5F, -0.5F, 0.0F, 1, 1, 9), PartPose.offsetAndRotation(1.2F, 0.7F, -3F,0.0F, 0.0F, 0.5235987755982988F));
        barrelholder1.addOrReplaceChild("barrel6", CubeListBuilder.create().texOffs(0,0).addBox(-0.5F, -0.5F, 0.0F, 1, 1, 9), PartPose.offsetAndRotation(1.2F, -0.7F, -3F,0.0F, 0.0F, -0.5235987755982988F));

        return LayerDefinition.create(meshDefinition, 64, 32);
    }
    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
