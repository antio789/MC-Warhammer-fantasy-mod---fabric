package warhammermod.Items.ranged;

import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import warhammermod.Entities.Projectile.Bullet;
import warhammermod.Items.GunBase;
import warhammermod.Items.IReloadItem;
import warhammermod.Items.ItemsInit;
import warhammermod.Items.melee.HammerTemplate;
import warhammermod.Items.melee.ShieldTemplate;
import warhammermod.utils.reference;

import java.util.Random;

public class GunSwordTemplate extends Item implements IReloadItem {
    float damage;
    public final int timetoreload;
    public final int Magsize;
    protected final Random rand = new Random();
    public float width = 1.5F;
    public float height = 1.5F;

    public GunSwordTemplate(ToolMaterial material, Item.Properties properties, float meleedamage, float attackspeed, int time, int magsize, float damagein) {
        super(material.applySwordProperties(properties, meleedamage, attackspeed).attributes(HammerTemplate.createAttributes(meleedamage, attackspeed)));
        damage = damagein;
        timetoreload = time;
        Magsize = magsize;
    }

    @Override
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
        return stack.getItem().equals(ItemsInit.Cartridge);
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
        if ((count == getUseDuration(stack, player) - timetoreload) && !isCharged(stack) && !((Player) player).isCreative() && !world.isClientSide()) {
            world.playSound(null, player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1, 1);
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
                ItemStack ammo = findAmmo(player);
                int ammoreserve = ammo.getCount();
                if (ammoreserve < Magsize) {
                    ammo.shrink(ammoreserve);
                    setCharge(stack, ammoreserve);
                } else {
                    ammo.shrink(Magsize);
                    setCharge(stack, Magsize);
                }
            }
        }
        return true;
    }

    public void fire(Player player, Level world, ItemStack stack) {
        if (world instanceof ServerLevel serverLevel) {
            world.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1, 1.35F / (rand.nextFloat() * 0.4F + 1.2F) + 0.5F);

            Bullet bullet = new Bullet(player, world, damage);
            bullet.setPos(player.getX(), player.getEyeY() - 0.26, player.getZ());
            bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 3.5F, 0.3F);

            int i = EnchantmentHelper.getItemEnchantmentLevel(reference.enchantment(serverLevel, Enchantments.POWER), stack);
            if (i > 0) {
                bullet.setpowerDamage(i);
            }
            int k = EnchantmentHelper.getItemEnchantmentLevel(reference.enchantment(serverLevel, Enchantments.PUNCH), stack) + 1;
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }

            world.addFreshEntity(bullet);
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
    }

    public static boolean isCharged(ItemStack stack) {
        return GunBase.isCharged(stack);
    }

    public static int getCharge(ItemStack stack) {
        return GunBase.getCharge(stack);
    }

    public static void setCharge(ItemStack stack, int ammo) {
        GunBase.setCharge(stack, ammo);
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
    public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player player) {
            hasshieldcache = hasshield(player);
        }
    }
}
