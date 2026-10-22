package warhammermod.Entities.Projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import warhammermod.utils.Registry.Entityinit;

public class FlameEntity extends Fireball {
    private int limitedlifespan = 30;

    public FlameEntity(EntityType<? extends FlameEntity> type, Level world) {
        super(type, world);
        setItem(Items.BLAZE_ROD.getDefaultInstance());
    }

    public FlameEntity(Level world, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(Entityinit.Flame, shooter, new Vec3(accelX, accelY, accelZ), world);
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
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!this.level().isClientSide()) {
            Entity entity = hitResult.getEntity();
            if (!entity.fireImmune()) {
                Entity owner = this.getOwner();
                int i = entity.getRemainingFireTicks();
                entity.igniteForSeconds(5);
                DamageSource damagesource = this.damageSources().fireball(this, owner == null ? this : owner);
                if (!entity.hurtServer((net.minecraft.server.level.ServerLevel) this.level(), damagesource, 5.0F)) {
                    entity.setRemainingFireTicks(i);
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!this.level().isClientSide()) {
            Entity entity = this.getOwner();
            if (this.level().isEmptyBlock(blockHitResult.getBlockPos())) {
                BlockPos blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                if (this.level().isEmptyBlock(blockPos)) {
                    this.level().setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.level(), blockPos));
                }
            }
            if (entity == null) {
                discard();
            }
        }
    }
}
