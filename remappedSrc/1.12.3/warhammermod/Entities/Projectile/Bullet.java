package warhammermod.Entities.Projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;


public class Bullet extends  ProjectileBase{
    public Bullet(World world){
        super(Entityinit.Bullet,world);
    }

    public Bullet(EntityType<? extends Bullet> p_i50148_1_, World p_i50148_2_) {
        super(p_i50148_1_, p_i50148_2_);
    }

    public Bullet(World worldin, LivingEntity shooter, float damageIn, EntityType<? extends ProjectileBase> type,ItemStack stack,ItemStack ammo) {
        super(type,worldin, shooter,stack,damageIn,ammo);
    }

    public Bullet(LivingEntity shooter, World world, float damage,ItemStack stack, ItemStack ammo) {
        this(world, shooter, damage, Entityinit.Bullet,stack, ammo);
    }

    public void tick()
    {
        if (this.getWorld().isClient())
        {
            this.getWorld().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
        super.tick();
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ItemsInit.Cartridge);
    }

}
