package warhammermod.Items.melee.specials;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;


public class Great_pick extends SwordItem {//enchantement pick + sword

    public Great_pick(Properties properties){
        super(Tiers.DIAMOND,properties.attributes(createAttributes(Tiers.DIAMOND,2,-2.6F)).component(DataComponents.TOOL,Tiers.DIAMOND.createToolProperties(BlockTags.MINEABLE_WITH_PICKAXE)));
    }




}
