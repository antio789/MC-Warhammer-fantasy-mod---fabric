package warhammermod.Items.melee;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;


public class HammerTemplate extends SwordItem {
    protected float attackDamage;
    protected float attackSpeed;

    public HammerTemplate(ToolMaterial tier, Settings settings) {
        super(tier,3F + tier.attackDamageBonus()+(tier.attackDamageBonus()>3?-1:0),-2.8f,settings);
    }

    public HammerTemplate(ToolMaterial tier,float ad,float as, Settings settings) {
        super(tier,ad + tier.attackDamageBonus()+(tier.attackDamageBonus()>3?-1:0),as,settings);
    }


}
