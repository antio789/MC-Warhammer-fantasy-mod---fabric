package warhammermod.Items.melee;


import warhammermod.Entities.Projectile.SpearEntity;
import warhammermod.utils.ModEnchantmentHelper;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;


public class SpearTemplate extends TieredItem {
    public SpearTemplate(Tier tier,Properties properties){
        super(tier,properties.component(DataComponents.TOOL,createToolComponent()));
    }

    private static Tool createToolComponent() {
        return new Tool(
                List.of(Tool.Rule.minesAndDrops(List.of(Blocks.COBWEB), 15.0F), Tool.Rule.overrideSpeed(BlockTags.SWORD_EFFICIENT, 1.5F)), 1.0F, 2
        );
    }
    public static ItemAttributeModifiers createAttributeModifiers(Tier material, float baseAttackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                BASE_ATTACK_DAMAGE_ID, (double)((float)baseAttackDamage + material.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }


    public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        return !player.isCreative();
    }
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player playerIn, InteractionHand handIn)  {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (stack.getDamageValue() >= stack.getMaxDamage() - 2) {
            return InteractionResultHolder.fail(stack);
        } else {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.consume(stack);
        }
    }

        public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if(entityLiving instanceof Player){
            Player player = (Player)entityLiving;

            SpearEntity entity = new SpearEntity(player,worldIn,(float)player.getAttributeValue(Attributes.ATTACK_DAMAGE),stack);
            entity.shootFromRotation(player,player.getXRot(), player.getYRot(), 0.0F, 1.3F, 1.0F);

            int i = ModEnchantmentHelper.getLevel(worldIn,stack,Enchantments.SHARPNESS);
            if (i > 0) {
                entity.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(worldIn,stack,Enchantments.KNOCKBACK);
            if (k > 0) {
                entity.setknockbacklevel(k);
            }
            if (ModEnchantmentHelper.getLevel(worldIn,stack,Enchantments.FIRE_ASPECT) > 0) {
                entity.igniteForSeconds(100);
            }
            worldIn.addFreshEntity(entity);
            Random random = new Random();
            worldIn.playSound(null,player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,1,1.35F/(random.nextFloat()*0.4F+1.2F)+0.5F);
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
            player.getInventory().removeItem(stack);
        }
    }

    public int getMaxUseTime(ItemStack stack) {
        return 1000;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }


    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }


}
