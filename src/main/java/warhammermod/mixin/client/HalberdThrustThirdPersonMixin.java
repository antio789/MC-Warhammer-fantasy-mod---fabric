package warhammermod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import warhammermod.Client.HalberdThrustAnimation;
import warhammermod.Items.melee.HalberdTemplate;

/**
 * Forward-thrust animation for the halberd charged attack, third person.
 * Unlike the vanilla spear attack (whose swing re-orients the ARM to the
 * look direction first), our thrust has no arm pose change, so the hand
 * frame still follows the torso. Realign the push axis with the player's
 * actual look direction (head yaw delta + pitch from the render state)
 * before applying the forward translate, then restore the frame so only
 * the offset - not the weapon orientation - changes.
 */
@Mixin(ItemInHandLayer.class)
public class HalberdThrustThirdPersonMixin {

    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void warhammermod$halberdThrust(ArmedEntityRenderState state, ItemStackRenderState itemRenderState,
                                            ItemStack stack, HumanoidArm arm, PoseStack poseStack,
                                            SubmitNodeCollector collector, int light, CallbackInfo ci) {
        float push = HalberdThrustAnimation.pushAmount();
        if (push == 0.0F || !(stack.getItem() instanceof HalberdTemplate)) {
            return;
        }
        LivingEntityRenderState livingState = (LivingEntityRenderState) state;
        // how far the head/look direction deviates from the torso
        float yawDelta = livingState.yRot - livingState.bodyRot;
        float pitch = livingState.xRot;

        // rotate the hand frame so its z axis faces the look direction,
        // translate, then rotate back: net effect is a pure offset along
        // the view direction regardless of body orientation
        poseStack.rotateAround(Axis.YP.rotationDegrees(yawDelta), 0.0F, 0.0F, 0.0F);
        poseStack.rotateAround(Axis.XN.rotationDegrees(pitch), 0.0F, 0.0F, 0.0F);
        poseStack.translate(0.0F, 0.0F, -push);
        poseStack.rotateAround(Axis.XP.rotationDegrees(pitch), 0.0F, 0.0F, 0.0F);
        poseStack.rotateAround(Axis.YN.rotationDegrees(yawDelta), 0.0F, 0.0F, 0.0F);
    }
}
