package warhammermod.Items.ranged;


import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import warhammermod.Entities.Projectile.ShotEntity;
import warhammermod.Items.GunBase;
import warhammermod.utils.ModEnchantmentHelper;


public class ShotgunTemplate extends GunBase {//copy crossbowspread, maybe we will see
    float damage;
    public ShotgunTemplate(net.minecraft.item.Item.Settings properties, Item ammotype, int time, int magsize, float damagein){
        super(properties, ammotype, time, magsize);
        damage=damagein;
    }

    public void fire(PlayerEntity player, World world, ItemStack stack) {
        playparticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,world,player);
        if(!world.isClient()) {
            player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, player.getSoundCategory(), 1.0f, 1.35F/(rand.nextFloat()*0.4F+1.2F)+0.5F);
            ShotEntity bullet = new ShotEntity(player, world, damage,player.getBlockPos(),stack,findAmmo(player));
            bullet.setPosition(player.getX(), player.getEyeY() - 0.26, player.getZ());
            bullet.setVelocity(player,player.getPitch(), player.getYaw(), 0, 3.5F, 0.3F);

            int i = ModEnchantmentHelper.getLevel(world,stack, Enchantments.POWER);
            if (i > 0) {
                bullet.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(world,stack,Enchantments.PUNCH) + 1;
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }

            world.spawnEntity(bullet);
            if (ModEnchantmentHelper.getLevel(world,stack,Enchantments.FLAME) > 0) {
                bullet.setOnFireFor(100);
            }
            stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
        }
    }
}
