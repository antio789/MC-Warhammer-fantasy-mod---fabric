package warhammermod.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import warhammermod.Items.IReloadItem;

/**
 * 26.2 port of the 1.21.3 gunreload mixin: while a gun is being reloaded,
 * draws the vanilla attack-indicator bar under the crosshair as a reload
 * countdown. (The old mixin's jezzail spyglass-overlay branch is now handled
 * natively via JezzailScopeMixin/Player.isScoping.)
 */
@Mixin(Hud.class)
public class GunReloadHudMixin {

    private static final Identifier ATTACK_INDICATOR_BACKGROUND =
            Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_background");
    private static final Identifier ATTACK_INDICATOR_PROGRESS =
            Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_progress");

    @Inject(method = "extractCrosshair", at = @At("TAIL"))
    private void warhammermod$renderReloadIndicator(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !minecraft.options.getCameraType().isFirstPerson() || !player.isUsingItem()) {
            return;
        }
        ItemStack stack = player.getUseItem();
        if (stack.getItem() instanceof IReloadItem gun
                && !gun.isReadytoFire(stack) && !player.isCreative()) {
            int reloadTime = gun.getTimetoreload();
            float remaining = Mth.clamp((reloadTime - player.getTicksUsingItem()) / (float) reloadTime, 0.0F, 1.0F);
            int x = extractor.guiWidth() / 2 - 8;
            int y = extractor.guiHeight() / 2 - 7 + 16;
            int progress = (int) (remaining * 17.0F);
            extractor.blitSprite(RenderPipelines.CROSSHAIR, ATTACK_INDICATOR_BACKGROUND, x, y, 16, 4);
            if (progress > 0) {
                extractor.blitSprite(RenderPipelines.CROSSHAIR, ATTACK_INDICATOR_PROGRESS,
                        16, 4, 0, 0, x, y, progress, 4);
            }
        }
    }
}
