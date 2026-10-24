package warhammermod.Client.Render.Item.Model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.item.Item;
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

    public static TexturedModelData createLayer() {
        ModelData meshDefinition = new ModelData();
        ModelPartData partDefinition = meshDefinition.getRoot();
        partDefinition.addChild("plate1", ModelPartBuilder.create().uv(0,0).cuboid(-0.5F,5F,-2.0F,1,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate2",ModelPartBuilder.create().uv(0, 2).cuboid(-1.5F,4,-2,3,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate3", ModelPartBuilder.create().uv(0, 4).cuboid(-2.5F,2F,-2F,5,2,1), ModelTransform.NONE);
        partDefinition.addChild("plate4", ModelPartBuilder.create().uv(0, 7).cuboid(-3.5F,0F,-2F,7,2,1), ModelTransform.NONE);
        partDefinition.addChild("plate5", ModelPartBuilder.create().uv(0, 10).cuboid(-4.5F,-2F,-2,9,2,1), ModelTransform.NONE);
        partDefinition.addChild("plate6", ModelPartBuilder.create().uv(0, 13).cuboid(-5.5F,-4F,-2,11,2,1), ModelTransform.NONE);
        partDefinition.addChild("plate7", ModelPartBuilder.create().uv(0, 16).cuboid(-6.5F,-6F,-2,13,2,1), ModelTransform.NONE);
        partDefinition.addChild("plate8", ModelPartBuilder.create().uv(0, 19).cuboid(-7.5F,-8F,-2,15,2,1), ModelTransform.NONE);
        partDefinition.addChild("plate9", ModelPartBuilder.create().uv(0, 22).cuboid(-8.5F,-9F,-2,17,1,1), ModelTransform.NONE);
        partDefinition.addChild("handle", ModelPartBuilder.create().uv(48, 0).cuboid(-1F, -3F, -1F, 2, 6, 6), ModelTransform.NONE);;

        return TexturedModelData.of(meshDefinition, 64, 64);
    }
    @Override
    public boolean isItem(Item item) {
        return item.equals(ItemsInit.Skaven_shield);
    }
}
