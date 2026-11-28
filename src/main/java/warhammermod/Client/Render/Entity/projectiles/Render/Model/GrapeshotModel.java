package warhammermod.Client.Render.Entity.projectiles.Render.Model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Grapeshot model: five small pellets that spread out with the projectile's age,
 * ported from the 1.21.3 GrapeshotModel.
 */
public class GrapeshotModel extends Model<ProjectileModel.State> {


    public final ModelPart bullet;
    public final ModelPart bullet2;
    public final ModelPart bullet3;
    public final ModelPart bullet4;
    public final ModelPart bullet5;

    public GrapeshotModel(ModelPart root) {
        super(root, RenderTypes::entitySolid);
        this.bullet = root.getChild("bullet");
        this.bullet2 = root.getChild("bullet_2");
        this.bullet3 = root.getChild("bullet3");
        this.bullet4 = root.getChild("bullet4");
        this.bullet5 = root.getChild("bullet5");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        CubeDeformation f01 = new CubeDeformation(0.1F);
        meshDefinition.getRoot().addOrReplaceChild("bullet", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1, 1, 1, f01), PartPose.ZERO);
        meshDefinition.getRoot().addOrReplaceChild("bullet1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 1, 1, 1, f01), PartPose.ZERO);
        meshDefinition.getRoot().addOrReplaceChild("bullet_2", CubeListBuilder.create().texOffs(0, 0).addBox(0.5F, 1F, -2F, 1, 1, 1, f01), PartPose.ZERO);
        meshDefinition.getRoot().addOrReplaceChild("bullet3", CubeListBuilder.create().texOffs(0, 0).addBox(1.5F, -1F, 1F, 1, 1, 1, f01), PartPose.ZERO);
        meshDefinition.getRoot().addOrReplaceChild("bullet4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 1F, -1.5F, 1, 1, 1, f01), PartPose.ZERO);
        meshDefinition.getRoot().addOrReplaceChild("bullet5", CubeListBuilder.create().texOffs(0, 0).addBox(2F, 0.5F, 0F, 1, 1, 1, f01), PartPose.ZERO);
        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    @Override
    public void setupAnim(ProjectileModel.State state) {
        // spread pellets as the shot ages (like the 1.21.3 setRotationAngles)
        this.bullet.x = state.age * 3F + 2;
        this.bullet2.y = -state.age * 3F - 1;
        this.bullet3.z = -state.age * 3F - 2;
        this.bullet4.x = state.age * 3F + 1.5F;
        this.bullet5.z = -state.age * 3F - 1.5F;
    }
}
