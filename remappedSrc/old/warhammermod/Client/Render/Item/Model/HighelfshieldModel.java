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
public class HighelfshieldModel extends abstractModshieldModel {

    public HighelfshieldModel(ModelPart modelPart)
    {
        super(modelPart);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("plate2",CubeListBuilder.create().texOffs(0, 0).addBox(-6F,-11F,-2.0F,11,19,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate3", CubeListBuilder.create().texOffs(0, 20).addBox(-5F,8F,-2.0F,9,1,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate4", CubeListBuilder.create().texOffs(0, 22).addBox(-4F,9F,-2.0F,7,1,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate5", CubeListBuilder.create().texOffs(0, 24).addBox(-3F,10F,-2.0F,5,2,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate6", CubeListBuilder.create().texOffs(0, 27).addBox(-2F,12F,-2.0F,3,1,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("plate7", CubeListBuilder.create().texOffs(0, 29).addBox(-1F,13F,-2.0F,1,1,1), PartPose.ZERO);
        partDefinition.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(426, 0).addBox(-1F, -3F, -1F, 2, 6, 6), PartPose.ZERO);;

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public boolean isItem(Item item) {
        return item.equals(ItemsInit.High_Elf_Shield);
    }
}
