package warhammermod.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import warhammermod.utils.functions;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public class setsprintingttest {
    @Inject(method="setSprinting",at = @At("TAIL"))
    public void testing(boolean sprinting,CallbackInfo info){
        functions.printer(sprinting + "  testingin setSprinting class");
    }
}
