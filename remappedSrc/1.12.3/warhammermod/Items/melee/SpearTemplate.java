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
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import warhammermod.Entities.Projectile.SpearEntity;
import warhammermod.utils.ModEnchantmentHelper;

import java.util.List;
import java.util.Random;


public class SpearTemplate extends ToolItem {
    public SpearTemplate(ToolMaterial tier,net.minecraft.item.Item.Settings properties){
        super(tier,properties.component(DataComponentTypes.TOOL,createToolComponent()));
    }

    private static ToolComponent createToolComponent() {
        return new ToolComponent(
                List.of(ToolComponent.Rule.ofAlwaysDropping(List.of(Blocks.COBWEB), 15.0F), ToolComponent.Rule.of(BlockTags.SWORD_EFFICIENT, 1.5F)), 1.0F, 2
        );
    }
    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                BASE_ATTACK_DAMAGE_MODIFIER_ID, (double)((float)baseAttackDamage + material.getAttackDamage()), EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, (double)attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .build();
    }


    public boolean canMine(BlockState blockState, World level, BlockPos blockPos, PlayerEntity player) {
        return !player.isCreative();
    }
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerIn, Hand handIn)  {
        ItemStack stack = playerIn.getStackInHand(handIn);
        if (stack.getDamage() >= stack.getMaxDamage() - 2) {
            return TypedActionResult.fail(stack);
        } else {
            playerIn.setCurrentHand(handIn);
            return TypedActionResult.consume(stack);
        }
    }

        public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if(entityLiving instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity)entityLiving;

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
    }

    public int getMaxUseTime(ItemStack stack) {
        return 1000;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }


    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }


}
