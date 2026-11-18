package warhammermod.Client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import warhammermod.Items.melee.HalberdTemplate;

/**
 * Client-side "forward thrust" animation state for the halberd charged
 * attack. Tracks the charge (armed) state of the local player each tick;
 * when the player releases a fully charged halberd, a short thrust timer
 * starts. The two client mixins apply a pure forward translation along
 * the item axis while the timer runs (no rotation, unlike the vanilla
 * STAB swing which carries a baked -70 degree arc).
 */
public final class HalberdThrustAnimation {

    /** Total animation length in ticks; vanilla attack cycle is 6. */
    public static final int DURATION_TICKS = 6;
    /** Maximum forward push in blocks along the item axis. */
    public static final float MAX_PUSH = 0.85F;
    /** Charge (in ticks of use time) needed for the power attack. Must match HalberdTemplate. */
    private static final float CHARGE_TICKS = 20.0F;

    private static int thrustTicks = 0;
    private static boolean armed = false;

    private HalberdThrustAnimation() {
    }

    /** Call once per client tick (END_CLIENT_TICK). */
    public static void tick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.isUsingItem()
                && player.getUseItem().getItem() instanceof HalberdTemplate
                && player.getTicksUsingItem() >= CHARGE_TICKS) {
            armed = true;
        } else if (armed) {
            // the fully charged use just ended -> release happened
            armed = false;
            thrustTicks = DURATION_TICKS;
        }
        if (thrustTicks > 0) {
            thrustTicks--;
        }
    }

    /** True while the thrust animation is playing. */
    public static boolean isThrusting() {
        return thrustTicks > 0;
    }

    /**
     * Signed amount to translate the item along its thrust axis.
     * Progress goes 0 -> 1 over the animation; the push eases out to
     * MAX_PUSH and back (sinusoidal out-and-return).
     */
    public static float pushAmount() {
        if (thrustTicks <= 0) {
            return 0.0F;
        }
        float progress = 1.0F - (thrustTicks / (float) DURATION_TICKS);
        return Mth.sin(progress * Mth.PI) * MAX_PUSH;
    }
}
