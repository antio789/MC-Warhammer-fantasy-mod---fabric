package warhammermod.Enchantements;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import warhammermod.utils.reference;

public class ModEnchantements {
    public static final ResourceKey<Enchantment> RANGE = of("range");
    public static final ResourceKey<Enchantment> RADIUS = of("radius");

    private static ResourceKey<Enchantment> of(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(reference.modid, name));
    }

    public static void initialize() {
    }
}
