package warhammermod.Items.melee;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlocksAttacks;

import java.util.List;
import java.util.Optional;

public class ShieldTemplate extends Item {
    public ShieldTemplate(Item.Properties properties) {
        super(shieldProperties(properties));
    }

    public static Item.Properties shieldProperties(Item.Properties properties) {
        return properties
                .durability(336)
                .component(DataComponents.BLOCKS_ATTACKS, defaultBlocksAttacks())
                .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
                .equippableUnswappable(EquipmentSlot.OFFHAND);
    }

    private static BlocksAttacks defaultBlocksAttacks() {
        return new BlocksAttacks(
                0.25F,
                1.0F,
                List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                Optional.empty(),
                Optional.of(SoundEvents.SHIELD_BLOCK),
                Optional.of(SoundEvents.SHIELD_BREAK)
        );
    }
}
