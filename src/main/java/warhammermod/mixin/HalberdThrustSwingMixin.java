package warhammermod.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.SwingAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import warhammermod.Items.melee.HalberdTemplate;
import warhammermod.utils.Registry.WHRegistry;

/**
 * While a halberd stack carries the HALBERD_THRUST flag (set by
 * HalberdTemplate.releaseUsing for the charged secondary attack),
 * report its swing animation as STAB - exactly what the vanilla spear
 * declares in Item.Properties.spear(...). ItemStack.getSwingAnimation()
 * is the single read point for the swing animation: the swing duration
 * (LivingEntity.getCurrentSwingDuration), the first-person switch
 * (ItemInHandRenderer.submitArmWithItem) and the third-person render
 * state (ArmedEntityRenderState) all call it, so the vanilla
 * SpearAnimations stab plays in every view with no render-path mixins.
 * Normal attacks have no flag and stay WHACK.
 */
@Mixin(ItemStack.class)
public class HalberdThrustSwingMixin {

    @Inject(method = "getSwingAnimation", at = @At("HEAD"), cancellable = true)
    private void warhammermod$halberdThrustSwing(CallbackInfoReturnable<SwingAnimation> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.getItem() instanceof HalberdTemplate && self.getOrDefault(WHRegistry.HALBERD_THRUST, false)) {
            cir.setReturnValue(new SwingAnimation(SwingAnimationType.STAB, HalberdTemplate.THRUST_SWING_TICKS));
        }
    }
}
