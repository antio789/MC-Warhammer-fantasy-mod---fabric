package warhammermod.Items.melee;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import warhammermod.Entities.Projectile.SpearEntity;
import warhammermod.utils.ModEnchantmentHelper;

import java.util.Random;


public class SpearTemplate extends SwordItem {
    public SpearTemplate(ToolMaterial tier,Settings settings){
        super(tier,2,-2,settings);
    }

    public ActionResult use(World world, PlayerEntity playerIn, Hand handIn)  {
        ItemStack stack = playerIn.getStackInHand(handIn);
        if (stack.getDamage() >= stack.getMaxDamage() - 2) {
            return ActionResult.FAIL;
        } else {
            playerIn.setCurrentHand(handIn);
            return ActionResult.CONSUME;
        }
    }

    public boolean onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if(entityLiving instanceof PlayerEntity player){

            SpearEntity entity = new SpearEntity(player,worldIn,(float)player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE),stack);
            entity.setVelocity(player,player.getPitch(), player.getYaw(), 0.0F, 1.3F, 1.0F);

            int i = ModEnchantmentHelper.getLevel(worldIn,stack,Enchantments.SHARPNESS);
            if (i > 0) {
                entity.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(worldIn,stack,Enchantments.KNOCKBACK);
            if (k > 0) {
                entity.setknockbacklevel(k);
            }
            if (ModEnchantmentHelper.getLevel(worldIn,stack,Enchantments.FIRE_ASPECT) > 0) {
                entity.setOnFireFor(100);
            }
            worldIn.spawnEntity(entity);
            Random random = new Random();
            worldIn.playSound(null,player.getBlockPos(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,1,1.35F/(random.nextFloat()*0.4F+1.2F)+0.5F);
            stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
            player.getInventory().removeOne(stack);
        }
        return false;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }


    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }


}
