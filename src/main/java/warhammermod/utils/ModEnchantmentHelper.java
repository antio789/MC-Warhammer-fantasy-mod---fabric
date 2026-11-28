package warhammermod.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class ModEnchantmentHelper {

    public static Holder<Enchantment> getRegistryEntry(Level world, ResourceKey<Enchantment> enchantment) {
        return world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment);
    }

    public static int getLevel(Level world, ItemStack stack, ResourceKey<Enchantment> enchantment) {
        if (!EnchantmentHelper.hasAnyEnchantments(stack)) return 0;
        return EnchantmentHelper.getItemEnchantmentLevel(getRegistryEntry(world, enchantment), stack);
    }

    public static int getEquipmentLevel(Level world, LivingEntity user, ResourceKey<Enchantment> enchantment) {
        int[] level = {0};
        EnchantmentHelper.runIterationOnEquipment(user, (holder, enchantmentLevel, enchantedItemInUse) -> {
            if (holder.is(enchantment) && enchantmentLevel > level[0]) {
                level[0] = enchantmentLevel;
            }
        });
        return level[0];
    }
}
