package warhammermod.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import warhammermod.Client.HalberdThrustAnimation;
import warhammermod.Items.melee.HalberdTemplate;

/**
 * Third-person arm pose for the halberd charged thrust. Rather than
 * inventing arm math, reuse the vanilla spear-use stance
 * (SpearAnimations.thirdPersonHandUse), which aims the holding arm at the
 * HEAD MODEL PART's rotation - fully player-relative, clamped like the
 * vanilla spear charge. The halberd carries no KINETIC_WEAPON component,
 * so only the head-relative base assignment runs.
 */
@Mixin(HumanoidModel.class)
public class HalberdThrustArmMixin {

    @Inject(method = "setupAttackAnimation", at = @At("TAIL"))
    private void warhammermod$halberdThrustArm(HumanoidRenderState state, CallbackInfo ci) {
        if (!HalberdThrustAnimation.isThrusting()
                || !(state instanceof ArmedEntityRenderState armedState)) {
            return;
        }
        ItemStack mainHand = armedState.getMainHandItemStack();
        if (!(mainHand.getItem() instanceof HalberdTemplate)) {
            return;
        }
        HumanoidModel<HumanoidRenderState> self = (HumanoidModel<HumanoidRenderState>) (Object) this;
        boolean holdingInRightArm = armedState.mainArm == HumanoidArm.RIGHT;
        ModelPart arm = holdingInRightArm ? self.rightArm : self.leftArm;
        SpearAnimations.thirdPersonHandUse(arm, self.head, holdingInRightArm, mainHand, state);
    }
}
