package warhammermod.Items.ranged;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import warhammermod.Entities.Projectile.WarpBulletEntity;
import warhammermod.Items.AutogunBase;
import warhammermod.Items.ItemsInit;
import warhammermod.utils.reference;

public class RatlingGun extends AutogunBase {

    public RatlingGun(Item.Properties properties, int MagSize, int time) {
        super(properties, ItemsInit.Warpstone, time, MagSize, 64, 6);
    }

    @Override
    public void fire(Player player, Level world, ItemStack stack) {
        if (world instanceof ServerLevel serverLevel) {
            WarpBulletEntity bullet = new WarpBulletEntity(player, world, 11);
            bullet.setPos(player.getX(), player.getEyeY() - 0.26, player.getZ());
            bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 3.5F, 6F);

            int i = EnchantmentHelper.getItemEnchantmentLevel(reference.enchantment(serverLevel, Enchantments.POWER), stack);
            if (i > 0) {
                bullet.setpowerDamage(i);
            }
            int k = EnchantmentHelper.getItemEnchantmentLevel(reference.enchantment(serverLevel, Enchantments.PUNCH), stack) + 1;
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }

            world.addFreshEntity(bullet);
        }
        world.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.6F, 1.55F / (rand.nextFloat() * 0.4F + 1.2F) + 0.5F);
    }
}
