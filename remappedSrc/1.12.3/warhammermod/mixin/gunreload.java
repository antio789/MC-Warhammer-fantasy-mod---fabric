package warhammermod.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import warhammermod.Client.Clientside;
import warhammermod.Items.IReloadItem;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class gunreload {
    private static final Identifier WIKI_image = Identifier.of(reference.modid,"textures/special/modwiki.png");


    @Shadow protected abstract void renderSpyglassOverlay(DrawContext context, float scale);


    @Shadow private static final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = Identifier.ofVanilla((String)"hud/crosshair_attack_indicator_background");
    @Shadow private static final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE = Identifier.ofVanilla((String)"hud/crosshair_attack_indicator_progress");

    @Inject(at = @At("RETURN"), method = "renderCrosshair")
    private void renderreload(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci){

        if(MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getActiveItem().getItem() instanceof IReloadItem) {
            IReloadItem item = (IReloadItem) MinecraftClient.getInstance().player.getActiveItem().getItem();
            if (!item.isReadytoFire(MinecraftClient.getInstance().player.getActiveItem()) && !MinecraftClient.getInstance().player.isCreative()) {

                int reloadtime = item.getTimetoreload();
                float diff = reloadtime - MinecraftClient.getInstance().player.getItemUseTime();
                float f = MathHelper.clamp(diff / reloadtime, 0,1);


                int j = context.getScaledWindowHeight() / 2 - 7 + 16;
                int k = context.getScaledWindowWidth() / 2 - 8;

                int l = (int) (f * 17.0F);
                context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, k, j, 16, 4);
                context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, k, j, l, 4);
            }
            else if(MinecraftClient.getInstance().player.getActiveItem().getItem() == ItemsInit.Warplock_jezzail && MinecraftClient.getInstance().options.getPerspective().isFirstPerson()){
                renderSpyglassOverlay(context,1);
            }

        }
        /*
        if(Clientside.Wiki_Map.isPressed()){
            this.renderTextureOverlay(WIKI_image,1);
        }

         */
    }
}
