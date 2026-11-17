package warhammermod.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import warhammermod.utils.JezzailScopeHelper;

/**
 * Makes the Warplock Jezzail aim like a vanilla spyglass: while using a
 * ready-to-fire (or creative) jezzail, isScoping() returns true, which
 * drives the spyglass scope overlay and the hidden held item. The actual
 * FOV factor is overridden by the client-side JezzailFovMixin.
 */
@Mixin(Player.class)
public class JezzailScopeMixin {

    @Inject(method = "isScoping", at = @At("HEAD"), cancellable = true)
    private void warhammermod$jezzailScope(CallbackInfoReturnable<Boolean> cir) {
        if (JezzailScopeHelper.isJezzailScoping((Player) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
