package warhammermod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
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
 * Forward-thrust animation for the halberd charged attack, third person:
 * mirrors the first person push so other players see the weapon punch
 * forward along its axis instead of the vanilla swing arc.
 */
@Mixin(ItemInHandLayer.class)
public class HalberdThrustThirdPersonMixin {

    @Inject(method = "submitArmWithItem", at = @At("TAIL"))
    private void warhammermod$halberdThrust(ArmedEntityRenderState state, ItemStackRenderState itemRenderState,
                                            ItemStack stack, HumanoidArm arm, PoseStack poseStack,
                                            SubmitNodeCollector collector, int light, CallbackInfo ci) {
        if (stack.getItem() instanceof HalberdTemplate && HalberdThrustAnimation.isThrusting()) {
            // only animate the local player's own thrust (other players use
            // the vanilla broadcast animation for normal attacks; a remote
            // halberd release is not tracked client-side)
            if (state instanceof HumanoidRenderState) {
                // the local player check is implicit: the thrust timer only
                // ever runs for the local player
                poseStack.translate(0.0F, 0.0F, HalberdThrustAnimation.pushAmount());
            }
        }
    }
}
