package warhammermod.Items.melee;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;


public class HammerTemplate extends SwordItem {
    protected float attackDamage;
    protected float attackSpeed;

    public HammerTemplate(Tier tier, Properties properties) {
        super(tier, properties.attributes(HammerTemplate.createAttributeModifiers(tier, 3F + tier.getAttackDamageBonus()+(tier.getAttackDamageBonus()>3?-1:0),-2.8F)));
    }

    public HammerTemplate(Tier tier, Properties properties,float base_damage,float base_speed) {
        super(tier, properties.attributes(HammerTemplate.createAttributeModifiers(tier, 3F + base_damage+(tier.getAttackDamageBonus()>3?-1:0),base_speed)));
    }

    public static ItemAttributeModifiers createAttributeModifiers(Tier material, float baseAttackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                BASE_ATTACK_DAMAGE_ID, (double)((float)baseAttackDamage + material.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }


}
