package warhammermod.Client.Render.Entity.Renders.Models;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/**
 * Dwarf model, ported from the 1.21.3 reference DwarfModel (Yarn) to the
 * 26.2 render-state based model system. Structure/UVs kept identical.
 */
public class DwarfModel<S extends HumanoidRenderState> extends HumanoidModel<S> {
    public final ModelPart hat;
    public final ModelPart hatRim;
    public final ModelPart villagerArms;

    public DwarfModel(ModelPart root) {
        super(root);
        this.hat = this.head.getChild("hat");
        this.hatRim = this.hat.getChild("hat_rim");
        this.villagerArms = root.getChild("arms");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partDefinition = meshDefinition.getRoot();
        CubeDeformation f05 = new CubeDeformation(0.5F);
        CubeDeformation f09 = new CubeDeformation(0.009F);

        PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -6.0F, -4.0F, 8, 10, 8), PartPose.ZERO);
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(65, 0).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 10.0F, 8.0F, f05), PartPose.ZERO);
        hat.addOrReplaceChild("hat_rim", CubeListBuilder.create().texOffs(65, 25).addBox(-8.0F, -8.0F, -3.0F, 16.0F, 16.0F, 1.0F), PartPose.offsetAndRotation(0, 0, 0, -(float) Math.PI / 2F, 0, 0));

        head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 2.0F, -6.0F, 2, 4, 2), PartPose.offset(0.0F, -2, 0));
        head.addOrReplaceChild("beard", CubeListBuilder.create().texOffs(36, 38).addBox(-4.0F, 6.0F, -5.0F, 8, 6, 1), PartPose.offset(0.0F, -2, 0));
        head.addOrReplaceChild("mustache1", CubeListBuilder.create().texOffs(36, 38)
                .addBox(-4.0F, 3.0F, -5.0F, 2, 3, 1)
                .addBox(2.0F, 3.0F, -5.0F, 2, 3, 1)
                .addBox(-2.0F, 3.0F, -5.0F, 1, 1, 1)
                .addBox(1.0F, 3.0F, -5.0F, 1, 1, 1), PartPose.offset(0, -2, 0));
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 19).addBox(-5.0F, 4.0F, -3.0F, 10, 10, 7), PartPose.ZERO);
        partDefinition.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 38).addBox(-5.0F, 4.0F, -3.0F, 10, 16, 7, f05), PartPose.ZERO);
        partDefinition.addOrReplaceChild("arms", CubeListBuilder.create()
                        .texOffs(40, 16).addBox(-8.0F, 1.0F, 1.0F, 4, 8, 4, f09)
                        .texOffs(40, 16).addBox(4.0F, 1.0F, 1.0F, 4, 8, 4, f09)
                        .texOffs(36, 29).addBox(-4.0F, 5.0F, 1.0F, 8, 4, 4, f09),
                PartPose.offset(0, 4, 0));
        partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(34, 0).addBox(-3F, 2.0F, -2.0F, 5, 10, 5), PartPose.offset(-2.0F, 12.0F, 0.0F));
        partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(34, 0).addBox(-2F, 2.0F, -2.0F, 5, 10, 5).mirror(), PartPose.offset(2.0F, 12.0F, 0.0F));
        partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(35, 46).addBox(-3F, -2F, 2F, 4, 10, 4).mirror(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(35, 46).addBox(-1.0F, 2.0F, -2F, 4, 10, 4).mirror(), PartPose.offset(5.0F, 2.0F, 0.0F));
        return LayerDefinition.create(meshDefinition, 128, 128);
    }

    @Override
    public void setupAnim(S state) {
        super.setupAnim(state);
        // villager-style crossed arms
        this.villagerArms.xRot = -0.75F;
        this.villagerArms.z = -0.5F;
        this.villagerArms.x = 0.0F;
        this.villagerArms.y = 4.5F;
    }
}
