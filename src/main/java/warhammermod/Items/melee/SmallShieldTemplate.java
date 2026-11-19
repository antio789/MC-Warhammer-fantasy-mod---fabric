package warhammermod.Items.melee;

import net.minecraft.world.item.Item;

public class SmallShieldTemplate extends Item {
    public SmallShieldTemplate(Item.Properties properties) {
        super(ShieldTemplate.shieldProperties(properties));
    }
}
