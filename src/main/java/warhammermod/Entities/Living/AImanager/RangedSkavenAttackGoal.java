package warhammermod.Entities.Living.AImanager;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Hand;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.Items.ranged.RatlingGun;
import warhammermod.Items.ranged.SlingTemplate;
import warhammermod.Items.ranged.WarpgunTemplate;
import warhammermod.utils.reference;

import java.util.EnumSet;

public class RangedSkavenAttackGoal<T extends HostileEntity & RangedAttackMob> extends Goal {
   private final SkavenEntity mob;
   private final double speedModifier;
   private int attackIntervalMin;
   private final float attackRadiusSqr;
   private int attackTime = -1;
   private int seeTime;
   private boolean strafingClockwise;
   private boolean strafingBackwards;
   private int strafingTime = -1;

   public RangedSkavenAttackGoal(SkavenEntity p_i47515_1_, double p_i47515_2_, int p_i47515_4_, float p_i47515_5_) {
      this.mob = p_i47515_1_;
      this.speedModifier = p_i47515_2_;
      this.attackIntervalMin = p_i47515_4_;
      this.attackRadiusSqr = p_i47515_5_ * p_i47515_5_;
      this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
   }

   public void setAttackInterval(int p_189428_1_) {
      this.attackIntervalMin = p_189428_1_;
   }

   public boolean canStart() {
      return this.mob.getTarget() != null && this.isHoldingBow();
   }

   protected boolean isHoldingBow() {
      return this.mob.isHolding(item -> (item.getItem() instanceof WarpgunTemplate || item.getItem() instanceof RatlingGun || item.getItem() instanceof SlingTemplate))||mob.getSkaventype().equals(reference.globadier);
   }

   public boolean shouldContinue() {
      return (this.canStart() || !this.mob.getNavigation().isIdle()) && this.isHoldingBow();
   }

   public void start() {
      super.start();
      this.mob.setAttacking(true);
   }

   public void stop() {
      super.stop();
      this.mob.setAttacking(false);
      this.seeTime = 0;
      this.attackTime = -1;
      this.mob.clearActiveItem();
   }

   public void tick() {
      LivingEntity livingentity = this.mob.getTarget();
      if (livingentity == null) {
         return;
      }
      double d = this.mob.squaredDistanceTo(livingentity.getX(), livingentity.getY(), livingentity.getZ());
      boolean flag = this.mob.getVisibilityCache().canSee(livingentity);
      boolean flag1 = this.seeTime > 0;
      if (flag != flag1) {
         this.seeTime = 0;
      }

      if (flag) {
         ++this.seeTime;
      } else {
         --this.seeTime;
      }

      if (!(d > (double)this.attackRadiusSqr) && this.seeTime >= 20) {
         this.mob.getNavigation().stop();
         ++this.strafingTime;
      } else {
         this.mob.getNavigation().startMovingTo(livingentity, this.speedModifier);
         this.strafingTime = -1;
      }

      if (this.strafingTime >= 20) {
         if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
            this.strafingClockwise = !this.strafingClockwise;
         }

         if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
            this.strafingBackwards = !this.strafingBackwards;
         }

         this.strafingTime = 0;
      }

      if (this.strafingTime > -1) {
         if (d > (double)(this.attackRadiusSqr * 0.75F)) {
            this.strafingBackwards = false;
         } else if (d < (double)(this.attackRadiusSqr * 0.25F)) {
            this.strafingBackwards = true;
         }

         //this.mob.getMoveControl().strafeTo(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
         this.mob.lookAtEntity(livingentity, 30.0F, 30.0F);
      } else {
         this.mob.getLookControl().lookAt(livingentity, 30.0F, 30.0F);
      }
      if (this.mob.isUsingItem()) {
         if (!flag && this.seeTime < -60) {
            this.mob.clearActiveItem();
         } else if (flag) {
            int i = this.mob.getItemUseTime();
            if (i >= 20) {
               this.mob.clearActiveItem();
               this.mob.attack(livingentity, 0);
               this.attackTime = this.attackIntervalMin;
            }
         }
      } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
         this.mob.setCurrentHand(Hand.MAIN_HAND);
      }

   }
}
