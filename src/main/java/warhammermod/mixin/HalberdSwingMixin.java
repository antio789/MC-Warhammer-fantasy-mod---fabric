package warhammermod.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import warhammermod.Items.melee.HalberdTemplate;
import warhammermod.utils.Registry.WHRegistry;

/**
 * Clears the HALBERD_THRUST flag (set by HalberdTemplate.releaseUsing) once
 * no swing is running anymore, so later normal attacks with the halberd
 * whack again instead of stabbing. Runs on both sides; the server-side
 * removal is synced to other clients with the component.
 */
@Mixin(LivingEntity.class)
public class HalberdSwingMixin {

    @Inject(method = "updateSwingTime", at = @At("TAIL"))
    private void warhammermod$clearHalberdThrust(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.swinging) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = self.getItemInHand(hand);
                if (stack.getItem() instanceof HalberdTemplate && stack.has(WHRegistry.HALBERD_THRUST)) {
                    stack.remove(WHRegistry.HALBERD_THRUST);
                }
            }
        }
    }
}
