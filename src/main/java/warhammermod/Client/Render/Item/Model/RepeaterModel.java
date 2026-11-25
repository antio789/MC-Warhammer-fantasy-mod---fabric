package warhammermod.Client.Render.Item.Model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Unit;

/**
 * Ported from the 1.21.3 Tabula model (warhammermod.Client.Render.Item.Model.RepeaterModel).
 * Texture sheet is 64x32 (textures/item/repeater.png).
 */
@Environment(EnvType.CLIENT)
public class RepeaterModel extends Model<Unit> {
    public final ModelPart barrelHolder;
    /**
     * Barrel spin angle (radians), set by RepeaterSpecialRenderer each frame from the
     * ammo component. Applied in setupAnim because the render pipeline calls
     * setupAnim (which resets poses) right before renderToBuffer.
     */
    public float barrelZRot;

    public RepeaterModel(ModelPart root) {
        super(root, RenderTypes::entitySolid);
        this.barrelHolder = root.getChild("barrelholder1");
    }

    @Override
    public void setupAnim(Unit state) {
        super.setupAnim(state);
        this.barrelHolder.zRot = this.barrelZRot;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition barrelholder1 = root.addOrReplaceChild("barrelholder1",
                CubeListBuilder.create().texOffs(35, 25).addBox(-2F, -2F, 0F, 4F, 4F, 1F),
                PartPose.offset(0.5F, -2F, 8F));

        root.addOrReplaceChild("stock1", CubeListBuilder.create().texOffs(0, 25).addBox(0F, 0F, 0F, 1F, 2F, 2F), PartPose.offset(0F, 0F, -2F));
        root.addOrReplaceChild("handle1", CubeListBuilder.create().texOffs(0, 22).addBox(0F, 0F, 0F, 1F, 1F, 8F), PartPose.offset(0F, 0F, 0F));
        root.addOrReplaceChild("handle2", CubeListBuilder.create().texOffs(0, 25).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(0F, -1F, 1F));
        root.addOrReplaceChild("stockbase", CubeListBuilder.create().texOffs(0, 25).addBox(-1F, -2F, 0F, 3F, 3F, 1F), PartPose.offset(0F, -1F, 2F));
        root.addOrReplaceChild("flint", CubeListBuilder.create().texOffs(57, 20).addBox(0F, 0F, 0F, 1F, 1F, 1F), PartPose.offset(0F, -4F, 2F));
        root.addOrReplaceChild("stock", CubeListBuilder.create().texOffs(45, 0).addBox(-1.5F, 0F, 0F, 4F, 4F, 2F), PartPose.offset(0F, -4F, 3F));

        barrelholder1.addOrReplaceChild("barrelholder2", CubeListBuilder.create().texOffs(35, 25).addBox(-1.5F, 0F, 0F, 4F, 4F, 1F), PartPose.offset(-0.5F, -2F, 4F));
        barrelholder1.addOrReplaceChild("barrel1", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 1F, 9F), PartPose.offset(-0.5F, -1.9F, -3F));
        barrelholder1.addOrReplaceChild("barrel2", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 9F), PartPose.offsetAndRotation(-1.2F, -0.7F, -3F, 0F, 0F, 0.5235988F));
        barrelholder1.addOrReplaceChild("barrel3", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 9F), PartPose.offsetAndRotation(-1.2F, 0.7F, -3F, 0F, 0F, -0.5235988F));
        barrelholder1.addOrReplaceChild("barrel4", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 1F, 1F, 9F), PartPose.offset(-0.5F, 0.9F, -3F));
        barrelholder1.addOrReplaceChild("barrel5", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 9F), PartPose.offsetAndRotation(1.2F, 0.7F, -3F, 0F, 0F, 0.5235988F));
        barrelholder1.addOrReplaceChild("barrel6", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 9F), PartPose.offsetAndRotation(1.2F, -0.7F, -3F, 0F, 0F, -0.5235988F));

        return LayerDefinition.create(mesh, 64, 32);
    }
}
