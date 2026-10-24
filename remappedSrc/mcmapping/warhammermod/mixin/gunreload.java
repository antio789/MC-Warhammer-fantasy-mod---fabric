package warhammermod.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
@Mixin(Gui.class)
public abstract class gunreload {
    private static final ResourceLocation WIKI_image = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/special/modwiki.png");


    @Shadow protected abstract void renderSpyglassOverlay(GuiGraphics context, float scale);


    @Shadow private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = ResourceLocation.withDefaultNamespace((String)"hud/crosshair_attack_indicator_background");
    @Shadow private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE = ResourceLocation.withDefaultNamespace((String)"hud/crosshair_attack_indicator_progress");

    @Inject(at = @At("RETURN"), method = "renderCrosshair")
    private void renderreload(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci){

        if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUseItem().getItem() instanceof IReloadItem) {
            IReloadItem item = (IReloadItem) Minecraft.getInstance().player.getUseItem().getItem();
            if (!item.isReadytoFire(Minecraft.getInstance().player.getUseItem()) && !Minecraft.getInstance().player.isCreative()) {

                int reloadtime = item.getTimetoreload();
                float diff = reloadtime - Minecraft.getInstance().player.getTicksUsingItem();
                float f = Mth.clamp(diff / reloadtime, 0,1);


                int j = context.guiHeight() / 2 - 7 + 16;
                int k = context.guiWidth() / 2 - 8;

                int l = (int) (f * 17.0F);
                context.blitSprite(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, k, j, 16, 4);
                context.blitSprite(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, k, j, l, 4);
            }
            else if(Minecraft.getInstance().player.getUseItem().getItem() == ItemsInit.Warplock_jezzail && Minecraft.getInstance().options.getCameraType().isFirstPerson()){
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
