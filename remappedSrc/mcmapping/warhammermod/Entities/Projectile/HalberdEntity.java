package warhammermod.Entities.Projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;


public class HalberdEntity extends ProjectileBase{
    int fuse=20;

    public HalberdEntity(EntityType<? extends HalberdEntity> entityType, Level p_i50148_2_) {
        super(entityType, p_i50148_2_);
    }
    public HalberdEntity(Level worldin, LivingEntity shooter, float damageIn, EntityType<? extends ProjectileBase> type,ItemStack stack) {
        super(type,worldin, shooter,new ItemStack(ItemsInit.iron_halberd),damageIn ,stack);
    }

    public HalberdEntity(LivingEntity shooter, Level world, float damage, ItemStack stack) {
        this(world, shooter, damage, Entityinit.halberdthrust, stack);
    }
    public HalberdEntity(Level world) {
        super(Entityinit.halberdthrust, world);
    }


    /*
        @OnlyIn(Dist.CLIENT)
        public boolean shouldRenderAtSqrDistance(double p_70112_1_) { return false; }
    */
    public void tick()
    {
        super.tick();
        fuse--;
        if(fuse==18){
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ItemsInit.iron_halberd);
    }
}
