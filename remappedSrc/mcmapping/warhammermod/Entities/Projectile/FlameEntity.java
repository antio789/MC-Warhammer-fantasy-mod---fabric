package warhammermod.Entities.Projectile;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import warhammermod.utils.Registry.Entityinit;

public class FlameEntity extends Fireball {
    private int limitedlifespan=30;


    public FlameEntity(EntityType<? extends FlameEntity> p_i50160_1_, Level p_i50160_2_) {
        super(p_i50160_1_, p_i50160_2_);
    }

    public FlameEntity(Level worldIn, LivingEntity shooter, double d, double e, double f, Vec3 vec3d) {
        super(Entityinit.Flame, d, e, f,vec3d, worldIn);
    }

    public FlameEntity(LivingEntity livingEntity,Vec3 vec3d,Level worldIn) {
        super(Entityinit.Flame, livingEntity,vec3d, worldIn);
    }

    public FlameEntity(Level world){
        super(Entityinit.Flame,world);
    }



    public void tick()
    {
        --limitedlifespan;
        if(limitedlifespan<=0){remove(RemovalReason.DISCARDED);}
        super.tick();
    }

    protected void onHitEntity(EntityHitResult entityhitresult) {
        super.onHitEntity(entityhitresult);
        if (this.level() instanceof ServerLevel world){
            Entity entity = entityhitresult.getEntity();
            Entity entity2 = this.getOwner();
            int i = entity.getRemainingFireTicks();
            entity.igniteForSeconds(5.0f);
            DamageSource damageSource = this.damageSources().fireball(this, entity2);
            if (!entity.hurt(damageSource, 5.0f)) {
                entity.setRemainingFireTicks(i);
            } else {
                EnchantmentHelper.doPostAttackEffects(world, entity, damageSource);
            }

        }
    }
    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = this.getOwner();
        if (!(entity instanceof Mob) || this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockPos blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
            if (this.level().isEmptyBlock(blockPos)) {
                this.level().setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.level(), blockPos));
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

}
