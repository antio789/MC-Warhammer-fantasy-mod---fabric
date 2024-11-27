package warhammermod.Items.melee;


import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.consume.UseAction;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import warhammermod.Entities.Projectile.HalberdEntity;
import warhammermod.utils.ModEnchantmentHelper;

public class HalberdTemplate extends SwordItem {
    protected float attackDamage;

    public HalberdTemplate(ToolMaterial tier, Settings settings) {
        super(tier,3,-2.8F,settings);
    }

    public ActionResult use(World level, PlayerEntity player, Hand hand) {
       ItemStack itemstack = player.getStackInHand(hand);
        if (itemstack.getDamage() >= itemstack.getMaxDamage() - 2) {
            return ActionResult.FAIL;
        }
        else {
            player.setCurrentHand(hand);
            return ActionResult.CONSUME;
        }
    }

    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity entityLiving, int timeLeft) {

        if(getMaxUseTime(stack)-20>=timeLeft && entityLiving instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) entityLiving;

            HalberdEntity entity = new HalberdEntity(player,world, getAttackDamage()*1.3F,stack);

            int i = ModEnchantmentHelper.getLevel(world,stack,Enchantments.SHARPNESS);
            if (i > 0) {
                entity.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(world,stack,Enchantments.KNOCKBACK) + 1;
            if (k > 0) {
                entity.setknockbacklevel(k);
            }
            entity.setPosition(player.getX(), player.getEyeY() - 0.26, player.getZ());
            entity.setVelocity(player,player.getPitch(), player.getYaw(), 0.0F, 3F, 0.5F);

            world.spawnEntity(entity);
            world.playSound(null,player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS,1,1);
            stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
            player.getItemCooldownManager().set(stack,60);
            return true;
        }
        return false;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 1000;
    }

    public UseAction getUseAction(ItemStack itemStack) {
        return UseAction.BOW;
    }
    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }




}
