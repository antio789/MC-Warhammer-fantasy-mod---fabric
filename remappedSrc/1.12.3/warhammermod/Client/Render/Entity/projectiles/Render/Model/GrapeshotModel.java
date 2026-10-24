package warhammermod.Client.Render.Entity.projectiles.Render.Model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class GrapeshotModel<T extends Entity> extends Model {
    public ModelPart bullet;
    public ModelPart bullet2;
    public ModelPart bullet3;
    public ModelPart bullet4;
    public ModelPart bullet5;
    public ModelPart root;

    public GrapeshotModel(ModelPart modelPart) {
        super(RenderLayer::getEntitySolid);
        this.root = modelPart;
        this.bullet =root.getChild("bullet");
        this.bullet2 =root.getChild("bullet_2");
        this.bullet3 =root.getChild("bullet3");
        this.bullet4 =root.getChild("bullet4");
        this.bullet5 =root.getChild("bullet5");
    }

    public static TexturedModelData createLayer() {
        ModelData meshDefinition = new ModelData();
        ModelPartData partDefinition = meshDefinition.getRoot();
        Dilation f01 = new Dilation(0.1F);
        partDefinition.addChild("bullet", ModelPartBuilder.create().uv(0,0).cuboid(0F, 0F, 0F, 1, 1, 1,f01), ModelTransform.NONE);
        partDefinition.addChild("bullet1", ModelPartBuilder.create().uv(0,0).cuboid(-1.0F, -1.0F, -1.0F, 1, 1, 1,f01), ModelTransform.NONE);
        partDefinition.addChild("bullet_2", ModelPartBuilder.create().uv(0,0).cuboid(0.5F, 1F, -2F, 1, 1, 1,f01), ModelTransform.NONE);
        partDefinition.addChild("bullet3", ModelPartBuilder.create().uv(0,0).cuboid(1.5F, -1F, 1F, 1, 1, 1,f01), ModelTransform.NONE);
        partDefinition.addChild("bullet4", ModelPartBuilder.create().uv(0,0).cuboid(-1.5F, 1F, -1.5F, 1, 1, 1,f01), ModelTransform.NONE);
        partDefinition.addChild("bullet5", ModelPartBuilder.create().uv(0,0).cuboid(2F, 0.5F, 0F, 1, 1, 1,f01), ModelTransform.NONE);
        return TexturedModelData.of(meshDefinition, 64, 32);
    }



    public void setRotationAngles(T p_225597_1_, float partialticks, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        bullet.pivotX=p_225597_1_.age*3F +2;
        bullet2.pivotY=-p_225597_1_.age*3F -1;
        bullet3.pivotZ=-p_225597_1_.age*3F -2;
        bullet4.pivotX=p_225597_1_.age*3F +1.5F;
        bullet5.pivotZ=-p_225597_1_.age*3F -1.5F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
