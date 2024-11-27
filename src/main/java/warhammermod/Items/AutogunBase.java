package warhammermod.Items;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.Random;

public abstract class AutogunBase extends GunBase implements IReloadItem {
    protected Random rand= new Random();
    private final int FireRate;
    private final int AmmoConsumption;

    public AutogunBase(Settings properties, Item ammotype, int time, int magsize, int ammoconsumption, int firerate) {
        super(properties,ammotype,time,magsize);
        AmmoConsumption=ammoconsumption;
        FireRate=firerate;
    }
    public void usageTick(World world, LivingEntity player, ItemStack stack, int count) {
        if (player instanceof PlayerEntity entityplayer){
            if((isCharged(stack) && (getMaxUseTime()-count)/getFireRate()<=getCharge(stack))|| entityplayer.isCreative()) {
                if((getMaxUseTime()-count)!=0 && (getMaxUseTime()-count)%getFireRate()==0){
                    fire( entityplayer,world,stack);
                }
            }
            else if ((count == getMaxUseTime() - timetoreload) && !isCharged(stack)) {
                world.playSound(null,player.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS,1,1);
            }
        }
    }

    public boolean onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {//implement infinity if possible
        if (entityLiving instanceof PlayerEntity player && !worldIn.isClient() && !((PlayerEntity) entityLiving).isCreative()) {

            if(isCharged(stack)) {
                setCharge(stack,getCharge(stack)-(getMaxUseTime()-timeLeft)/getFireRate());
                return true;
            }else{

                stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
                int ammoreserve = this.findAmmo(player).getCount();
                if ((ammoreserve >= 0)) {
                    this.findAmmo(player).decrement(Math.min(ammoreserve,AmmoConsumption));
                    setCharge(stack,(int)((float)Magsize/AmmoConsumption* Math.min(ammoreserve,AmmoConsumption)));
                    return true;
                }

            }
        }
        return false;
    }
    public int getFireRate() {
        return FireRate;
    }

}
