package warhammermod.Items.ranged;


import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import warhammermod.Enchantements.ModEnchantements;
import warhammermod.Entities.Projectile.GrenadeEntity;
import warhammermod.Items.GunBase;
import warhammermod.utils.ModEnchantmentHelper;


public class GrenadeTemplate extends GunBase { // need power punch, unbreaking mending


    public GrenadeTemplate(net.minecraft.item.Item.Settings properties, Item ammotype, int time, int magsize){
        super(properties, ammotype, time, magsize);
    }
    public void fire(PlayerEntity player, World world, ItemStack stack){
        playparticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,world,player);
        if(!world.isClient()) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0F, 1.35F / (rand.nextFloat() * 0.4F + 1.2F) + 0.5F);

            GrenadeEntity bullet = new GrenadeEntity(player, world,stack,findAmmo(player));
            bullet.setPosition(player.getX(),player.getEyeY()-0.26,player.getZ());

            int i = ModEnchantmentHelper.getLevel(world, stack, ModEnchantements.RANGE);
            bullet.setVelocity(player,player.getPitch(), player.getYaw(), 0, 0.7F + 0.13F * i, 0.6F);

            int k = ModEnchantmentHelper.getLevel(world, stack, ModEnchantements.RADIUS);
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }

            world.spawnEntity(bullet);

            stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
        }
    }
}
