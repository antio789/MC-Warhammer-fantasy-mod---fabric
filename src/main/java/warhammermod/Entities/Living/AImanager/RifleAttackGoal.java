package warhammermod.Entities.Living.AImanager;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.ranged.RatlingGun;
import warhammermod.Items.ranged.SlingTemplate;
import warhammermod.Items.ranged.WarpgunTemplate;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;

import java.util.EnumSet;

public class RifleAttackGoal<T extends SkavenEntity & SkavenRangedUser> extends Goal {
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
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.hasAliveTarget() && this.isEntityHoldingRifle();
    }

    private boolean isEntityHoldingRifle() {
        return this.actor.isHolding(item -> (item.getItem() instanceof WarpgunTemplate || item.getItem() instanceof RatlingGun || item.getItem() instanceof SlingTemplate)) || actor.getSkaventype().equals(reference.globadier);
    }

    @Override
    public boolean canContinueToUse() {
        return this.hasAliveTarget() && (this.canUse() || !this.actor.getNavigation().isDone()) && this.isEntityHoldingRifle();
    }

    private boolean hasAliveTarget() {
        return this.actor.getTarget() != null && this.actor.getTarget().isAlive();
    }

    @Override
    public void stop() {
        super.stop();
        this.actor.setAggressive(false);
        this.actor.setTarget(null);
        this.seeingTargetTicker = 0;
        if (this.actor.isUsingItem()) {
            this.actor.stopUsingItem();
            this.actor.setChargingCrossbow(false);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public static int getPullTime(SkavenEntity user) {
        return user.getFirerate();
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity == null) {
            return;
        }
        boolean cansee = this.actor.getSensing().hasLineOfSight(livingEntity);
        boolean bl2 = this.seeingTargetTicker > 0;
        if (cansee != bl2) {
            this.seeingTargetTicker = 0;
        }
        if (cansee) {
            ++this.seeingTargetTicker;
        } else {
            --this.seeingTargetTicker;
        }
        double d = this.actor.distanceToSqr(livingEntity);
        boolean bl3 = (d > (double) this.squaredRange || this.seeingTargetTicker < 5) && this.chargedTicksLeft == 0;
        if (bl3) {
            --this.cooldown;
            if (this.cooldown <= 0) {
                this.actor.getNavigation().moveTo(livingEntity, this.isUncharged() ? this.speed : this.speed * 0.5);
                RandomSource random = this.actor.getRandom();
                this.cooldown = 20 + random.nextInt(20);
            }
        } else {
            this.cooldown = 0;
            this.actor.getNavigation().stop();
        }
        this.actor.getLookControl().setLookAt(livingEntity, 30.0f, 30.0f);
        if (this.stage == Stage.UNCHARGED) {
            if (!bl3) {
                this.actor.startUsingItem(InteractionHand.MAIN_HAND);
                this.stage = Stage.CHARGING;
                this.actor.setChargingCrossbow(true);
            }
        } else if (this.stage == Stage.CHARGING) {
            if (!this.actor.isUsingItem()) {
                this.stage = Stage.UNCHARGED;
            }
            if (this.actor.getTicksUsingItem() >= getPullTime(this.actor)) {
                this.actor.stopUsingItem();
                this.stage = Stage.CHARGED;
                this.chargedTicksLeft = 20 + this.actor.getRandom().nextInt(20);
                this.actor.setChargingCrossbow(false);
                this.actor.getMainHandItem().set(WHRegistry.AMMO, new Ammocomponent(1, 1));
            }
        } else if (this.stage == Stage.CHARGED) {
            --this.chargedTicksLeft;
            if (this.chargedTicksLeft == 0) {
                this.stage = Stage.READY_TO_ATTACK;
            } else actor.setAiming(true);
        } else if (this.stage == Stage.READY_TO_ATTACK && cansee) {
            this.actor.performRangedAttack(livingEntity, 1.0f);
            this.actor.getMainHandItem().set(WHRegistry.AMMO, new Ammocomponent(0, 0));
            this.stage = Stage.UNCHARGED;
            this.actor.setAiming(false);
        }
    }

    private boolean isUncharged() {
        return this.stage == Stage.UNCHARGED;
    }

    enum Stage {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;
    }
}
