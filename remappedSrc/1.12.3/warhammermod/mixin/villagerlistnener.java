package warhammermod.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public class villagerlistnener {
    @Inject(at = @At("TAIL"),method = "<init>")
    private void init(EntityType e, World w, CallbackInfo callbackInfo){
        System.out.print("does this work? \n");
    }
}
