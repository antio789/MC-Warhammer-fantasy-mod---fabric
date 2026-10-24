package warhammermod.Items.melee;


import net.minecraft.world.item.ShieldItem;

public class SmallShieldTemplate extends ShieldItem {


    public SmallShieldTemplate(Properties builder){
        super(builder.durability(1008));
    }



/*
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(entityIn instanceof Player && worldIn.isClientSide()){
            LivingEntity player = (LivingEntity) entityIn;

            if(player.getUseItem() != stack && player.getItemInHand(offhand) != stack && player.getAttribute(Attributes.MOVEMENT_SPEED) !=null) {

                AttributeInstance modifiableattributeinstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (modifiableattributeinstance.getModifier(BLOCKING_SPEED_BOOST_ID) != null) {

                    modifiableattributeinstance.removeModifier(BLOCKING_SPEED_BOOST);
                }
                if (modifiableattributeinstance.getModifier(SPRINT_SPEED_BOOST_ID) != null) {

                    modifiableattributeinstance.removeModifier(SPRINT_SPEED_BOOST);
                }
            }

        }
    }*/

}
