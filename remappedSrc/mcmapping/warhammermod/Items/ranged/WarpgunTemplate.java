package warhammermod.Items.ranged;


import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import warhammermod.Entities.Projectile.WarpBulletEntity;
import warhammermod.Items.GunBase;
import warhammermod.Items.IReloadItem;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.WHRegistry;

public class WarpgunTemplate extends GunBase implements IReloadItem {
    float damage;

    public WarpgunTemplate(Properties properties, Item ammotype, int time, int magsize, float damagein){
        super(properties, ammotype, time, magsize);
        damage=damagein;
    }

    public void fire(Player player, Level world, ItemStack stack) {
        if(world.isClientSide()) {
            for (int k = 0; k < 25; ++k) {
                world.addParticle(WHRegistry.WARP, player.getX() + player.getLookAngle().x * 2 + (double) (this.rand.nextFloat() * this.width*1.5) - (double) this.width, player.getY() + 0.4 + (double) (this.rand.nextFloat() * this.height), player.getZ() + player.getLookAngle().z * 2 + (double) (this.rand.nextFloat() * this.width*1.5) - (double) this.width, 0, 0, 0);
            }
        }
        if(!world.isClientSide()) {
            world.playSound(null,player.getX(),player.getY(),player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS,1F,1.35F/(rand.nextFloat()*0.4F+1.2F)+0.5F);
            WarpBulletEntity bullet = new WarpBulletEntity(player,world, damage,stack,findAmmo(player));
            bullet.setPos(player.getX(), player.getEyeY() - 0.26, player.getZ());
            bullet.shootFromRotation(player,player.getXRot(), player.getYRot(), 1, 4F, 0.1F);

            int i = ModEnchantmentHelper.getLevel(world,stack, Enchantments.POWER);
            if (i > 0) {
                bullet.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(world,stack, Enchantments.PUNCH) + 1;
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }

            world.addFreshEntity(bullet);
            if (ModEnchantmentHelper.getLevel(world,stack, Enchantments.FLAME) > 0) {
                bullet.igniteForSeconds(100);
            }
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        }
    }


}
