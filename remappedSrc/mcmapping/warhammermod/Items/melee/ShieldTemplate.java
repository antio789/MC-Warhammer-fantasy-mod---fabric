package warhammermod.Items.melee;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import warhammermod.Items.GunBase;

public class ShieldTemplate  extends ShieldItem {


    public ShieldTemplate( Properties builder){
        super(builder.durability(1008));
    }

    private boolean hasGun(LivingEntity entityIn, ItemStack stack){
        if(entityIn instanceof Player) {
            Player player = (Player) entityIn;
            return stack.getItem() instanceof GunBase && ((GunBase) stack.getItem()).isReadytoFire(stack);
            //|| stack.getItem() instanceof GunSwordTemplate && ((GunSwordTemplate) stack.getItem()).isReadytoFire(stack);
        }return false;
    }


}
