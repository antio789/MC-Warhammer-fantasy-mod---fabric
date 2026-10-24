package warhammermod.Client.Render.Item.Model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.item.Item;
import warhammermod.utils.Registry.ItemsInit;
@Environment(EnvType.CLIENT)
public class EmpireShieldmodel extends abstractModshieldModel {

    public EmpireShieldmodel(ModelPart modelPart){
        super(modelPart);
    }

    public static TexturedModelData createLayer() {
        ModelData meshDefinition = new ModelData();
        ModelPartData partDefinition = meshDefinition.getRoot();
        partDefinition.addChild("plate2",ModelPartBuilder.create().uv(0, 0).cuboid(-6F,-11F,-2.0F,12,14,1), ModelTransform.NONE);
        partDefinition.addChild("plate3", ModelPartBuilder.create().uv(0, 20).cuboid(-5F,3F,-2.0F,10,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate4", ModelPartBuilder.create().uv(0, 22).cuboid(-4F,4F,-2.0F,8,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate5", ModelPartBuilder.create().uv(0, 24).cuboid(-3F,5F,-2.0F,6,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate6", ModelPartBuilder.create().uv(0, 26).cuboid(-2F,6F,-2.0F,4,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate7", ModelPartBuilder.create().uv(0, 28).cuboid(-1F,7F,-2.0F,2,1,1), ModelTransform.NONE);
        partDefinition.addChild("plate8", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F,8F,-2.0F,1,1,1), ModelTransform.NONE);
        partDefinition.addChild("handle", ModelPartBuilder.create().uv(26, 0).cuboid(-1.0F, -4.0F, -1.0F, 2, 6, 6), ModelTransform.NONE);;

        return TexturedModelData.of(meshDefinition, 64, 64);
    }

    @Override
    public boolean isItem(Item item) {
        return item.equals(ItemsInit.Imperial_shield);
    }



}
