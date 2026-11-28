package warhammermod.Items.melee;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class HammerTemplate extends Item {
    protected final float attackDamage;

    public HammerTemplate(ToolMaterial material, Item.Properties properties, float damage, float attackspeed) {
        super(material.applySwordProperties(properties, damage, attackspeed).attributes(createAttributes(damage, attackspeed)));
        this.attackDamage = damage;
    }

    public static ItemAttributeModifiers createAttributes(float damage, float speed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Identifier.withDefaultNamespace("base_attack_damage"), damage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Identifier.withDefaultNamespace("base_attack_speed"), speed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public float getDamage() {
        return this.attackDamage;
    }
}
