package warhammermod.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.Enchantment;

public class reference {
    public static final String modid = "warhammermod";

    public static Identifier location(String name) {
        return Identifier.fromNamespaceAndPath(modid, name);
    }

    public static Holder<Enchantment> enchantment(ServerLevel level, ResourceKey<Enchantment> key) {
        return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }
}
