package warhammermod.Items.melee;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class DaggerTemplate extends Item {
    public DaggerTemplate(ToolMaterial material, Item.Properties properties, float damage, float attackspeed) {
        super(material.applySwordProperties(properties, damage, attackspeed).attributes(HammerTemplate.createAttributes(damage, attackspeed)));
    }
}
