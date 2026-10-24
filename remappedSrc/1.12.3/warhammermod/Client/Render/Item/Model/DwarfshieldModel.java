package warhammermod.Client.Render.Item.Model;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.item.Item;
import warhammermod.utils.Registry.ItemsInit;
@Environment(EnvType.CLIENT)
public class DwarfshieldModel extends abstractModshieldModel {


    public DwarfshieldModel(ModelPart modelPart){
        super(modelPart);
    }

    public static TexturedModelData createLayer() {
        ModelData meshDefinition = new ModelData();
        ModelPartData partDefinition = meshDefinition.getRoot();
        partDefinition.addChild("plate1", ModelPartBuilder.create().uv(0,0).cuboid(-3F,-11F,-2.0F,6,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate2",ModelPartBuilder.create().uv(0, 2).cuboid(-5F,-10F,-2.0F,10,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate3", ModelPartBuilder.create().uv(0, 4).cuboid(-6F,-9F,-2.0F,12,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate4", ModelPartBuilder.create().uv(0, 6).cuboid(-7F,-8F,-2.0F,14,2,1), ModelTransform.NONE);
        partDefinition.addChild("plate5", ModelPartBuilder.create().uv(0, 9).cuboid(-8F,-6F,-2.0F,16,6,1), ModelTransform.NONE);
        partDefinition.addChild("plate6", ModelPartBuilder.create().uv(0, 16).cuboid(-7F,0F,-2.0F,14,2,1), ModelTransform.NONE);
        partDefinition.addChild("plate7", ModelPartBuilder.create().uv(0, 19).cuboid(-6F,2F,-2.0F,12,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate8", ModelPartBuilder.create().uv(0, 21).cuboid(-5F,3F,-2.0F,10,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate9", ModelPartBuilder.create().uv(0, 23).cuboid(-3F,4F,-2.0F,6,1,1), ModelTransform.NONE);
        partDefinition.addChild("handle", ModelPartBuilder.create().uv(48, 0).cuboid(-1F, -4F, -1F, 2, 6, 6), ModelTransform.NONE);;

        return TexturedModelData.of(meshDefinition, 64, 64);
    }
    @Override
    public boolean isItem(Item item) {
        return item.equals(ItemsInit.Dwarf_shield);
    }

}
