package warhammermod.Entities.Projectile;


import net.minecraft.block.AbstractFireBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import warhammermod.utils.Registry.Entityinit;

public class FlameEntity extends AbstractFireballEntity {
    private int limitedlifespan=30;


    public FlameEntity(EntityType<? extends FlameEntity> p_i50160_1_, World p_i50160_2_) {
        super(p_i50160_1_, p_i50160_2_);
    }

    public FlameEntity(World worldIn, LivingEntity shooter, double d, double e, double f, Vec3d vec3d) {
        super(Entityinit.Flame, d, e, f,vec3d, worldIn);
    }

    public FlameEntity(LivingEntity livingEntity,Vec3d vec3d,World worldIn) {
        super(Entityinit.Flame, livingEntity,vec3d, worldIn);
    }

    public FlameEntity(World world){
        super(Entityinit.Flame,world);
    }



    public void tick()
    {
        --limitedlifespan;
        if(limitedlifespan<=0){remove(RemovalReason.DISCARDED);}
        super.tick();
    }

    protected void onEntityHit(EntityHitResult entityhitresult) {
        super.onEntityHit(entityhitresult);
        if (this.getWorld() instanceof ServerWorld world){
            Entity entity = entityhitresult.getEntity();
            Entity entity2 = this.getOwner();
            int i = entity.getFireTicks();
            entity.setOnFireFor(5.0f);
            DamageSource damageSource = this.getDamageSources().fireball(this, entity2);
            if (!entity.clientDamage(damageSource, 5.0f)) {
                entity.setFireTicks(i);
            } else {
                EnchantmentHelper.onTargetDamaged(world, entity, damageSource);
            }

        }
    }
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (this.getWorld().isClient) {
            return;
        }
        Entity entity = this.getOwner();
        if (!(entity instanceof MobEntity) || this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            BlockPos blockPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
            if (this.getWorld().isAir(blockPos)) {
                this.getWorld().setBlockState(blockPos, AbstractFireBlock.getState(this.getWorld(), blockPos));
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.discard();
        }
    }
    @Override
    public boolean clientDamage(DamageSource source, float amount) {
        return false;
    }

}
