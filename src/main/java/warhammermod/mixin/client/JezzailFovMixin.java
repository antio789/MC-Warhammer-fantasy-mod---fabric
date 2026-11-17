package warhammermod.mixin.client;

import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import warhammermod.mixin.JezzailScopeMixin;

/**
 * 26.2 equivalent of the 1.21.3 FOV_Capture mixin: the vanilla spyglass
 * zoom is a fixed 10x (fov modifier 0.1). The jezzail uses a rifle-scope
 * 2.5x instead (modifier 0.4), while keeping the spyglass overlay from
 * JezzailScopeMixin.
 */
@Mixin(AbstractClientPlayer.class)
public class JezzailFovMixin {

    /** FOV multiplier while aiming the jezzail: 0.4 = 2.5x zoom. */
    private static final float JEZZAIL_ZOOM_FOV_MODIFIER = 0.4F;

    @Inject(method = "getFieldOfViewModifier", at = @At("HEAD"), cancellable = true)
    private void warhammermod$jezzailFov(CallbackInfoReturnable<Float> cir) {
        if (JezzailScopeMixin.isJezzailScoping((AbstractClientPlayer) (Object) this)) {
            cir.setReturnValue(JEZZAIL_ZOOM_FOV_MODIFIER);
        }
    }
}
