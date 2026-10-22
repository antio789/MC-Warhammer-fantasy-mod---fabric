package warhammermod.Items.ranged;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import warhammermod.Entities.Projectile.StoneEntity;
import warhammermod.utils.reference;

import java.util.Random;

public class SlingTemplate extends BowItem {

    public SlingTemplate(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeleft) {
        if (!(entity instanceof Player playerentity)) {
            return false;
        }
        boolean shoulduseammo = playerentity.isCreative() || (world instanceof ServerLevel serverLevel && EnchantmentHelper.getItemEnchantmentLevel(reference.enchantment(serverLevel, Enchantments.INFINITY), stack) > 0);
        ItemStack itemstack = findAmmo(playerentity);
        if (!itemstack.isEmpty() || shoulduseammo) {
            if (itemstack.isEmpty()) {
                itemstack = new ItemStack(Items.COBBLESTONE);
            }

            int j = this.getUseDuration(stack, entity) - timeleft;
            float f = getPowerForTime(j);
            if (!((double) f < 0.1D)) {
                if (world instanceof ServerLevel serverLevel) {
                    StoneEntity stone = new StoneEntity(playerentity, world, 2.5F);
                    stone.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot(), 0.0F, f * 2.75F, 1.0F);
                    if (f == 1.0F) {
                        stone.setCritArrow(true);
                    }

                    int p = EnchantmentHelper.getItemEnchantmentLevel(reference.enchantment(serverLevel, Enchantments.POWER), stack);
                    if (p > 0) {
                        stone.setBaseDamage(2.0D + (double) j * 0.5D + 0.5D);
                    }

                    stack.hurtAndBreak(1, playerentity, net.minecraft.world.entity.EquipmentSlot.MAINHAND);

                    world.addFreshEntity(stone);
                }
                Random random = new Random();
                world.playSound(null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                if (!shoulduseammo) {
                    itemstack.shrink(1);
                    if (itemstack.isEmpty()) {
                        playerentity.getInventory().removeItem(itemstack);
                    }
                }
                playerentity.awardStat(Stats.ITEM_USED.get(this));
            }
        }
        return true;
    }

    public ItemStack findAmmo(Player player) {
        if (isAmmo(player.getItemInHand(InteractionHand.OFF_HAND))) {
            return player.getItemInHand(InteractionHand.OFF_HAND);
        } else if (isAmmo(player.getItemInHand(InteractionHand.MAIN_HAND))) {
            return player.getItemInHand(InteractionHand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack = player.getInventory().getItem(i);
                if (isAmmo(itemstack)) {
                    return itemstack;
                }
            }
            return ItemStack.EMPTY;
        }
    }

    private boolean isAmmo(ItemStack stack) {
        return stack.getItem().equals(Items.COBBLESTONE);
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean hasammo = !findAmmo(player).isEmpty();
        if (!player.isCreative() && !hasammo) {
            return InteractionResult.FAIL;
        }
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public java.util.function.Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem().equals(Items.COBBLESTONE);
    }
}
