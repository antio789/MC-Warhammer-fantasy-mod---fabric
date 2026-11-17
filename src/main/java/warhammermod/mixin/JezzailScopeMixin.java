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
 * drives the spyglass FOV zoom, the scope overlay and the hidden held
 * item (26.2 equivalent of the 1.21.3 FOV_Capture mixin).
 */
@Mixin(Player.class)
public class JezzailScopeMixin {

    @Inject(method = "isScoping", at = @At("HEAD"), cancellable = true)
    private void warhammermod$jezzailScope(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        if (self.isUsingItem()) {
            ItemStack stack = self.getUseItem();
            if (stack.getItem() == ItemsInit.Warplock_jezzail
                    && (((GunBase) stack.getItem()).isReadytoFire(stack) || self.isCreative())) {
                cir.setReturnValue(true);
            }
        }
    }
}
