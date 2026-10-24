package warhammermod.Client.Render.Entity.projectiles.Render.Model;

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
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class GrapeshotModel<T extends Entity> extends Model {
    public ModelPart bullet;
    public ModelPart bullet2;
    public ModelPart bullet3;
    public ModelPart bullet4;
    public ModelPart bullet5;
    public ModelPart root;

    public GrapeshotModel(ModelPart modelPart) {
        super(RenderType::entitySolid);
        this.root = modelPart;
        this.bullet =root.getChild("bullet");
        this.bullet2 =root.getChild("bullet_2");
        this.bullet3 =root.getChild("bullet3");
        this.bullet4 =root.getChild("bullet4");
        this.bullet5 =root.getChild("bullet5");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        CubeDeformation f01 = new CubeDeformation(0.1F);
        partDefinition.addOrReplaceChild("bullet", CubeListBuilder.create().texOffs(0,0).addBox(0F, 0F, 0F, 1, 1, 1,f01), PartPose.ZERO);
        partDefinition.addOrReplaceChild("bullet1", CubeListBuilder.create().texOffs(0,0).addBox(-1.0F, -1.0F, -1.0F, 1, 1, 1,f01), PartPose.ZERO);
        partDefinition.addOrReplaceChild("bullet_2", CubeListBuilder.create().texOffs(0,0).addBox(0.5F, 1F, -2F, 1, 1, 1,f01), PartPose.ZERO);
        partDefinition.addOrReplaceChild("bullet3", CubeListBuilder.create().texOffs(0,0).addBox(1.5F, -1F, 1F, 1, 1, 1,f01), PartPose.ZERO);
        partDefinition.addOrReplaceChild("bullet4", CubeListBuilder.create().texOffs(0,0).addBox(-1.5F, 1F, -1.5F, 1, 1, 1,f01), PartPose.ZERO);
        partDefinition.addOrReplaceChild("bullet5", CubeListBuilder.create().texOffs(0,0).addBox(2F, 0.5F, 0F, 1, 1, 1,f01), PartPose.ZERO);
        return LayerDefinition.create(meshDefinition, 64, 32);
    }



    public void setRotationAngles(T p_225597_1_, float partialticks, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        bullet.x=p_225597_1_.tickCount*3F +2;
        bullet2.y=-p_225597_1_.tickCount*3F -1;
        bullet3.z=-p_225597_1_.tickCount*3F -2;
        bullet4.x=p_225597_1_.tickCount*3F +1.5F;
        bullet5.z=-p_225597_1_.tickCount*3F -1.5F;
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
