package warhammermod.Enchantements;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import warhammermod.utils.reference;

public class ModEnchantements {
    public static final RegistryKey<Enchantment> RANGE = of("range");
    public static final RegistryKey<Enchantment> RADIUS = of("radius");

    private static RegistryKey<Enchantment> of(String name) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(reference.modid, name));
    }

    public static void initialize() {
    }
}
