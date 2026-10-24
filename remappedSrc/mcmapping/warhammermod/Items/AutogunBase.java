package warhammermod.Items;


import java.util.Random;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AutogunBase extends GunBase implements IReloadItem {
    protected Random rand= new Random();
    private final int FireRate;
    private final int AmmoConsumption;

    public AutogunBase(Properties properties, Item ammotype, int time, int magsize, int ammoconsumption, int firerate) {
        super(properties,ammotype,time,magsize);
        AmmoConsumption=ammoconsumption;
        FireRate=firerate;
    }
    public void onUseTick(Level world, LivingEntity player, ItemStack stack, int count) {
        if (player instanceof Player entityplayer){
            if((isCharged(stack) && (getMaxUseTime()-count)/getFireRate()<=getCharge(stack))|| entityplayer.isCreative()) {
                if((getMaxUseTime()-count)!=0 && (getMaxUseTime()-count)%getFireRate()==0){
                    fire( entityplayer,world,stack);
                }
            }
            else if ((count == getMaxUseTime() - timetoreload) && !isCharged(stack)) {
                world.playSound(null,player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,1,1);
            }
        }
    }

    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {//implement infinity if possible
        if (entityLiving instanceof Player player && !worldIn.isClientSide() && !((Player) entityLiving).isCreative()) {

            if(isCharged(stack)) {
                setCharge(stack,getCharge(stack)-(getMaxUseTime()-timeLeft)/getFireRate());
            }else{

                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                int ammoreserve = this.findAmmo(player).getCount();
                if ((ammoreserve >= 0)) {
                    this.findAmmo(player).shrink(Math.min(ammoreserve,AmmoConsumption));
                    setCharge(stack,(int)((float)Magsize/AmmoConsumption* Math.min(ammoreserve,AmmoConsumption)));
                }
            }
        }
    }
    public int getFireRate() {
        return FireRate;
    }

}
