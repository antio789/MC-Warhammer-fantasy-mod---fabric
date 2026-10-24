package warhammermod.Items.ranged;


import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import warhammermod.Client.Render.Item.RenderRatlingGun;
import warhammermod.Entities.Projectile.WarpBulletEntity;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.AutogunBase;
import warhammermod.Items.firecomponent;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;


public class RatlingGun extends AutogunBase { //BOW enchantements

    public RatlingGun(net.minecraft.item.Item.Settings properties, int MagSize, int time) {
        super(properties.component(WHRegistry.Fireorder, firecomponent.DEFAULT), ItemsInit.Warpstone,time,MagSize,64, 6);
    }


    public void fire(PlayerEntity player, World world, ItemStack stack){
        if(!world.isClient()) {
            WarpBulletEntity bullet = new WarpBulletEntity(player,world, 11,stack,findAmmo(player));
            bullet.setPosition(player.getX(), player.getEyeY() - 0.26, player.getZ());
            bullet.setVelocity(player,player.getPitch(), player.getYaw(), 0, 3.5F, 6F);

            int i = ModEnchantmentHelper.getLevel(world, stack, Enchantments.POWER);
            if (i > 0) {
                bullet.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(world, stack, Enchantments.PUNCH) + 1;
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }

            world.spawnEntity(bullet);
        }

        else if(world.isClient()){
            RenderRatlingGun.playerfired(player);
            int i=stack.getOrDefault(WHRegistry.Fireorder,firecomponent.DEFAULT).firecount();
            if(i>6)i=0;
            stack.set(WHRegistry.Fireorder,new firecomponent(i+1));
            for (int k = 0; k < 15; ++k) {
                world.addParticle(WHRegistry.WARP, player.getX() + player.getRotationVector().x * 2 + (double) (this.rand.nextFloat() * this.width) - (double) this.width, player.getY() + 0.4 + (double) (this.rand.nextFloat() * this.height), player.getZ() + player.getRotationVector().z * 2 + (double) (this.rand.nextFloat() * this.width) - (double) this.width, 0, 0, 0);
            }
        }
        PlayaSound(world, player);
    }

    public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {//implement infinity if possible
        stack.set(WHRegistry.Fireorder,new firecomponent(firecomponent.DEFAULT.firecount()));
        super.onStoppedUsing(stack, worldIn, entityLiving, timeLeft);
    }

        public void PlayaSound(World world, PlayerEntity player){
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, player.getSoundCategory(), 0.6f, 1.55F/(rand.nextFloat()*0.4F+1.2F)+0.5F);
    }
}
