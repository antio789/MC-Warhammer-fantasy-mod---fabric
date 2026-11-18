package warhammermod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
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
 * Mirrors SpearAnimations.thirdPersonAttackItem, which pushes the weapon
 * along the item frame's Y axis (jetForward * (attack - retract)) - fully
 * hand-frame-relative, no absolute space math. The arm is aimed at the
 * player's look by HalberdThrustArmMixin (vanilla thirdPersonHandUse),
 * so the frame's Y already points along the thrust direction.
 */
@Mixin(ItemInHandLayer.class)
public class HalberdThrustThirdPersonMixin {

    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void warhammermod$halberdThrust(ArmedEntityRenderState state, ItemStackRenderState itemRenderState,
                                            ItemStack stack, HumanoidArm arm, PoseStack poseStack,
                                            SubmitNodeCollector collector, int light, CallbackInfo ci) {
        float push = HalberdThrustAnimation.pushAmount();
        if (push != 0.0F && stack.getItem() instanceof HalberdTemplate) {
            // vanilla's spear attack pushes along this frame's Y axis
            poseStack.translate(0.0F, push, 0.0F);
        }
    }
}
