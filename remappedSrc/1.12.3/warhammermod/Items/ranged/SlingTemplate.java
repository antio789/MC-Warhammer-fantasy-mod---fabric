package warhammermod.Items.ranged;


import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import warhammermod.Entities.Projectile.StoneEntity;
import warhammermod.Items.firecomponent;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.WHRegistry;

import java.util.Random;


public class SlingTemplate extends BowItem {

    private final double baseDamage = 1.0D;

    public SlingTemplate(net.minecraft.item.Item.Settings settings) {
        super(settings.component(WHRegistry.Fireorder, firecomponent.DEFAULT));
    }




    public ItemStack findAmmo(PlayerEntity player) {
        if (this.isAmmo(player.getStackInHand(Hand.OFF_HAND))) {
            return player.getStackInHand(Hand.OFF_HAND);
        } else if (this.isAmmo(player.getStackInHand(Hand.MAIN_HAND))) {
            return player.getStackInHand(Hand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.getInventory().size(); ++i) {
                ItemStack itemstack = player.getInventory().getStack(i);
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

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeleft) {
        if (entity instanceof PlayerEntity player) {
            boolean shoulduseammo = player.getAbilities().creativeMode || ModEnchantmentHelper.getLevel(world, stack, Enchantments.INFINITY) > 0;
            ItemStack itemstack = findAmmo(player);
            if (!itemstack.isEmpty() || shoulduseammo) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.COBBLESTONE);
                }


                int j = this.getMaxUseTime(stack, player) - timeleft;
                float f = getPullProgress(j);
                if (!((double)f < 0.1D)) {

                    if (!world.isClient) {
                        //check damage is coherent with bow, bow base is 2
                        StoneEntity stone = new StoneEntity(player,world,1F,stack,findAmmo(player));
                        stone.setPosition(player.getX(),player.getEyeY()-0.26,player.getZ());
                        stone.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, f * 2.5F, 1.0F);
                        if (f == 1.0F) {
                            stone.setCritical(true);
                        }

                        int p = ModEnchantmentHelper.getLevel(world, stack,Enchantments.POWER);
                        if (p > 0) {
                            stone.setDamage(stone.getDamage() + (double)j * 0.5D + 0.5D);
                        }

                        int k = ModEnchantmentHelper.getLevel(world, stack,Enchantments.PUNCH);
                        if (k > 0) {
                            stone.setknockbacklevel(k);
                        }

                        if (ModEnchantmentHelper.getLevel(world, stack,Enchantments.FLAME) > 0) {
                            stone.setOnFireFor(100);
                        }

                        stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
                        world.spawnEntity(stone);
                    }
                    Random random = new Random();
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!shoulduseammo) {
                        itemstack.decrement(1);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeOne(itemstack);
                        }
                    }

                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                }
            }
        }
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerIn, Hand hand) {
        ItemStack itemstack = playerIn.getStackInHand(hand);
        boolean hasammo = !findAmmo(playerIn).isEmpty();

        if (!playerIn.getAbilities().creativeMode && !hasammo) {
            return TypedActionResult.fail(itemstack);
        } else {
            playerIn.setCurrentHand(hand);
            return TypedActionResult.consume(itemstack);
        }
    }





}
