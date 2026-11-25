package warhammermod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import warhammermod.Client.HalberdAnimations;
import warhammermod.Items.melee.HalberdTemplate;
import warhammermod.utils.Registry.WHRegistry;

/**
 * First-person custom thrust for the halberd's charged secondary attack.
 *
 * Vanilla routes the STAB swing case of ItemInHandRenderer.submitArmWithItem
 * to SpearAnimations.firstPersonAttack. Our HalberdThrustSwingMixin makes
 * ItemStack.getSwingAnimation() report STAB while the stack carries the
 * HALBERD_THRUST flag (set in HalberdTemplate.releaseUsing), so the release
 * swing lands on that call. Here we redirect it: flagged halberd -> our own
 * HalberdAnimations curve, everything else (real spears) -> vanilla.
 */
@Mixin(ItemInHandRenderer.class)
public class HalberdFirstPersonThrustMixin {

    @Redirect(
            method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/effects/SpearAnimations;firstPersonAttack(FLcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/entity/HumanoidArm;)V"
            )
    )
    private void warhammermod$halberdFirstPersonThrust(float attack, PoseStack poseStack, int invert, HumanoidArm arm, AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand, float swingAttack, ItemStack itemStack, float inverseArmHeight, PoseStack handPoseStack, SubmitNodeCollector submitNodeCollector, int lightCoords) {
        if (itemStack.getItem() instanceof HalberdTemplate
                && itemStack.getOrDefault(WHRegistry.HALBERD_THRUST, false)) {
            HalberdAnimations.firstPersonThrust(attack, poseStack, invert, arm);
        } else {
            SpearAnimations.firstPersonAttack(attack, poseStack, invert, arm);
        }
    }
}
