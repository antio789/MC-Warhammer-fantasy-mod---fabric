package warhammermod.Entities.Projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import warhammermod.utils.Registry.Entityinit;

public class Bullet extends ProjectileBase {
    public Bullet(EntityType<? extends Bullet> type, Level world) {
        super(type, world);
    }

    public Bullet(LivingEntity shooter, Level world, float damage) {
        super(Entityinit.Bullet, shooter, world, damage);
    }

    @Override
    public void tick() {
        if (this.level().isClientSide() && !this.isInGround()) {
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
        super.tick();
    }
}
