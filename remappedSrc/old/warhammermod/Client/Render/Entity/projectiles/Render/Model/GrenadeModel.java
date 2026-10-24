package warhammermod.Client.Render.Entity.projectiles.Render.Model;

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
import net.minecraft.world.entity.Entity;
@Environment(EnvType.CLIENT)
public class GrenadeModel<T extends Entity> extends Model {
    public ModelPart grenadepart1;
    public ModelPart grenadetop;
    public ModelPart root;

    public GrenadeModel(ModelPart modelPart) {
        super(RenderType::entitySolid);
        this.root = modelPart;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("grenadetop", CubeListBuilder.create().texOffs(3, 0).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3), PartPose.offset(0.0F, 0.0F, 0.0F));
        partDefinition.addOrReplaceChild("grenadepart", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 1.5F, -0.5F, 1, 1, 1), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(meshDefinition, 64, 32);
    }


    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
