package warhammermod.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import warhammermod.Items.GunBase;
import warhammermod.Items.ItemsInit;

/** Shared check used by the jezzail scope mixins (not a mixin itself). */
public final class JezzailScopeHelper {

    private JezzailScopeHelper() {
    }

    /** True while the player is aiming a ready-to-fire (or creative) warplock jezzail. */
    public static boolean isJezzailScoping(Player player) {
        if (!player.isUsingItem()) {
            return false;
        }
        ItemStack stack = player.getUseItem();
        return stack.getItem() == ItemsInit.Warplock_jezzail
                && (((GunBase) stack.getItem()).isReadytoFire(stack) || player.isCreative());
    }
}
