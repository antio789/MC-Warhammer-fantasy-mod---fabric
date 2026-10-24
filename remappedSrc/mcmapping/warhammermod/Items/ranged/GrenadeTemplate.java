package warhammermod.Items.ranged;


import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import warhammermod.Enchantements.ModEnchantements;
import warhammermod.Entities.Projectile.GrenadeEntity;
import warhammermod.Items.GunBase;
import warhammermod.utils.ModEnchantmentHelper;


public class GrenadeTemplate extends GunBase { // need power punch, unbreaking mending


    public GrenadeTemplate(Properties properties, Item ammotype, int time, int magsize){
        super(properties, ammotype, time, magsize);
    }
    public void fire(Player player, Level world, ItemStack stack){
        playparticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,world,player);
        if(!world.isClientSide()) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 1.35F / (rand.nextFloat() * 0.4F + 1.2F) + 0.5F);

            GrenadeEntity bullet = new GrenadeEntity(player, world,stack,findAmmo(player));
            bullet.setPos(player.getX(),player.getEyeY()-0.26,player.getZ());

            int i = ModEnchantmentHelper.getLevel(world, stack, ModEnchantements.RANGE);
            bullet.shootFromRotation(player,player.getXRot(), player.getYRot(), 0, 0.7F + 0.13F * i, 0.6F);

            int k = ModEnchantmentHelper.getLevel(world, stack, ModEnchantements.RADIUS);
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }

            world.addFreshEntity(bullet);

            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        }
    }
}
