package warhammermod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import warhammermod.Client.HalberdThrustAnimation;
import warhammermod.Items.melee.HalberdTemplate;

/**
 * Third-person charged-thrust animation: feed our thrust cycle into the
 * render state's attackTime. Everything vanilla then plays on its own,
 * because the halberd uses the STAB swing type:
 *
 * - HumanoidModel.setupAttackAnimation (attackTime > 0, STAB) ->
 *   SpearAnimations.thirdPersonAttackHand: the arm punch
 * - ItemInHandLayer.submitArmWithItem (attackTime > 0, attackArm, STAB) ->
 *   SpearAnimations.thirdPersonAttackItem: the grip-anchored rotation and
 *   forward push, applied in the correct hand-item frame
 * - ArmPose mapping for STAB aims the arm via the head part
 */
@Mixin(ArmedEntityRenderState.class)
public class HalberdThrustStateMixin {

    @Inject(method = "extractArmedEntityRenderState", at = @At("TAIL"))
    private static void warhammermod$halberdThrustState(LivingEntity entity, ArmedEntityRenderState state,
                                                        ItemModelResolver resolver, float partialTicks, CallbackInfo ci) {
        if (HalberdThrustAnimation.isThrusting()) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null && entity == localPlayer
                    && state.getMainHandItemStack().getItem() instanceof HalberdTemplate) {
                state.attackTime = HalberdThrustAnimation.attackProgress();
                state.attackArm = state.mainArm;
            }
        }
    }
}
