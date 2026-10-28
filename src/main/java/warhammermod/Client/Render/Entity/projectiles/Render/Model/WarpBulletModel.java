package warhammermod.Client.Render.Entity.projectiles.Render.Model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Warp bullet cube model (UV offset 0,8 like the 1.21.3 WarpBulletModel).
 */
public class WarpBulletModel extends Model<ProjectileModel.State> {


    public WarpBulletModel(ModelPart root) {
        super(root, RenderTypes::entitySolid);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        meshDefinition.getRoot().addOrReplaceChild("bullet",
                CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2), PartPose.ZERO);
        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    @Override
    public void setupAnim(ProjectileModel.State state) {
    }
}
