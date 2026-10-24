/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.Hand;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.Items.ranged.RatlingGun;
import warhammermod.Items.ranged.SlingTemplate;
import warhammermod.Items.ranged.WarpgunTemplate;
import warhammermod.utils.reference;

import java.util.EnumSet;

public class SkavenRangedAttackGoal<T extends SkavenEntity>
        extends Goal {
    private final T actor;
    private final double speed;
    private int attackInterval;
    private final float squaredRange;
    private int cooldown = -1;
    private int seeingTargetTicker;
    private boolean canStrafe =true;
    private boolean backward;
    private int combatTicks = -1;

    public SkavenRangedAttackGoal(T actor, double speed, int attackInterval, float range) {
        this.actor = actor;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.squaredRange = range * range;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public SkavenRangedAttackGoal(T actor, double speed, int attackInterval, float range,boolean canstrafe) {
        this(actor, speed, attackInterval, range);
        this.canStrafe=canstrafe;

    }
    public void setAttackInterval(int attackInterval) {
        this.attackInterval = attackInterval;
    }

    @Override
    public boolean canStart() {
        if (((MobEntity)this.actor).getTarget() == null) {
            return false;
        }
        return this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return this.actor.isHolding(item -> (item.getItem() instanceof WarpgunTemplate || item.getItem() instanceof RatlingGun || item.getItem() instanceof SlingTemplate))||actor.getSkaventype().equals(reference.globadier);
    }
    @Override
    public boolean shouldContinue() {
        return (this.canStart() || !((MobEntity)this.actor).getNavigation().isIdle()) && this.isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        ((MobEntity)this.actor).setAttacking(true);
    }

    @Override
    public void stop() {
        super.stop();
        ((MobEntity)this.actor).setAttacking(false);
        this.seeingTargetTicker = 0;
        this.cooldown = -1;
        ((LivingEntity)this.actor).clearActiveItem();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        boolean bl2;
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity == null) {
            return;
        }

        boolean canSee = this.actor.getVisibilityCache().canSee(livingEntity);
        bl2 = this.seeingTargetTicker > 0;
        if (canSee != bl2) {
            this.seeingTargetTicker = 0;
        }
        if (canSee) {
            ++this.seeingTargetTicker;
        } else {
            --this.seeingTargetTicker;
        }
        double d = this.actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        if (d > (double)this.squaredRange || this.seeingTargetTicker < 20) {
            this.actor.getNavigation().startMovingTo(livingEntity, this.speed);
            this.combatTicks = -1;
        } else {
            this.actor.getNavigation().stop();
            ++this.combatTicks;
        }
        if (this.combatTicks > -1) {
            if (d > (double)(this.squaredRange * 0.75f)) {
                this.backward = false;
            } else if (d < (double)(this.squaredRange * 0.25f)) {
                this.backward = true;
            }
            if(canStrafe) this.actor.getMoveControl().strafeTo(this.backward ? -0.5f : 0.5f, 0);
            Entity entity = this.actor.getControllingVehicle();
            if (entity instanceof MobEntity mobEntity) {
                mobEntity.lookAtEntity(livingEntity, 30.0f, 30.0f);
            }
            ((MobEntity)this.actor).lookAtEntity(livingEntity, 30.0f, 30.0f);
        }
        this.actor.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);

        if (((LivingEntity)this.actor).isUsingItem()) {
            int i;
            if (!canSee && this.seeingTargetTicker < -60) {
                ((LivingEntity)this.actor).clearActiveItem();
            } else if (canSee && (i = ((LivingEntity)this.actor).getItemUseTime()) >= 10) {
                ((LivingEntity)this.actor).clearActiveItem();
                ((RangedAttackMob)this.actor).shootAt(livingEntity, BowItem.getPullProgress(i));
                this.cooldown = this.attackInterval;
            }
        } else if (--this.cooldown <= 0 && this.seeingTargetTicker >= -60) {
            this.actor.setCurrentHand(Hand.MAIN_HAND);
        }
    }


}

