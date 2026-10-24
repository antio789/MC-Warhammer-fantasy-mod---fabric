package warhammermod.Items;


import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.WHRegistry;

import java.util.Random;
import java.util.function.Predicate;

public abstract class GunBase extends RangedWeaponItem implements IReloadItem { //necesary to rework the shield component move to mixin
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

    public GunBase(net.minecraft.item.Item.Settings properties, Item ammotype, int time, int magsize) {
        super(properties.component(WHRegistry.AMMO,Ammocomponent.DEFAULT));
        Magsize=magsize;
        timetoreload=time;
        AmmoType=ammotype;
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
        return stack.getItem().equals(AmmoType);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerIn, Hand hand) {
        ItemStack stack = playerIn.getStackInHand(hand);
        if(isCharged(stack) || playerIn.isCreative()|| !findAmmo(playerIn).isEmpty()){
            playerIn.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity player, ItemStack stack, int count) {
        if (player instanceof PlayerEntity){
            if ((count == getMaxUseTime() - timetoreload) && !isCharged(stack) && !((PlayerEntity) player).isCreative() && !world.isClient()) {
                world.playSound(null,player.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE,SoundCategory.PLAYERS,1,1);
            }
        }
    }


    public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity player) {
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
                        this.findAmmo(player).decrement(ammoreserve);
                    }
                    setCharge(stack,ammoreserve);
                } else {
                    if (infinitylevel == 0) {
                        this.findAmmo(player).decrement(Magsize);
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
    public void fire(PlayerEntity player, World world, ItemStack stack){}

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
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }
    public int getMaxUseTime() { return 72000; }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
/*
    public Boolean hasshield(PlayerEntity player){
        return player.getOffHandStack().getItem() instanceof ShieldTemplate;
    }

    private void setshield(PlayerEntity player){ hasshield = hasshield(player); }
*/
    public int getTimetoreload(){ return timetoreload; }

    public int getEnchantability() { return 1; }
    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
    }
    @Override
    public Predicate<ItemStack> getProjectiles() {
        return null;
    }
    @Override
    public int getRange() {
        return 15;
    }

    public void playparticles(ParticleEffect effect,World world,PlayerEntity player){
        if(world.isClient()) {
            for (int k = 0; k < 30; ++k) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                world.addParticle(effect, player.getX() + player.getRotationVector().x * 2 + (double) (this.rand.nextFloat() * this.width * 2) - (double) this.width, player.getY() + 0.4 + (double) (this.rand.nextFloat() * this.height), player.getZ() + player.getRotationVector().z * 2 + (double) (this.rand.nextFloat() * this.width * 2) - (double) this.width, d2, d0, d1);
            }
        }
    }
}
