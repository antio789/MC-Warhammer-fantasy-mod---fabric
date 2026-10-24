package warhammermod.Entities.Projectile;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;

public class GrenadeEntity extends ProjectileBase{



    public GrenadeEntity(EntityType<? extends GrenadeEntity> p_i50148_1_, Level p_i50148_2_) {
        super(p_i50148_1_, p_i50148_2_);
    }

    public GrenadeEntity(Level worldin, LivingEntity shooter, float damageIn, EntityType<? extends ProjectileBase> type, ItemStack stack, ItemStack ammo) {
        super(type,worldin, shooter,stack,damageIn,ammo);
    }
    public GrenadeEntity(LivingEntity shooter, Level world, float damageIn, ItemStack stack,ItemStack ammo) {
        this(world,shooter, damageIn, Entityinit.Grenade, stack,ammo);
    }

    public GrenadeEntity(LivingEntity shooter, Level world, ItemStack stack, ItemStack ammo) {
        this(world, shooter, 0, Entityinit.Grenade,stack,ammo);
    }

    protected void onHitBlock(BlockHitResult hitResult) {
        if (!this.level().isClientSide()) {
            this.level().explode(null, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 2 + knocklevel, Level.ExplosionInteraction.TNT);
            this.remove(RemovalReason.DISCARDED);

        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ItemsInit.Grenade);
    }

    protected void onHitEntity(EntityHitResult hitResult){
        this.level().explode(null, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 2 + knocklevel, Level.ExplosionInteraction.TNT);
        this.remove(RemovalReason.DISCARDED);
    }



}
