package warhammermod.Client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Ease;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

/**
 * Custom first-person animation for the halberd's charged thrust. Called by
 * HalberdFirstPersonThrustMixin in place of SpearAnimations.firstPersonAttack
 * while a halberd swings with the HALBERD_THRUST flag set.
 *
 * For now firstPersonThrust is an exact copy of vanilla's
 * SpearAnimations.firstPersonAttack - tweak the constants below to change
 * the motion. Frame at the call site: after ItemInHandRenderer
 * .applyItemArmTransform, so +Z is forward toward the crosshair, +Y up.
 * {@code attack} is the swing cycle, 0 -> 1 over the swing duration.
 */
@Environment(EnvType.CLIENT)
public final class HalberdAnimations {

    private HalberdAnimations() {
    }

    private static float progress(float time, float start, float end) {
        return Mth.clamp(Mth.inverseLerp(time, start, end), 0.0F, 1.0F);
    }

    /**
     * Exact copy of SpearAnimations.firstPersonAttack (26.2) - the halberd
     * thrust starts from the identical curve and can be tweaked from here.
     */
    public static void firstPersonThrust(float attack, PoseStack poseStack, int invert, HumanoidArm arm) {
        float startingAmount = Ease.inOutSine(progress(attack, 0.0F, 0.05F));
        float middleAmount = Ease.outBack(progress(attack, 0.05F, 0.2F));
        float endingAmount = Ease.inOutExpo(progress(attack, 0.4F, 1.0F));
        poseStack.translate(invert * 0.1F * (startingAmount - middleAmount), -0.075F * (startingAmount - endingAmount), 0.65F * (startingAmount - middleAmount));
        poseStack.mulPose(Axis.XP.rotationDegrees(-70.0F * (startingAmount - endingAmount)));
        poseStack.translate(0.0F, 0.0F, -0.25F * (endingAmount - middleAmount));
    }
}
