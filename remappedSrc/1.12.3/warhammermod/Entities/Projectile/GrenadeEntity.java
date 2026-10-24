package warhammermod.Entities.Projectile;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;

public class GrenadeEntity extends ProjectileBase{



    public GrenadeEntity(EntityType<? extends GrenadeEntity> p_i50148_1_, World p_i50148_2_) {
        super(p_i50148_1_, p_i50148_2_);
    }

    public GrenadeEntity(World worldin, LivingEntity shooter, float damageIn, EntityType<? extends ProjectileBase> type, ItemStack stack, ItemStack ammo) {
        super(type,worldin, shooter,stack,damageIn,ammo);
    }
    public GrenadeEntity(LivingEntity shooter, World world, float damageIn, ItemStack stack,ItemStack ammo) {
        this(world,shooter, damageIn, Entityinit.Grenade, stack,ammo);
    }

    public GrenadeEntity(LivingEntity shooter, World world, ItemStack stack, ItemStack ammo) {
        this(world, shooter, 0, Entityinit.Grenade,stack,ammo);
    }

    protected void onBlockHit(BlockHitResult hitResult) {
        if (!this.getWorld().isClient()) {
            this.getWorld().createExplosion(null, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z, 2 + knocklevel, World.ExplosionSourceType.TNT);
            this.remove(RemovalReason.DISCARDED);

        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ItemsInit.Grenade);
    }

    protected void onEntityHit(EntityHitResult hitResult){
        this.getWorld().createExplosion(null, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z, 2 + knocklevel, World.ExplosionSourceType.TNT);
        this.remove(RemovalReason.DISCARDED);
    }



}
