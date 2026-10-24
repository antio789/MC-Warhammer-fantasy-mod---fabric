package warhammermod.Entities.Projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import warhammermod.utils.Registry.Entityinit;

public class FlameEntity extends Fireball {
    private int limitedlifespan = 30;

    public FlameEntity(EntityType<? extends FlameEntity> type, Level level) {
        super(type, level);
        setItem(Items.BLAZE_ROD.getDefaultInstance());
    }

    public FlameEntity(Level worldIn, LivingEntity shooter, double d, double e, double f, Vec3 vec3d) {
        super(Entityinit.Flame, d, e, f, vec3d, worldIn);
        setItem(Items.BLAZE_ROD.getDefaultInstance());
    }

    public FlameEntity(Level worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(Entityinit.Flame, shooter, new Vec3(accelX, accelY, accelZ), worldIn);
        setItem(Items.BLAZE_ROD.getDefaultInstance());
    }

    public FlameEntity(Level world) {
        super(Entityinit.Flame, world);
        setItem(Items.BLAZE_ROD.getDefaultInstance());
    }

    @Override
    public void tick() {
        --limitedlifespan;
        if (limitedlifespan <= 0) {
            discard();
        }
        super.tick();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityhitresult) {
        super.onHitEntity(entityhitresult);
        if (this.level() instanceof ServerLevel world) {
            Entity entity = entityhitresult.getEntity();
            Entity entity2 = this.getOwner();
            int i = entity.getRemainingFireTicks();
            entity.igniteForSeconds(5.0f);
            DamageSource damageSource = this.damageSources().fireball(this, entity2);
            if (!entity.hurtServer(world, damageSource, 5.0f)) {
                entity.setRemainingFireTicks(i);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level().isClientSide()) {
            return;
        }
        Entity entity = this.getOwner();
        if (!(entity instanceof Mob) || ((ServerLevel) this.level()).getGameRules().get(GameRules.MOB_GRIEFING)) {
            BlockPos blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
            if (this.level().isEmptyBlock(blockPos)) {
                this.level().setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.level(), blockPos));
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }
}
