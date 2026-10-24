package warhammermod.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
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
@Mixin(ClientPlayerEntity.class)
public abstract class sprinting extends LivingEntity {


    protected sprinting(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    //ClientPlayerEntity player = ((ClientPlayerEntity)(Object)this);
    @Shadow boolean usingItem; // isusingitem() both in livingentity and clientplayer
    @Shadow Hand activeHand; //getactivehand() in both livingentity and clientplayer
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
            if(getStackInHand(getActiveHand()).getItem() instanceof SmallShieldTemplate)
            {

                return  this.isWalking() && this.canSprint() &&
                        !this.hasStatusEffect(StatusEffects.BLINDNESS) &&
                        (!this.hasVehicle() || this.canVehicleSprint(this.getVehicle())) && !this.isGliding();

            }else return info.getReturnValue();
        }
        return info.getReturnValue();
    }

    public Hand getActiveHand() {
        return Objects.requireNonNullElse(this.activeHand, Hand.MAIN_HAND);
    }

    private boolean isWalking() {
        double d = 0.8;
        return this.isSubmergedInWater() ? this.input.hasForwardMovement() : (double)this.input.movementForward >= 0.19;
    }
}
