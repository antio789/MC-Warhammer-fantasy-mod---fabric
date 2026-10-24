package warhammermod.utils;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class ModEnchantmentHelper {
    private static HolderGetter enchRegistryLookup = null;

    public static Holder<Enchantment> getRegistryEntry(Level world, ResourceKey<Enchantment> enchantment) {
        if(enchRegistryLookup == null) enchRegistryLookup = world.registryAccess().asGetterLookup().lookupOrThrow(Registries.ENCHANTMENT);
        Optional<Holder.Reference<Enchantment>> enchantmentEntry = enchRegistryLookup.get(enchantment);
        if(enchantmentEntry.isPresent()) return enchantmentEntry.get();
        else {
            enchRegistryLookup = world.registryAccess().asGetterLookup().lookupOrThrow(Registries.ENCHANTMENT);
            return enchRegistryLookup.getOrThrow(enchantment);
        }
    }


    public static int getLevel(Level world, ItemStack stack, ResourceKey<Enchantment> enchantment) {
        if(!stack.isEnchanted()) return 0;
        return EnchantmentHelper.getItemEnchantmentLevel(getRegistryEntry(world, enchantment), stack);
    }

    public static int getEquipmentLevel(Level world, LivingEntity user, ResourceKey<Enchantment> enchantment) {
        return EnchantmentHelper.getEnchantmentLevel(getRegistryEntry(world, enchantment), user);
    }
}
