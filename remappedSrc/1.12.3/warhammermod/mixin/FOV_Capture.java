package warhammermod.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import warhammermod.Items.GunBase;
import warhammermod.utils.Registry.ItemsInit;
@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public class FOV_Capture {
    @Inject(at = @At("RETURN"), method = "getFovMultiplier", cancellable = true)
    private void changefov(CallbackInfoReturnable<Float> cir){
        ClientPlayerEntity player =MinecraftClient.getInstance().player;
        if(player!=null && cir.getReturnValue()>=1) {
            Item item = player.getActiveItem().getItem();
            if (item == ItemsInit.Warplock_jezzail && (((GunBase) item).isReadytoFire(player.getActiveItem())||player.isCreative())) {
                cir.setReturnValue(0.5F);
            }
        }
    }
}
