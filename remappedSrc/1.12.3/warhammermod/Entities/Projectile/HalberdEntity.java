package warhammermod.Entities.Projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;


public class HalberdEntity extends ProjectileBase{
    int fuse=20;

    public HalberdEntity(EntityType<? extends HalberdEntity> entityType, World p_i50148_2_) {
        super(entityType, p_i50148_2_);
    }
    public HalberdEntity(World worldin, LivingEntity shooter, float damageIn, EntityType<? extends ProjectileBase> type,ItemStack stack) {
        super(type,worldin, shooter,new ItemStack(ItemsInit.iron_halberd),damageIn ,stack);
    }

    public HalberdEntity(LivingEntity shooter, World world, float damage, ItemStack stack) {
        this(world, shooter, damage, Entityinit.halberdthrust, stack);
    }
    public HalberdEntity(World world) {
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
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ItemsInit.iron_halberd);
    }
}
