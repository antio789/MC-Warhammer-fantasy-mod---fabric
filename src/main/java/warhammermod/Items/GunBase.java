package warhammermod.Items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import warhammermod.Items.melee.ShieldTemplate;

import java.util.Random;

public class GunBase extends Item implements IReloadItem {
    public final int timetoreload;
    public final int Magsize;
    private final Item AmmoType;
    protected final Random rand = new Random();
    public float width = 1.5F;
    public float height = 1.5F;

    public GunBase(Item.Properties properties, Item ammotype, int time, int magsize) {
        super(properties);
        Magsize = magsize;
        timetoreload = time;
        AmmoType = ammotype;
    }

    public boolean isReadytoFire(ItemStack stack) {
        return isCharged(stack);
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
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isCharged(stack) || player.isCreative() || !findAmmo(player).isEmpty()) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onUseTick(Level world, LivingEntity player, ItemStack stack, int count) {
        if (player instanceof Player) {
            if ((count == getUseDuration(stack, player) - timetoreload) && !isCharged(stack) && !((Player) player).isCreative() && !world.isClientSide()) {
                world.playSound(null, player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1, 1);
            }
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            if (isCharged(stack) || player.isCreative()) {
                fire(player, world, stack);
                if (!player.isCreative()) {
                    setCharge(stack, getCharge(stack) - 1);
                }
            } else if (timetoreload <= getUseDuration(stack, entityLiving) - timeLeft && !world.isClientSide()) {
                ItemStack ammo = this.findAmmo(player);
                int ammoreserve = ammo.getCount();
                if (ammoreserve < Magsize) {
                    if (!player.isCreative()) {
                        ammo.shrink(ammoreserve);
                    }
                    setCharge(stack, ammoreserve);
                } else {
                    if (!player.isCreative()) {
                        ammo.shrink(Magsize);
                    }
                    setCharge(stack, Magsize);
                }
            }
        }
        return true;
    }

    public void fire(Player player, Level world, ItemStack stack) {
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
        return (isCharged(stack) && hasshieldcache) ? ItemUseAnimation.BLOCK : ItemUseAnimation.BOW;
    }

    private boolean hasshieldcache = false;

    public boolean hasshield(Player player) {
        return player.getOffhandItem().getItem() instanceof ShieldTemplate;
    }

    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player player) {
            hasshieldcache = hasshield(player);
        }
    }

    public int getTimetoreload() {
        return timetoreload;
    }
}
