package warhammermod.Items;


import org.jetbrains.annotations.Nullable;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.WHRegistry;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public abstract class GunBase extends ProjectileWeaponItem implements IReloadItem { //necesary to rework the shield component move to mixin
    public int timetoreload;
    public int Magsize;

    public Item getAmmoType() {
        return AmmoType;
    }

    private final Item AmmoType;
    protected Random rand= new Random();
    public boolean hasshield=false; //use player.getitemoffhand instead
    public float width = 1.5F;
    public float height = 1.5F;

    public GunBase(Properties properties, Item ammotype, int time, int magsize) {
        super(properties.component(WHRegistry.AMMO,Ammocomponent.DEFAULT));
        Magsize=magsize;
        timetoreload=time;
        AmmoType=ammotype;
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
                world.playSound(null,player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE,SoundSource.PLAYERS,1,1);
            }
        }
    }


    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            if (isCharged(stack) || player.isCreative()) {
                fire(player,worldIn,stack);
                if(!player.isCreative())setAmmoused(stack,getCharge(stack)-1);
                else {
                    stack.set(WHRegistry.AMMO,new Ammocomponent(getCharge(stack),getMagCount(stack)+1));
                }
            }
            else if(timetoreload<=getMaxUseTime()-timeLeft) {
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
    public void fire(Player player, Level world, ItemStack stack){}

    public boolean isCharged(ItemStack stack) {
        Ammocomponent ammocomponent = stack.getOrDefault(WHRegistry.AMMO,Ammocomponent.DEFAULT);
        return ammocomponent.ammocount() > 0;
    }

    public static int getCharge(ItemStack stack) {
        return stack.getOrDefault(WHRegistry.AMMO,Ammocomponent.DEFAULT).ammocount();
    }

    public static int getMagCount(ItemStack stack) {
        return stack.getOrDefault(WHRegistry.AMMO,Ammocomponent.DEFAULT).startammo();
    }

    public static void setCharge(ItemStack stack, int ammo) {
        stack.set(WHRegistry.AMMO,new Ammocomponent(ammo,ammo));
    }

    public static void setAmmoused(ItemStack stack, int ammo) {
        stack.set(WHRegistry.AMMO,new Ammocomponent(ammo,getMagCount(stack)));
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

    public int getEnchantmentValue() { return 1; }
    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
    }
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return null;
    }
    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }

    public void playparticles(ParticleOptions effect,Level world,Player player){
        if(world.isClientSide()) {
            for (int k = 0; k < 30; ++k) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                world.addParticle(effect, player.getX() + player.getLookAngle().x * 2 + (double) (this.rand.nextFloat() * this.width * 2) - (double) this.width, player.getY() + 0.4 + (double) (this.rand.nextFloat() * this.height), player.getZ() + player.getLookAngle().z * 2 + (double) (this.rand.nextFloat() * this.width * 2) - (double) this.width, d2, d0, d1);
            }
        }
    }
}
