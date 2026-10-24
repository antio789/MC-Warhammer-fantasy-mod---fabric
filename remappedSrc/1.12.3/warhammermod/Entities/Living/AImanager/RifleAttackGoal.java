/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.ranged.RatlingGun;
import warhammermod.Items.ranged.SlingTemplate;
import warhammermod.Items.ranged.WarpgunTemplate;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;

import java.util.EnumSet;

public class RifleAttackGoal<T extends SkavenEntity & SkavenRangedUser>
extends Goal {
    public static final UniformIntProvider COOLDOWN_RANGE = TimeHelper.betweenSeconds(1, 2);
    private final T actor;
    private Stage stage = Stage.UNCHARGED;
    private final double speed;
    private final float squaredRange;
    private int seeingTargetTicker;
    private int chargedTicksLeft;
    private int cooldown;

    public RifleAttackGoal(T actor, double speed, float range) {
        this.actor = actor;
        this.speed = speed;
        this.squaredRange = range * range;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return this.hasAliveTarget() && this.isEntityHoldingCrossbow();
    }

    private boolean isEntityHoldingCrossbow() {
        return this.actor.isHolding(item -> (item.getItem() instanceof WarpgunTemplate || item.getItem() instanceof RatlingGun || item.getItem() instanceof SlingTemplate))||actor.getSkaventype().equals(reference.globadier);
    }

    @Override
    public boolean shouldContinue() {
        return this.hasAliveTarget() && (this.canStart() || !((MobEntity)this.actor).getNavigation().isIdle()) && this.isEntityHoldingCrossbow();
    }

    private boolean hasAliveTarget() {
        return ((MobEntity)this.actor).getTarget() != null && ((MobEntity)this.actor).getTarget().isAlive();
    }

    @Override
    public void stop() {
        super.stop();
        this.actor.setAttacking(false);
        this.actor.setTarget(null);
        this.seeingTargetTicker = 0;
        if (this.actor.isUsingItem()) {
            this.actor.clearActiveItem();
            this.actor.setCharging(false);
        }
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    public static int getPullTime(SkavenEntity user) {
        return user.getFirerate();
    }

    @Override
    public void tick() {
        boolean bl3;
        boolean bl2;
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity == null) {
            return;
        }
        boolean cansee = this.actor.getVisibilityCache().canSee(livingEntity);
        bl2 = this.seeingTargetTicker > 0;
        if (cansee != bl2) {
            this.seeingTargetTicker = 0;
        }
        if (cansee) {
            ++this.seeingTargetTicker;
        } else {
            --this.seeingTargetTicker;
        }
        double d = this.actor.squaredDistanceTo(livingEntity);
         bl3 = (d > (double)this.squaredRange || this.seeingTargetTicker < 5) && this.chargedTicksLeft == 0;
        if (bl3) {
            --this.cooldown;
            if (this.cooldown <= 0) {
                this.actor.getNavigation().startMovingTo(livingEntity, this.isUncharged() ? this.speed : this.speed * 0.5);
                this.cooldown = COOLDOWN_RANGE.get(this.actor.getRandom());
            }
        } else {
            this.cooldown = 0;
            this.actor.getNavigation().stop();
        }
        this.actor.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
        if (this.stage == Stage.UNCHARGED) {
            if (!bl3) {
                this.actor.setCurrentHand(Hand.MAIN_HAND);
                this.stage = Stage.CHARGING;
                this.actor.setCharging(true);
            }
        } else if (this.stage == Stage.CHARGING) {
            if (!this.actor.isUsingItem()) {
                this.stage = Stage.UNCHARGED;
            }
            if (this.actor.getItemUseTime() >= getPullTime(this.actor)) {
                this.actor.stopUsingItem();
                this.stage = Stage.CHARGED;
                this.chargedTicksLeft = 20 + this.actor.getRandom().nextInt(20);
                this.actor.setCharging(false);
                this.actor.getMainHandStack().set(WHRegistry.AMMO,new Ammocomponent(1,1));
            }
        } else if (this.stage == Stage.CHARGED) {
            --this.chargedTicksLeft;
            if (this.chargedTicksLeft == 0) {
                this.stage = Stage.READY_TO_ATTACK;
            }else actor.setAiming(true);
        } else if (this.stage == Stage.READY_TO_ATTACK && cansee) {
            this.actor.shootAt(livingEntity, 1.0f);
            this.actor.getMainHandStack().set(WHRegistry.AMMO,new Ammocomponent(0,0));
            this.stage = Stage.UNCHARGED;
            this.actor.setAiming(false);
        }
    }

    private boolean isUncharged() {
        return this.stage == Stage.UNCHARGED;
    }

    static enum Stage {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;

    }
}

