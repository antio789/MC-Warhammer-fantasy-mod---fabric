package warhammermod.Items.ranged;

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
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import warhammermod.Entities.Projectile.StoneEntity;
import warhammermod.Items.firecomponent;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.WHRegistry;

import java.util.Random;

public class SlingTemplate extends BowItem {

    public SlingTemplate(Item.Properties settings) {
        super(settings.component(WHRegistry.Fireorder, firecomponent.DEFAULT));
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
        return stack.getItem().equals(Items.COBBLESTONE);
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeleft) {
        if (entity instanceof Player player) {
            boolean shoulduseammo = player.isCreative() || ModEnchantmentHelper.getLevel(world, stack, Enchantments.INFINITY) > 0;
            ItemStack itemstack = findAmmo(player);
            if (!itemstack.isEmpty() || shoulduseammo) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.COBBLESTONE);
                }

                int j = this.getUseDuration(stack, player) - timeleft;
                float f = getPowerForTime(j);
                if (!((double) f < 0.1D)) {
                    if (!world.isClientSide()) {
                        StoneEntity stone = new StoneEntity(player, world, 1F, stack, findAmmo(player));
                        stone.setPos(player.getX(), player.getEyeY() - 0.26, player.getZ());
                        stone.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 2.5F, 1.0F);
                        if (f == 1.0F) {
                            stone.setCritArrow(true);
                        }

                        int p = ModEnchantmentHelper.getLevel(world, stack, Enchantments.POWER);
                        if (p > 0) {
                            stone.setTotaldamage(stone.getTotalDamage() + (float) j * 0.5F + 0.5F);
                        }

                        int k = ModEnchantmentHelper.getLevel(world, stack, Enchantments.PUNCH);
                        if (k > 0) {
                            stone.setknockbacklevel(k);
                        }

                        if (ModEnchantmentHelper.getLevel(world, stack, Enchantments.FLAME) > 0) {
                            stone.igniteForSeconds(100);
                        }

                        stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                        world.addFreshEntity(stone);
                    }
                    Random random = new Random();
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!shoulduseammo) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeItem(itemstack);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        boolean hasammo = !findAmmo(player).isEmpty();

        if (!player.isCreative() && !hasammo) {
            return InteractionResult.FAIL;
        } else {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
    }
}
