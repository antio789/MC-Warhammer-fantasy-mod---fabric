package warhammermod.Items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;

public abstract class AutogunBase extends GunBase implements IReloadItem {
    protected final Random rand = new Random();
    private final int FireRate;
    private final int AmmoConsumption;

    public AutogunBase(Item.Properties properties, Item ammotype, int time, int magsize, int ammoconsumption, int firerate) {
        super(properties, ammotype, time, magsize);
        AmmoConsumption = ammoconsumption;
        FireRate = firerate;
    }

    @Override
    public void onUseTick(Level world, LivingEntity player, ItemStack stack, int count) {
        if (player instanceof Player entityplayer) {
            if ((isCharged(stack) && (getUseDuration(stack, player) - count) / getFireRate() <= getCharge(stack)) || entityplayer.isCreative()) {
                if ((getUseDuration(stack, player) - count) != 0 && (getUseDuration(stack, player) - count) % getFireRate() == 0) {
                    fire(entityplayer, world, stack);
                }
            } else if ((count == getUseDuration(stack, player) - timetoreload) && !isCharged(stack)) {
                world.playSound(null, player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1, 1);
            }
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player && !world.isClientSide() && !player.isCreative()) {
            if (isCharged(stack)) {
                setCharge(stack, getCharge(stack) - (getUseDuration(stack, entityLiving) - timeLeft) / getFireRate());
                return true;
            } else {
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                int ammoreserve = this.findAmmo(player).getCount();
                if ((ammoreserve >= 0)) {
                    this.findAmmo(player).shrink(Math.min(ammoreserve, AmmoConsumption));
                    setCharge(stack, (int) ((float) Magsize / AmmoConsumption * Math.min(ammoreserve, AmmoConsumption)));
                    return true;
                }
            }
        }
        return false;
    }

    public int getFireRate() {
        return FireRate;
    }

    @Override
    public abstract void fire(Player player, Level world, ItemStack stack);
}
