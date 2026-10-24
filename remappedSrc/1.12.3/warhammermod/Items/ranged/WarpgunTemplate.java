package warhammermod.Items.ranged;


import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import warhammermod.Entities.Projectile.WarpBulletEntity;
import warhammermod.Items.GunBase;
import warhammermod.Items.IReloadItem;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.WHRegistry;

public class WarpgunTemplate extends GunBase implements IReloadItem {
    float damage;

    public WarpgunTemplate(net.minecraft.item.Item.Settings properties, Item ammotype, int time, int magsize, float damagein){
        super(properties, ammotype, time, magsize);
        damage=damagein;
    }

    public void fire(PlayerEntity player, World world, ItemStack stack) {
        if(world.isClient()) {
            for (int k = 0; k < 25; ++k) {
                world.addParticle(WHRegistry.WARP, player.getX() + player.getRotationVector().x * 2 + (double) (this.rand.nextFloat() * this.width*1.5) - (double) this.width, player.getY() + 0.4 + (double) (this.rand.nextFloat() * this.height), player.getZ() + player.getRotationVector().z * 2 + (double) (this.rand.nextFloat() * this.width*1.5) - (double) this.width, 0, 0, 0);
            }
        }
        if(!world.isClient()) {
            world.playSound(null,player.getX(),player.getY(),player.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS,1F,1.35F/(rand.nextFloat()*0.4F+1.2F)+0.5F);
            WarpBulletEntity bullet = new WarpBulletEntity(player,world, damage,stack,findAmmo(player));
            bullet.setPosition(player.getX(), player.getEyeY() - 0.26, player.getZ());
            bullet.setVelocity(player,player.getPitch(), player.getYaw(), 1, 4F, 0.1F);

            int i = ModEnchantmentHelper.getLevel(world,stack, Enchantments.POWER);
            if (i > 0) {
                bullet.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(world,stack, Enchantments.PUNCH) + 1;
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }

            world.spawnEntity(bullet);
            if (ModEnchantmentHelper.getLevel(world,stack, Enchantments.FLAME) > 0) {
                bullet.setOnFireFor(100);
            }
            stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
        }
    }


}
