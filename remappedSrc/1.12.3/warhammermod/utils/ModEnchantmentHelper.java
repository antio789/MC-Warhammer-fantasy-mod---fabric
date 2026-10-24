package warhammermod.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

import java.util.Optional;

public class ModEnchantmentHelper {
    private static RegistryEntryLookup enchRegistryLookup = null;

    public static RegistryEntry<Enchantment> getRegistryEntry(World world, RegistryKey<Enchantment> enchantment) {
        if(enchRegistryLookup == null) enchRegistryLookup = world.getRegistryManager().createRegistryLookup().getOrThrow(RegistryKeys.ENCHANTMENT);
        Optional<RegistryEntry.Reference<Enchantment>> enchantmentEntry = enchRegistryLookup.getOptional(enchantment);
        if(enchantmentEntry.isPresent()) return enchantmentEntry.get();
        else {
            enchRegistryLookup = world.getRegistryManager().createRegistryLookup().getOrThrow(RegistryKeys.ENCHANTMENT);
            return enchRegistryLookup.getOrThrow(enchantment);
        }
    }


    public static int getLevel(World world, ItemStack stack, RegistryKey<Enchantment> enchantment) {
        if(!stack.hasEnchantments()) return 0;
        return EnchantmentHelper.getLevel(getRegistryEntry(world, enchantment), stack);
    }

    public static int getEquipmentLevel(World world, LivingEntity user, RegistryKey<Enchantment> enchantment) {
        return EnchantmentHelper.getEquipmentLevel(getRegistryEntry(world, enchantment), user);
    }
}
