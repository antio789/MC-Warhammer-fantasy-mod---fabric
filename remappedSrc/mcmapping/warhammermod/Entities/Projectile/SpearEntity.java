package warhammermod.Entities.Projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;

public class SpearEntity extends ProjectileBase {
    private ItemStack throwed_spear;
    public SpearEntity(EntityType<? extends SpearEntity> p_i50148_1_, Level p_i50148_2_) {
        super(p_i50148_1_, p_i50148_2_);
    }
    public SpearEntity(Level worldin, LivingEntity shooter, float damageIn, EntityType<? extends SpearEntity> type,ItemStack stack) {
        super(type,worldin, shooter,new ItemStack(ItemsInit.iron_spear),damageIn,stack);
    }

    public SpearEntity(LivingEntity shooter, Level world, float damage,ItemStack stack){
        this(world, shooter, damage, Entityinit.SpearProjectile,stack);
        throwed_spear =stack;
    }


    protected void onHitEntity(EntityHitResult entityHitResult){
        if(throwed_spear!=null) this.spawnAtLocation(throwed_spear);
        super.onHitEntity(entityHitResult);

    }

    protected void onHitBlock(BlockHitResult p_230299_1_){
        if(throwed_spear!=null)this.spawnAtLocation(throwed_spear);
        super.onHitBlock(p_230299_1_);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ItemsInit.iron_spear);
    }

}
