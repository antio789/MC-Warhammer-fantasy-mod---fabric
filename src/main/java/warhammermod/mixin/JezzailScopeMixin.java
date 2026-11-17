package warhammermod.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import warhammermod.Items.GunBase;
import warhammermod.Items.ItemsInit;

/**
 * Makes the Warplock Jezzail aim like a vanilla spyglass: while using a
 * ready-to-fire (or creative) jezzail, isScoping() returns true, which
 * drives the spyglass scope overlay and the hidden held item. The actual
 * FOV factor is overridden by the client-side JezzailFovMixin.
 */
@Mixin(Player.class)
public class JezzailScopeMixin {

    /** True while the player is aiming a ready-to-fire (or creative) warplock jezzail. */
    public static boolean isJezzailScoping(Player player) {
        if (!player.isUsingItem()) {
            return false;
        }
        ItemStack stack = player.getUseItem();
        return stack.getItem() == ItemsInit.Warplock_jezzail
                && (((GunBase) stack.getItem()).isReadytoFire(stack) || player.isCreative());
    }

    @Inject(method = "isScoping", at = @At("HEAD"), cancellable = true)
    private void warhammermod$jezzailScope(CallbackInfoReturnable<Boolean> cir) {
        if (isJezzailScoping((Player) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
