package warhammermod.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class villagerlistnener {
    @Inject(at = @At("TAIL"),method = "<init>")
    private void init(EntityType e, Level w, CallbackInfo callbackInfo){
        System.out.print("does this work? \n");
    }
}
