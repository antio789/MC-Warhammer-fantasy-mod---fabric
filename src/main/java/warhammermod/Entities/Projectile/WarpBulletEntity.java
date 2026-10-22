package warhammermod.Entities.Projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import warhammermod.utils.Registry.Entityinit;

public class WarpBulletEntity extends ProjectileBase {
    public WarpBulletEntity(EntityType<? extends WarpBulletEntity> type, Level world) {
        super(type, world);
    }

    public WarpBulletEntity(LivingEntity shooter, Level world, float damageIn) {
        super(Entityinit.WarpBullet, shooter, world, damageIn);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide() && !this.isInGround()) {
            spawnColoredParticles();
        }
    }

    @Override
    public void applyspecialeffect(LivingEntity entity) {
        if (random.nextFloat() < 0.3) {
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
        }
    }

    private void spawnColoredParticles() {
        double d0 = 1.0D;
        double d1 = 1.0D;
        double d2 = 0.0D;
        for (int j = 0; j < 5; ++j) {
            this.level().addParticle(net.minecraft.core.particles.ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 65280), this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(), this.getY() + this.random.nextDouble() * (double) this.getBbHeight(), this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(), d0, d1, d2);
        }
    }
}
