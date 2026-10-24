package warhammermod.Client.Render.Item.Model;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.item.Item;
import warhammermod.utils.Registry.ItemsInit;

@Environment(EnvType.CLIENT)
public class DarkElfshieldmodel extends abstractModshieldModel {


    public DarkElfshieldmodel(ModelPart modelPart)
    {
        super(modelPart);
    }

    @Override
    public boolean isItem(Item item) {
        return item.equals(ItemsInit.Dark_Elf_Shield);
    }


    public static TexturedModelData createLayer() {
        ModelData modeldata = new ModelData();
        ModelPartData partDefinition = modeldata.getRoot();
        partDefinition.addChild("plate",ModelPartBuilder.create().uv(10,0).cuboid(-2F, -11.0F, -2.0F, 7, 19, 1),ModelTransform.NONE);
        partDefinition.addChild("plate2",ModelPartBuilder.create().uv(0, 0).cuboid(-6F,-8F,-2.0F,4,16,1), ModelTransform.NONE);
        partDefinition.addChild("plate3", ModelPartBuilder.create().uv(0, 20).cuboid(-5F,8F,-2.0F,9,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate4", ModelPartBuilder.create().uv(0, 22).cuboid(-4F,9F,-2.0F,7,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate5", ModelPartBuilder.create().uv(0, 24).cuboid(-3F,10F,-2.0F,5,2,1), ModelTransform.NONE);
        partDefinition.addChild("plate6", ModelPartBuilder.create().uv(0, 27).cuboid(-2F,12F,-2.0F,3,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate7", ModelPartBuilder.create().uv(0, 29).cuboid(-3F,-11F,-2.0F,1,1,1).cuboid(-6F,-9F,-2.0F,1,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate8", ModelPartBuilder.create().uv(0, 18).cuboid(-3F,-11F,-2.0F,1,1,1), ModelTransform.NONE);
        partDefinition.addChild("handle", ModelPartBuilder.create().uv(48, 0).cuboid(-1F, -3F, -1F, 2, 6, 6), ModelTransform.NONE);;

        return TexturedModelData.of(modeldata, 64, 64);
    }






}
