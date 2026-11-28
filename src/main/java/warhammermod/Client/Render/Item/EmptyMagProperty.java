package warhammermod.Client.Render.Item;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import warhammermod.Items.Ammocomponent;
import warhammermod.utils.Registry.WHRegistry;

/**
 * Custom item model condition (replaces the 1.21.3 "reloading" model predicate):
 * true while the holding player is not in creative and the gun's ammo component
 * is empty. Registered as warhammermod:empty_mag in Clientside.
 */
@Environment(EnvType.CLIENT)
public record EmptyMagProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<EmptyMagProperty> MAP_CODEC = MapCodec.unit(new EmptyMagProperty());

    @Override
    public MapCodec<EmptyMagProperty> type() {
        return MAP_CODEC;
    }

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        return owner instanceof Player player
                && !player.isCreative()
                && stack.getOrDefault(WHRegistry.AMMO, Ammocomponent.DEFAULT).ammocount() <= 0;
    }
}
