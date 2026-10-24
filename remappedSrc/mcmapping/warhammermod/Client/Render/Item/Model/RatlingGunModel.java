package warhammermod.Client.Render.Item.Model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

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
        super(RenderType::entitySolid);
        this.root = modelPart;
        this.cannonback =root.getChild("cannonback");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        CubeDeformation barrelsize = new CubeDeformation(0.1F);
        CubeDeformation f03 = new CubeDeformation(0.3F);
        CubeDeformation f02 = new CubeDeformation(0.2F);

        PartDefinition cannonback = partDefinition.addOrReplaceChild("cannonback", CubeListBuilder.create().texOffs(45, 16).addBox(-2.0F, -2.0F, 0.0F, 4, 4, 1,f03), PartPose.offset(0.0F, 10.8F, -15.0F));

        partDefinition.addOrReplaceChild("recipient",CubeListBuilder.create().texOffs(0, 26).addBox(-1.5F, -2.5F, -1.5F, 3, 5, 3), PartPose.offset(0.0F, 12.0F, -9.5F));
        cannonback.addOrReplaceChild("cannon1", CubeListBuilder.create().texOffs(35,0).addBox(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), PartPose.offset(0.0F, -1.7F, -8.0F));
        cannonback.addOrReplaceChild("cannon2", CubeListBuilder.create().texOffs(35,0).addBox(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), PartPose.offsetAndRotation(1.4F, -0.9F, -8.0F,0.0F, 0.0F, -0.5235987755982988F));
        cannonback.addOrReplaceChild("cannon3", CubeListBuilder.create().texOffs(35,0).addBox(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), PartPose.offsetAndRotation(1.4F, 0.7F, -8.0F,0.0F, 0.0F, 0.5235987755982988F));
        cannonback.addOrReplaceChild("cannon4", CubeListBuilder.create().texOffs(35,0).addBox(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), PartPose.offset(0.0F, 1.5F, -8.0F));
        cannonback.addOrReplaceChild("cannon5", CubeListBuilder.create().texOffs(35,0).addBox(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), PartPose.offsetAndRotation(-1.4F, 0.7F, -8.0F,0.0F, 0.0F, -0.5235987755982988F));
        cannonback.addOrReplaceChild("cannon6", CubeListBuilder.create().texOffs(35,0).addBox(-0.5F, -0.5F, -5.0F, 1, 1, 13,barrelsize), PartPose.offsetAndRotation(-1.4F, -0.9F, -8.0F,0.0F, 0.0F, 0.5235987755982988F));
        cannonback.addOrReplaceChild("cannonfront", CubeListBuilder.create().texOffs(45, 16).addBox(-2.0F, -2.0F, 0.0F, 4, 4, 1,f03), PartPose.offset(0.0F, -0.1F, -10.0F));

        partDefinition.addOrReplaceChild("core1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.5F, -3.0F, 5, 5, 6), PartPose.offset(0.0F, 10.0F, -5.0F));
        partDefinition.addOrReplaceChild("core2", CubeListBuilder.create().texOffs(0, 12).addBox(-2.5F, -2.5F, -2.0F, 4, 5, 1), PartPose.offset(0.5F, 10.0F, -12.0F));
        partDefinition.addOrReplaceChild("core3", CubeListBuilder.create().texOffs(0, 19).addBox(-1.5F, -1.5F, -1.0F, 3, 4, 2), PartPose.offset(0F, 10.2F, -12.0F));

        partDefinition.addOrReplaceChild("tube1", CubeListBuilder.create().texOffs(25, 35).addBox(0.0F, 0.0F, 0.0F, 1, 1, 1), PartPose.offset(-0.5F, 8.5F, -10.0F));
        partDefinition.addOrReplaceChild("tube2", CubeListBuilder.create().texOffs(25, 37).addBox(0.0F, 0.0F, 0.0F, 1, 1, 4), PartPose.offset(-0.5F, 7.5F, -13.0F));
        partDefinition.addOrReplaceChild("tube3", CubeListBuilder.create().texOffs(23, 45).addBox(0.0F, 0.0F, -2.0F, 1, 1, 6,f02), PartPose.offset(-5.1F, 10.4F, -3.0F));
        partDefinition.addOrReplaceChild("tube4", CubeListBuilder.create().texOffs(25, 35).addBox(0.0F, 0.0F, 0.0F, 1, 1, 1,f02), PartPose.offset(-3.7F, 10.4F, -5.0F));
        partDefinition.addOrReplaceChild("tube5", CubeListBuilder.create().texOffs(25, 35).addBox(0.0F, 0.0F, 0.0F, 1, 1, 1,f02), PartPose.offset(-3.7F, 10.4F, 0.0F));
        partDefinition.addOrReplaceChild("handle1", CubeListBuilder.create().texOffs(0, 50).addBox(-0.5F, -1.0F, -0.5F, 1, 1, 1), PartPose.offset(1.5F, 7.5F, -13.5F));
        partDefinition.addOrReplaceChild("handle2", CubeListBuilder.create().texOffs(0,50).addBox(-0.5F, -1.0F, -0.5F, 1, 1, 1), PartPose.offset(-1.5F, 7.5F, -13.5F));
        partDefinition.addOrReplaceChild("handlebar", CubeListBuilder.create().texOffs(0,50).addBox(-2.0F, 0.0F, 0.0F, 4, 1, 1), PartPose.offset(0.0F, 5.5F, -14.0F));
        partDefinition.addOrReplaceChild("handleback", CubeListBuilder.create().texOffs(14,12).addBox(0,0,0,2,2,4), PartPose.offset(-1,13,3));
        partDefinition.addOrReplaceChild("container", CubeListBuilder.create().texOffs(0, 35).addBox(-2.5F, -4.0F, -2.5F, 5, 8, 5), PartPose.offset(0.0F, 14.0F, 0.5F));


        return LayerDefinition.create(meshDefinition, 64, 64);
    }
    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
