package warhammermod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import warhammermod.Client.HalberdThrustAnimation;
import warhammermod.Items.melee.HalberdTemplate;

/**
 * First-person charged-thrust animation: instead of writing our own motion,
 * substitute the attack value of the vanilla SpearAnimations.firstPersonAttack
 * call (which already runs for the halberd's STAB swing type, just with
 * attack=0 since no real swing happened) with our thrust cycle
 * (1.0 -> 0.0). Vanilla's keyframes then produce the full stab: the -70
 * degree tilt into thrust orientation AND the 0.65 forward push.
 */
@Mixin(ItemInHandRenderer.class)
public class HalberdThrustFirstPersonMixin {

    @ModifyArgs(
            method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/effects/SpearAnimations;firstPersonAttack(FLcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/entity/HumanoidArm;)V"
            )
    )
    private void warhammermod$halberdThrustAttack(Args args, AbstractClientPlayer player, float frameInterp,
                                                  float xRot, InteractionHand hand, float attack, ItemStack itemStack,
                                                  float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector collector,
                                                  int lightCoords) {
        // only substitute while our thrust cycle is running and no real swing
        // is animating; vanilla's own swing path stays untouched otherwise
        if (hand == InteractionHand.MAIN_HAND
                && attack < 1.0E-4F
                && itemStack.getItem() instanceof HalberdTemplate
                && HalberdThrustAnimation.isThrusting()) {
            args.set(0, HalberdThrustAnimation.attackProgress());
        }
    }
}
