package warhammermod.Entities.Projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import warhammermod.utils.Registry.Entityinit;

public class ShotEntity extends ProjectileBase {
    private int timer;

    public ShotEntity(EntityType<? extends ShotEntity> type, Level world) {
        super(type, world);
    }

    public ShotEntity(LivingEntity shooter, Level world, float damage) {
        super(Entityinit.Shotentity, shooter, world, damage);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityhit) {
        if (modifydamage(timer)) {
            super.onHitEntity(entityhit);
        } else {
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        timer++;
    }

    private boolean modifydamage(double distance) {
        double modifier = damagemodifier(distance);
        projectiledamage *= modifier;
        extradamage *= modifier;
        return modifier != 0;
    }

    private double damagemodifier(double x) {
        if (x == 0) x = 1;
        return Math.max(-0.3 * x + 1.3, 0);
    }
}
