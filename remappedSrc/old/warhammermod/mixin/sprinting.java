package warhammermod.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import warhammermod.Items.melee.SmallShieldTemplate;
import warhammermod.utils.functions;

import java.util.Objects;

//not functional, sprinting is not translated to actual movement increase
@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class sprinting extends LivingEntity {


    protected sprinting(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    //ClientPlayerEntity player = ((ClientPlayerEntity)(Object)this);
    @Shadow boolean usingItem; // isusingitem() both in livingentity and clientplayer
    @Shadow InteractionHand activeHand; //getactivehand() in both livingentity and clientplayer
    //@Shadow abstract ItemStack getStackInHand(Hand hand); in living entity
    //@Shadow abstract boolean isSprinting(); in entity
    @Shadow abstract boolean canSprint();
    @Shadow Input input;
    //@Shadow abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect); in livingentity
    //@Shadow abstract boolean hasVehicle(); in entity
    @Shadow abstract boolean canVehicleSprint(Entity entity);
    //@Shadow abstract Entity getVehicle(); in entity
    //@Shadow abstract boolean isFallFlying(); in livingentity

    @Inject(method="canStartSprinting",at = @At("TAIL"))
    public boolean cansprintwithshield(CallbackInfoReturnable<Boolean> info){
        if(!info.getReturnValue() && usingItem){
            if(getItemInHand(getUsedItemHand()).getItem() instanceof SmallShieldTemplate)
            {

                return  this.isWalking() && this.canSprint() &&
                        !this.hasEffect(MobEffects.BLINDNESS) &&
                        (!this.isPassenger() || this.canVehicleSprint(this.getVehicle())) && !this.isFallFlying();

            }else return info.getReturnValue();
        }
        return info.getReturnValue();
    }

    public InteractionHand getUsedItemHand() {
        return Objects.requireNonNullElse(this.activeHand, InteractionHand.MAIN_HAND);
    }

    private boolean isWalking() {
        double d = 0.8;
        return this.isUnderWater() ? this.input.hasForwardImpulse() : (double)this.input.forwardImpulse >= 0.19;
    }
}
