package warhammermod.Entities.Projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import warhammermod.utils.Registry.Entityinit;

public class GrenadeEntity extends ProjectileBase implements net.minecraft.world.entity.projectile.ItemSupplier {
    public net.minecraft.world.item.ItemStack getItem() {
        return warhammermod.Items.ItemsInit.Grenade.getDefaultInstance();
    }
    public GrenadeEntity(EntityType<? extends GrenadeEntity> type, Level world) {
        super(type, world);
    }

    public GrenadeEntity(LivingEntity shooter, Level world) {
        super(Entityinit.Grenade, shooter, world, 0);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide()) {
            this.level().explode(this, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 2 + knocklevel, Level.ExplosionInteraction.TNT);
            discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
    }
}
