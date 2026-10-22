package warhammermod.Items.melee.specials;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class Great_pick extends Item {
    public Great_pick(ToolMaterial material, Item.Properties properties, float damage, float attackspeed) {
        super(material.applyToolProperties(properties, net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE, 8.0F, -2.8F, 0.0F)
                .attributes(warhammermod.Items.melee.HammerTemplate.createAttributes(damage, attackspeed)));
    }
}
