package warhammermod.Entities.Projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.Items.ItemsInit;

public class GrenadeEntity extends ProjectileBase implements net.minecraft.world.entity.projectile.ItemSupplier {

    public GrenadeEntity(EntityType<? extends GrenadeEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(ItemsInit.Grenade);
    }

    public GrenadeEntity(Level worldin, LivingEntity shooter, float damageIn, EntityType<? extends ProjectileBase> type, ItemStack stack, ItemStack ammo) {
        super(type, worldin, shooter, ammo, damageIn, stack);
    }

    public GrenadeEntity(LivingEntity shooter, Level world, float damageIn, ItemStack stack, ItemStack ammo) {
        this(world, shooter, damageIn, Entityinit.Grenade, stack, ammo);
    }

    public GrenadeEntity(LivingEntity shooter, Level world, ItemStack stack, ItemStack ammo) {
        this(world, shooter, 0, Entityinit.Grenade, stack, ammo);
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        if (!this.level().isClientSide()) {
            this.level().explode(null, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 2 + knocklevel, Level.ExplosionInteraction.TNT);
            this.discard();
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ItemsInit.Grenade);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        this.level().explode(null, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 2 + knocklevel, Level.ExplosionInteraction.TNT);
        this.discard();
    }
}
