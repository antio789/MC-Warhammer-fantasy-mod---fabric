package warhammermod.Items.ranged;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import warhammermod.Entities.Projectile.Bullet;
import warhammermod.Items.GunBase;
import warhammermod.utils.ModEnchantmentHelper;


public class GunTemplate extends GunBase {
    float damage;

    public GunTemplate(Properties properties, Item ammotype, int time, int magsize, float damagein){
        super(properties, ammotype, time, magsize);
        damage=damagein;
    }

    public void fire(Player player, Level world, ItemStack stack) {
        playparticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,world,player);
        if(!world.isClientSide()) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, player.getSoundSource(), 1.0f, 1.35F/(rand.nextFloat()*0.4F+1.2F)+0.5F);
            Bullet bullet = new Bullet(player, world,damage,stack,findAmmo(player));
            bullet.setPos(player.getX(), player.getEyeY() - 0.26, player.getZ());
            bullet.shootFromRotation(player,player.getXRot(), player.getYRot(), 0, 3.5F, 0.3F);

            int i = ModEnchantmentHelper.getLevel(world, stack, Enchantments.POWER);
            if (i > 0) {
                bullet.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(world, stack, Enchantments.PUNCH) + 1;
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }
            world.addFreshEntity(bullet);
            if (ModEnchantmentHelper.getLevel(world, stack, Enchantments.FLAME) > 0) {
                bullet.igniteForSeconds(100);
            }
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        }
    }

}
