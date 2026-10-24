package warhammermod.Items.ranged;


import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import warhammermod.Entities.Projectile.Bullet;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.IReloadItem;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;

import java.util.Random;


public class GunSwordTemplate extends SwordItem implements IReloadItem { //needs sword and bow enchantements
    float damage;
    public int timetoreload;
    public int Magsize;
    protected Random rand= new Random();
    public boolean hasshield=false; //use player.getitemoffhand instead
    public float width = 1.5F;
    public float height = 1.5F;



    public GunSwordTemplate(ToolMaterial tier, int time, int magsize, float damagein){
        super(tier,(new net.minecraft.item.Item.Settings().attributeModifiers(GunSwordTemplate.createAttributeModifiers(tier,3,-2.4F))));
        damage=damagein;
        timetoreload=time;
        Magsize=magsize;
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
        return stack.getItem().equals(ItemsInit.Cartridge);
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
                world.playSound(null,player.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS,1,1);
            }
        }
    }


    public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity player) {
            if (isCharged(stack) || player.isCreative()) {
                fire(player,worldIn,stack);
                if(!player.isCreative())setCharge(stack,getCharge(stack)-1);
            }
            else if(timetoreload<=getMaxUseTime()-timeLeft && !worldIn.isClient()) {
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
    public void fire(PlayerEntity player, World world, ItemStack stack) {
        if(world.isClient()) {
            for (int k = 0; k < 30; ++k) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, player.getX() + player.getRotationVector().x * 2 + (double) (this.rand.nextFloat() * this.width * 2) - (double) this.width, player.getY() + 0.4 + (double) (this.rand.nextFloat() * this.height), player.getZ() + player.getRotationVector().z * 2 + (double) (this.rand.nextFloat() * this.width * 2) - (double) this.width, d2, d0, d1);
            }
        }
        if(!world.isClient()) {
            player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, player.getSoundCategory(), 1.0f, 1.35F/(rand.nextFloat()*0.4F+1.2F)+0.5F);
            Bullet bullet = new Bullet(player, world, damage,stack,findAmmo(player));
            bullet.setPosition(player.getX(), player.getEyeY() - 0.26, player.getZ());
            bullet.setVelocity(player,player.getPitch(), player.getYaw(), 0, 3.5F, 0.3F);

            int i = ModEnchantmentHelper.getLevel(world, stack, Enchantments.POWER);
            if (i > 0) {
                bullet.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(world, stack, Enchantments.PUNCH) + 1;
            if (k > 0) {
                bullet.setknockbacklevel(k);
            }
            world.spawnEntity(bullet);
            if (ModEnchantmentHelper.getLevel(world, stack, Enchantments.FLAME) > 0) {
                bullet.setOnFireFor(100);
            }
            stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
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
}
