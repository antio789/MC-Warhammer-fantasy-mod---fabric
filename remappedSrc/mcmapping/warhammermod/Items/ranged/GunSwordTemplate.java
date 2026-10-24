package warhammermod.Items.ranged;


import warhammermod.Entities.Projectile.Bullet;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.IReloadItem;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;

import java.util.Random;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;


public class GunSwordTemplate extends SwordItem implements IReloadItem { //needs sword and bow enchantements
    float damage;
    public int timetoreload;
    public int Magsize;
    protected Random rand= new Random();
    public boolean hasshield=false; //use player.getitemoffhand instead
    public float width = 1.5F;
    public float height = 1.5F;



    public GunSwordTemplate(Tier tier, int time, int magsize, float damagein){
        super(tier,(new Properties().attributes(GunSwordTemplate.createAttributes(tier,3,-2.4F))));
        damage=damagein;
        timetoreload=time;
        Magsize=magsize;
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


    public InteractionResultHolder<ItemStack> use(Level world, Player playerIn, InteractionHand hand) {
        ItemStack stack = playerIn.getItemInHand(hand);
        if(isCharged(stack) || playerIn.isCreative()|| !findAmmo(playerIn).isEmpty()){
            playerIn.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }


    @Override
    public void onUseTick(Level world, LivingEntity player, ItemStack stack, int count) {
        if (player instanceof Player){
            if ((count == getMaxUseTime() - timetoreload) && !isCharged(stack) && !((Player) player).isCreative() && !world.isClientSide()) {
                world.playSound(null,player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,1,1);
            }
        }
    }


    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            if (isCharged(stack) || player.isCreative()) {
                fire(player,worldIn,stack);
                if(!player.isCreative())setCharge(stack,getCharge(stack)-1);
            }
            else if(timetoreload<=getMaxUseTime()-timeLeft && !worldIn.isClientSide()) {
                int ammoreserve = this.findAmmo(player).getCount();
                int infinitylevel = ModEnchantmentHelper.getLevel(worldIn, stack, Enchantments.INFINITY);
                if (ammoreserve < Magsize) {
                    if (infinitylevel == 0) {
                        this.findAmmo(player).shrink(ammoreserve);
                    }
                    setCharge(stack,ammoreserve);
                } else {
                    if (infinitylevel == 0) {
                        this.findAmmo(player).shrink(Magsize);
                    }
                    setCharge(stack,Magsize);
                }
            }
        }
    }
    public boolean isReadytoFire(ItemStack stack){
        return isCharged(stack);
    }
/*
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        if(entity instanceof PlayerEntity)  setshield((PlayerEntity) entity);
    }
*/
    public void fire(Player player, Level world, ItemStack stack) {
        if(world.isClientSide()) {
            for (int k = 0; k < 30; ++k) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, player.getX() + player.getLookAngle().x * 2 + (double) (this.rand.nextFloat() * this.width * 2) - (double) this.width, player.getY() + 0.4 + (double) (this.rand.nextFloat() * this.height), player.getZ() + player.getLookAngle().z * 2 + (double) (this.rand.nextFloat() * this.width * 2) - (double) this.width, d2, d0, d1);
            }
        }
        if(!world.isClientSide()) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, player.getSoundSource(), 1.0f, 1.35F/(rand.nextFloat()*0.4F+1.2F)+0.5F);
            Bullet bullet = new Bullet(player, world, damage,stack,findAmmo(player));
            bullet.setPos(player.getX(), player.getEyeY() - 0.26, player.getZ());
            bullet.shootFromRotation(player,player.getXRot(), player.getYRot(), 0, 3.5F, 0.3F);

            int i = ModEnchantmentHelper.getLevel(world, stack, Enchantments.POWER);
            if (i > 0) {
                bullet.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(world, stack, Enchantments.PUNCH) + 1;
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }
            world.addFreshEntity(bullet);
            if (ModEnchantmentHelper.getLevel(world, stack, Enchantments.FLAME) > 0) {
                bullet.igniteForSeconds(100);
            }
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        }
    }

    public boolean isCharged(ItemStack stack) {
        Ammocomponent ammocomponent = stack.getOrDefault(WHRegistry.AMMO,Ammocomponent.DEFAULT);
        return ammocomponent.ammocount() > 0;
    }

    public static int getCharge(ItemStack stack) {
        return stack.getOrDefault(WHRegistry.AMMO,Ammocomponent.DEFAULT).ammocount();
    }

    public static void setCharge(ItemStack stack, int ammo) {
        stack.set(WHRegistry.AMMO,new Ammocomponent(ammo,ammo));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    public int getMaxUseTime() { return 72000; }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }
/*
    public Boolean hasshield(PlayerEntity player){
        return player.getOffHandStack().getItem() instanceof ShieldTemplate;
    }
    private void setshield(PlayerEntity player){ hasshield = hasshield(player); }
*/
    public int getTimetoreload(){ return timetoreload; }
}
