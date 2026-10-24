package warhammermod.Entities.Projectile;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;

public class WarpBulletEntity extends ProjectileBase {

    public WarpBulletEntity(World world){
        super(Entityinit.WarpBullet,world);
    }
    public WarpBulletEntity(EntityType<? extends WarpBulletEntity> p_i50148_1_, World p_i50148_2_) {
        super(p_i50148_1_, p_i50148_2_);
    }
    public WarpBulletEntity(World worldin, LivingEntity shooter, float damageIn, EntityType<? extends ProjectileBase> type,ItemStack stack,ItemStack ammo) {
        super(type,worldin, shooter,stack,damageIn,ammo);
    }
    public WarpBulletEntity( LivingEntity shooter,World world, float damageIn,ItemStack stack) {
        this(world, shooter, damageIn, Entityinit.WarpBullet,stack,new ItemStack(ItemsInit.Warpstone));

    }
    public WarpBulletEntity( LivingEntity shooter,World world, float damageIn,ItemStack stack,ItemStack ammo) {
        this(world, shooter, damageIn, Entityinit.WarpBullet,stack,ammo);

    }






    int delayedtick = 0;
    public void tick()
    {
        super.tick();

        if (delayedtick<=0 && this.getWorld().isClient() && !this.inGround)
        {
            delayedtick=3;
            spawnColoredParticles();
        }
        delayedtick--;
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ItemsInit.Warpstone);
    }


    @Override
    public void applyspecialeffect(LivingEntity entity) {
        if(random.nextFloat()<0.3){
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 60, 1));
        }
    }

    private void spawnColoredParticles()
    {
        int i = 65280;
            double d0 = (double)(0) / 255.0D;
            double d1 = (double)(i >> 8 & 255) / 255.0D;
            double d2 = (double)(0) / 255.0D;

            for (int j = 0; j < 5; ++j)
            {
                this.getWorld().addParticle(WHRegistry.WARP, this.getX() + (this.random.nextDouble() - 0.5D) * (double)this.getWidth(), this.getY() +this.random.nextDouble()*(double)this.getHeight(), this.getZ() + (this.random.nextDouble() - 0.5D) * (double)this.getWidth(), d0, d1, d2);
            }
    }

}
