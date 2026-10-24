package warhammermod.Items.melee.specials;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.tag.BlockTags;


public class Great_pick extends SwordItem {//enchantement pick + sword

    public Great_pick(net.minecraft.item.Item.Settings properties){
        super(ToolMaterials.DIAMOND,properties.attributeModifiers(createAttributeModifiers(ToolMaterials.DIAMOND,2,-2.6F)).component(DataComponentTypes.TOOL,ToolMaterials.DIAMOND.createComponent(BlockTags.PICKAXE_MINEABLE)));
    }




}
