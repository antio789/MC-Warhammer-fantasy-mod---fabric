package warhammermod.Items.ranged;


import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import warhammermod.Client.Render.Item.RenderRatlingGun;
import warhammermod.Entities.Projectile.WarpBulletEntity;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.AutogunBase;
import warhammermod.Items.firecomponent;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;


public class RatlingGun extends AutogunBase { //BOW enchantements

    public RatlingGun(Properties properties, int MagSize, int time) {
        super(properties.component(WHRegistry.Fireorder, firecomponent.DEFAULT), ItemsInit.Warpstone,time,MagSize,64, 6);
    }


    public void fire(Player player, Level world, ItemStack stack){
        if(!world.isClientSide()) {
            WarpBulletEntity bullet = new WarpBulletEntity(player,world, 11,stack,findAmmo(player));
            bullet.setPos(player.getX(), player.getEyeY() - 0.26, player.getZ());
            bullet.shootFromRotation(player,player.getXRot(), player.getYRot(), 0, 3.5F, 6F);

            int i = ModEnchantmentHelper.getLevel(world, stack, Enchantments.POWER);
            if (i > 0) {
                bullet.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(world, stack, Enchantments.PUNCH) + 1;
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }

            world.addFreshEntity(bullet);
        }

        else if(world.isClientSide()){
            RenderRatlingGun.playerfired(player);
            int i=stack.getOrDefault(WHRegistry.Fireorder,firecomponent.DEFAULT).firecount();
            if(i>6)i=0;
            stack.set(WHRegistry.Fireorder,new firecomponent(i+1));
            for (int k = 0; k < 15; ++k) {
                world.addParticle(WHRegistry.WARP, player.getX() + player.getLookAngle().x * 2 + (double) (this.rand.nextFloat() * this.width) - (double) this.width, player.getY() + 0.4 + (double) (this.rand.nextFloat() * this.height), player.getZ() + player.getLookAngle().z * 2 + (double) (this.rand.nextFloat() * this.width) - (double) this.width, 0, 0, 0);
            }
        }
        PlayaSound(world, player);
    }

    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {//implement infinity if possible
        stack.set(WHRegistry.Fireorder,new firecomponent(firecomponent.DEFAULT.firecount()));
        super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
    }

        public void PlayaSound(Level world, Player player){
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, player.getSoundSource(), 0.6f, 1.55F/(rand.nextFloat()*0.4F+1.2F)+0.5F);
    }
}
