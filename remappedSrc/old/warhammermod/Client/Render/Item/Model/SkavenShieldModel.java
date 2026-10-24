package warhammermod.Client.Render.Item.Model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.item.Item;
import warhammermod.utils.Registry.ItemsInit;
@Environment(EnvType.CLIENT)
public class SkavenShieldModel extends abstractModshieldModel {
    public ModelPart plate1;
    public ModelPart plate2;
    public ModelPart plate3;
    public ModelPart plate4;
    public ModelPart plate5;
    public ModelPart plate6;
    public ModelPart plate7;
    public ModelPart plate8;
    public ModelPart plate9;
    //public ModelPart plate10;
    public ModelPart handle;

    public SkavenShieldModel(ModelPart modelPart){
        super(modelPart);

        /*
        this.plate10= new ModelPart(this,0,24);
        this.plate10.func_22830_(-8.5F,-10F,1.5F,17,1,1,0.0F);
        this.plate10.rotateAngleY=3.141592654F;
         */
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("plate1", CubeListBuilder.create().texOffs(0,0).addBox(-0.5F,5F,-2.0F,1,1,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate2",CubeListBuilder.create().texOffs(0, 2).addBox(-1.5F,4,-2,3,1,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate3", CubeListBuilder.create().texOffs(0, 4).addBox(-2.5F,2F,-2F,5,2,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate4", CubeListBuilder.create().texOffs(0, 7).addBox(-3.5F,0F,-2F,7,2,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate5", CubeListBuilder.create().texOffs(0, 10).addBox(-4.5F,-2F,-2,9,2,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate6", CubeListBuilder.create().texOffs(0, 13).addBox(-5.5F,-4F,-2,11,2,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate7", CubeListBuilder.create().texOffs(0, 16).addBox(-6.5F,-6F,-2,13,2,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate8", CubeListBuilder.create().texOffs(0, 19).addBox(-7.5F,-8F,-2,15,2,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate9", CubeListBuilder.create().texOffs(0, 22).addBox(-8.5F,-9F,-2,17,1,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(48, 0).addBox(-1F, -3F, -1F, 2, 6, 6), PartPose.ZERO);;

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
    @Override
    public boolean isItem(Item item) {
        return item.equals(ItemsInit.Skaven_shield);
    }
}
