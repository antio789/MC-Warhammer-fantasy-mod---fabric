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

    public HammerTemplate(ToolMaterial tier, net.minecraft.item.Item.Settings properties) {
        super(tier, properties.attributeModifiers(HammerTemplate.createAttributeModifiers(tier, 3F + tier.getAttackDamage()+(tier.getAttackDamage()>3?-1:0),-2.8F)));
    }

    public HammerTemplate(ToolMaterial tier, net.minecraft.item.Item.Settings properties,float base_damage,float base_speed) {
        super(tier, properties.attributeModifiers(HammerTemplate.createAttributeModifiers(tier, 3F + base_damage+(tier.getAttackDamage()>3?-1:0),base_speed)));
    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                BASE_ATTACK_DAMAGE_MODIFIER_ID, (double)((float)baseAttackDamage + material.getAttackDamage()), EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, (double)attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .build();
    }


}
