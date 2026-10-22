package warhammermod.Entities.Projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class ProjectileBase extends AbstractArrow {
    public float projectiledamage;
    protected float extradamage;
    protected int knocklevel;

    public void setpowerDamage(float powerIn) {
        extradamage = 1.5F * powerIn;
    }

    public void setknockbacklevel(int knockin) {
        knocklevel = knockin;
    }

    public float getTotalDamage() {
        return projectiledamage + extradamage;
    }

    public void applyspecialeffect(LivingEntity entity) {
    }

    protected ProjectileBase(EntityType<? extends ProjectileBase> entityType, Level world) {
        super(entityType, world);
        pickup = Pickup.DISALLOWED;
    }

    protected ProjectileBase(EntityType<? extends ProjectileBase> type, LivingEntity shooter, Level world, float damageIn) {
        super(type, shooter, world, ItemStack.EMPTY, ItemStack.EMPTY);
        projectiledamage = damageIn;
        pickup = Pickup.DISALLOWED;
    }

    protected ProjectileBase(EntityType<? extends ProjectileBase> entityType, Level world, double x, double y, double z) {
        super(entityType, x, y, z, world, ItemStack.EMPTY, ItemStack.EMPTY);
        pickup = Pickup.DISALLOWED;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        playSound(net.minecraft.sounds.SoundEvents.STONE_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        Entity owner = getOwner();
        float damage = getTotalDamage();
        if (isOnFire()) {
            entity.igniteForSeconds(5);
        }
        boolean hurt = entity.hurtServer((net.minecraft.server.level.ServerLevel) this.level(), damageSources().arrow(this, owner == null ? this : owner), damage);
        if (hurt) {
            if (entity instanceof LivingEntity living) {
                applyspecialeffect(living);
                if (this.knocklevel > 0) {
                    Vec3 vec = getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(this.knocklevel * 0.6D);
                    if (vec.lengthSqr() > 0.0D) {
                        living.push(vec.x, 0.1D, vec.z);
                    }
                }
                doPostHurtEffects(living);
            }
            playSound(net.minecraft.sounds.SoundEvents.STONE_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            discard();
        } else {
            setDeltaMovement(getDeltaMovement().scale(-0.1D));
            setYRot(getYRot() + 180.0F);
            if (!level().isClientSide() && getDeltaMovement().lengthSqr() < 1.0E-7D) {
                discard();
            }
        }
    }
}
