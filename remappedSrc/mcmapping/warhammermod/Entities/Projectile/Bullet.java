package warhammermod.Entities.Projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;


public class Bullet extends  ProjectileBase{
    public Bullet(Level world){
        super(Entityinit.Bullet,world);
    }

    public Bullet(EntityType<? extends Bullet> p_i50148_1_, Level p_i50148_2_) {
        super(p_i50148_1_, p_i50148_2_);
    }

    public Bullet(Level worldin, LivingEntity shooter, float damageIn, EntityType<? extends ProjectileBase> type,ItemStack stack,ItemStack ammo) {
        super(type,worldin, shooter,stack,damageIn,ammo);
    }

    public Bullet(LivingEntity shooter, Level world, float damage,ItemStack stack, ItemStack ammo) {
        this(world, shooter, damage, Entityinit.Bullet,stack, ammo);
    }

    public void tick()
    {
        if (this.level().isClientSide())
        {
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
        super.tick();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ItemsInit.Cartridge);
    }

}
