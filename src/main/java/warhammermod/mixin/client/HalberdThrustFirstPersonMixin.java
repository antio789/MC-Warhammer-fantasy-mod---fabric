package warhammermod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import warhammermod.Client.HalberdThrustAnimation;
import warhammermod.Items.melee.HalberdTemplate;

/**
 * Forward-thrust animation for the halberd charged attack, first person.
 * Appends a pure forward translation along the item axis while the local
 * thrust timer runs - no rotation, so it reads as a thrust instead of the
 * vanilla STAB swing's baked -70 degree arc.
 */
@Mixin(ItemInHandRenderer.class)
public class HalberdThrustFirstPersonMixin {

    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void warhammermod$halberdThrust(AbstractClientPlayer player, float partialTick, float partialTick2,
                                            InteractionHand hand, float f4, ItemStack stack, float equipProgress,
                                            PoseStack poseStack, SubmitNodeCollector collector, int light, CallbackInfo ci) {
        if (hand == InteractionHand.MAIN_HAND
                && stack.getItem() instanceof HalberdTemplate
                && HalberdThrustAnimation.isThrusting()) {
            // vanilla's own spear attack push uses positive z along this axis
            poseStack.translate(0.0F, 0.0F, -HalberdThrustAnimation.pushAmount());
        }
    }
}
