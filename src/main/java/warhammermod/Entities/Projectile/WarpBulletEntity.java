package warhammermod.Entities.Projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;

public class WarpBulletEntity extends ProjectileBase {

    public WarpBulletEntity(Level world) {
        super(Entityinit.WarpBullet, world);
    }

    public WarpBulletEntity(EntityType<? extends WarpBulletEntity> type, Level level) {
        super(type, level);
    }

    public WarpBulletEntity(Level worldin, LivingEntity shooter, float damageIn, EntityType<? extends ProjectileBase> type, ItemStack stack, ItemStack ammo) {
        super(type, worldin, shooter, ammo, damageIn, stack);
    }

    public WarpBulletEntity(LivingEntity shooter, Level world, float damageIn, ItemStack stack) {
        this(world, shooter, damageIn, Entityinit.WarpBullet, stack, new ItemStack(ItemsInit.Warpstone));
    }

    public WarpBulletEntity(LivingEntity shooter, Level world, float damageIn, ItemStack stack, ItemStack ammo) {
        this(world, shooter, damageIn, Entityinit.WarpBullet, stack, ammo);
    }

    int delayedtick = 0;

    @Override
    public void tick() {
        super.tick();

        if (delayedtick <= 0 && this.level().isClientSide() && !this.isInGround()) {
            delayedtick = 3;
            spawnColoredParticles();
        }
        delayedtick--;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ItemsInit.Warpstone);
    }

    @Override
    public void applyspecialeffect(LivingEntity entity) {
        if (random.nextFloat() < 0.3) {
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
        }
    }

    private void spawnColoredParticles() {
        double d0 = 0.0D;
        double d1 = 1.0D;
        double d2 = 0.0D;

        for (int j = 0; j < 5; ++j) {
            this.level().addParticle(WHRegistry.WARP, this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(), this.getY() + this.random.nextDouble() * (double) this.getBbHeight(), this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(), d0, d1, d2);
        }
    }
}
