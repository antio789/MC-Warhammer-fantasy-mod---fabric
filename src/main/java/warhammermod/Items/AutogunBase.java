package warhammermod.Items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.Random;

public class AutogunBase extends Item implements IReloadItem {
    public final int timetoreload;
    public final int Magsize;
    private final Item AmmoType;
    protected final Random rand = new Random();
    private final int FireRate;
    private final int AmmoConsumption;

    public AutogunBase(Item.Properties properties, Item ammotype, int time, int magsize, int ammoconsumption, int firerate) {
        super(properties);
        Magsize = magsize;
        timetoreload = time;
        AmmoType = ammotype;
        FireRate = firerate;
        AmmoConsumption = ammoconsumption;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isCharged(stack) || player.isCreative() || !findAmmo(player).isEmpty()) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    public ItemStack findAmmo(Player player) {
        if (this.isAmmo(player.getItemInHand(InteractionHand.OFF_HAND))) {
            return player.getItemInHand(InteractionHand.OFF_HAND);
        } else if (this.isAmmo(player.getItemInHand(InteractionHand.MAIN_HAND))) {
            return player.getItemInHand(InteractionHand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack = player.getInventory().getItem(i);
                if (this.isAmmo(itemstack)) {
                    return itemstack;
                }
            }
            return ItemStack.EMPTY;
        }
    }

    private boolean isAmmo(ItemStack stack) {
        return stack.getItem().equals(AmmoType);
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

    public int getFireRate() {
        return FireRate;
    }

    public void fire(Player player, Level world, ItemStack stack) {
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player && !world.isClientSide() && !player.isCreative()) {
            if (isCharged(stack)) {
                setCharge(stack, getCharge(stack) - (getUseDuration(stack, entity) - timeLeft) / getFireRate());
            } else {
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                ItemStack ammo = this.findAmmo(player);
                int ammoreserve = ammo.getCount();
                if (ammoreserve >= 0) {
                    ammo.shrink(Math.min(ammoreserve, AmmoConsumption));
                    setCharge(stack, (int) ((float) Magsize / AmmoConsumption * Math.min(ammoreserve, AmmoConsumption)));
                }
            }
        }
        return true;
    }

    public static boolean isCharged(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().getIntOr("ammo", 0) > 0;
    }

    public static int getCharge(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return 0;
        }
        return data.copyTag().getIntOr("ammo", 0);
    }

    public static void setCharge(ItemStack stack, int ammo) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("ammo", ammo));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    public int getTimetoreload() {
        return timetoreload;
    }

    @Override
    public boolean isReadytoFire(ItemStack stack) {
        return isCharged(stack);
    }
}
