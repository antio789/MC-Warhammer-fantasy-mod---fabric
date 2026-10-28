package warhammermod.Client.Render.Entity.projectiles.Render.Model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Grenade cube model, ported from the 1.21.3 GrenadeModel.
 */
public class GrenadeModel extends Model<ProjectileModel.State> {


    public GrenadeModel(ModelPart root) {
        super(root, RenderTypes::entitySolid);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        meshDefinition.getRoot().addOrReplaceChild("grenadetop",
                CubeListBuilder.create().texOffs(3, 0).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3), PartPose.ZERO);
        meshDefinition.getRoot().addOrReplaceChild("grenadepart",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 1.5F, -0.5F, 1, 1, 1), PartPose.ZERO);
        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    @Override
    public void setupAnim(ProjectileModel.State state) {
    }
}
