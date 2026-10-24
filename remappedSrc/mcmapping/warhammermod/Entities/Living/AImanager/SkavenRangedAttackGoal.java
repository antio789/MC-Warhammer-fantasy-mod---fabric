/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager;

import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.Items.ranged.RatlingGun;
import warhammermod.Items.ranged.SlingTemplate;
import warhammermod.Items.ranged.WarpgunTemplate;
import warhammermod.utils.reference;

import java.util.EnumSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.BowItem;

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
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public SkavenRangedAttackGoal(T actor, double speed, int attackInterval, float range,boolean canstrafe) {
        this(actor, speed, attackInterval, range);
        this.canStrafe=canstrafe;

    }
    public void setAttackInterval(int attackInterval) {
        this.attackInterval = attackInterval;
    }

    @Override
    public boolean canUse() {
        if (((Mob)this.actor).getTarget() == null) {
            return false;
        }
        return this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return this.actor.isHolding(item -> (item.getItem() instanceof WarpgunTemplate || item.getItem() instanceof RatlingGun || item.getItem() instanceof SlingTemplate))||actor.getSkaventype().equals(reference.globadier);
    }
    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !((Mob)this.actor).getNavigation().isDone()) && this.isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        ((Mob)this.actor).setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        ((Mob)this.actor).setAggressive(false);
        this.seeingTargetTicker = 0;
        this.cooldown = -1;
        ((LivingEntity)this.actor).stopUsingItem();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        boolean bl2;
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity == null) {
            return;
        }

        boolean canSee = this.actor.getSensing().hasLineOfSight(livingEntity);
        bl2 = this.seeingTargetTicker > 0;
        if (canSee != bl2) {
            this.seeingTargetTicker = 0;
        }
        if (canSee) {
            ++this.seeingTargetTicker;
        } else {
            --this.seeingTargetTicker;
        }
        double d = this.actor.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        if (d > (double)this.squaredRange || this.seeingTargetTicker < 20) {
            this.actor.getNavigation().moveTo(livingEntity, this.speed);
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
            if(canStrafe) this.actor.getMoveControl().strafe(this.backward ? -0.5f : 0.5f, 0);
            Entity entity = this.actor.getControlledVehicle();
            if (entity instanceof Mob mobEntity) {
                mobEntity.lookAt(livingEntity, 30.0f, 30.0f);
            }
            ((Mob)this.actor).lookAt(livingEntity, 30.0f, 30.0f);
        }
        this.actor.getLookControl().setLookAt(livingEntity, 30.0f, 30.0f);

        if (((LivingEntity)this.actor).isUsingItem()) {
            int i;
            if (!canSee && this.seeingTargetTicker < -60) {
                ((LivingEntity)this.actor).stopUsingItem();
            } else if (canSee && (i = ((LivingEntity)this.actor).getTicksUsingItem()) >= 10) {
                ((LivingEntity)this.actor).stopUsingItem();
                ((RangedAttackMob)this.actor).performRangedAttack(livingEntity, BowItem.getPowerForTime(i));
                this.cooldown = this.attackInterval;
            }
        } else if (--this.cooldown <= 0 && this.seeingTargetTicker >= -60) {
            this.actor.startUsingItem(InteractionHand.MAIN_HAND);
        }
    }


}

