package warhammermod.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import warhammermod.Items.melee.HalberdTemplate;

/**
 * The halberd's charged release temporarily sets SWING_ANIMATION=STAB on the
 * stack so the vanilla spear thrust animation plays for that one swing
 * (normal attacks stay WHACK). The component is read live from the held
 * stack, so it must be removed again once no swing is running - otherwise
 * every later attack with the halberd would stab.
 */
@Mixin(LivingEntity.class)
public class HalberdSwingMixin {

    @Inject(method = "updateSwingTime", at = @At("TAIL"))
    private void warhammermod$clearHalberdStab(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.swinging) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = self.getItemInHand(hand);
                if (stack.getItem() instanceof HalberdTemplate && stack.getSwingAnimation().type() == SwingAnimationType.STAB) {
                    stack.remove(DataComponents.SWING_ANIMATION);
                }
            }
        }
    }
}
