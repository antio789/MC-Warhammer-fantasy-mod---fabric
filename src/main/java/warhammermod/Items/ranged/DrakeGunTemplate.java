package warhammermod.Items.ranged;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import warhammermod.Entities.Projectile.FlameEntity;
import warhammermod.Items.AutogunBase;
import warhammermod.utils.Registry.WHRegistry;

public class DrakeGunTemplate extends AutogunBase {

    public DrakeGunTemplate(Item.Properties properties, int MagSize, int time) {
        super(properties, Items.BLAZE_ROD, time, MagSize, 1, 4);
    }

    @Override
    public void fire(Player player, Level world, ItemStack stack) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), WHRegistry.flame, SoundSource.PLAYERS, 0.3F, 1.05F + (rand.nextFloat() + rand.nextFloat()) * 0.1F);

        if (!world.isClientSide()) {
            FlameEntity smallfireballentity = new FlameEntity(world, player, player.getLookAngle().x * 5 + rand.nextGaussian() * 0.1, player.getLookAngle().y * 5, player.getLookAngle().z * 5 + rand.nextGaussian() * 0.1);
            smallfireballentity.setPos(player.getX(), player.getEyeY() - 0.35F, player.getZ());
            world.addFreshEntity(smallfireballentity);
        }
    }
}
