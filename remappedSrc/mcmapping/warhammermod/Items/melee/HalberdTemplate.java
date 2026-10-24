package warhammermod.Items.melee;


import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import warhammermod.Entities.Projectile.HalberdEntity;
import warhammermod.utils.ModEnchantmentHelper;

public class HalberdTemplate extends SwordItem {
    protected float attackDamage;


    public HalberdTemplate(Tier tier, Properties properties) {
        super(tier, properties.attributes(HalberdTemplate.createAttributeModifiers(tier,3F,-2.8F)));
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
       ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 2) {
            return InteractionResultHolder.fail(itemstack);
        }
        else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    public void releaseUsing(ItemStack stack, Level world, LivingEntity entityLiving, int timeLeft) {

        if(getMaxUseTime(stack)-20>=timeLeft && entityLiving instanceof Player){
            Player player = (Player) entityLiving;

            HalberdEntity entity = new HalberdEntity(player,world, getAttackDamage()*1.3F,stack);

            int i = ModEnchantmentHelper.getLevel(world,stack,Enchantments.SHARPNESS);
            if (i > 0) {
                entity.setpowerDamage(i);
            }
            int k = ModEnchantmentHelper.getLevel(world,stack,Enchantments.KNOCKBACK) + 1;
            if (k > 0) {
                entity.setknockbacklevel(k);
            }
            entity.setPos(player.getX(), player.getEyeY() - 0.26, player.getZ());
            entity.shootFromRotation(player,player.getXRot(), player.getYRot(), 0.0F, 3F, 0.5F);

            world.addFreshEntity(entity);
            world.playSound(null,player.blockPosition(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundSource.PLAYERS,1,1);
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
            player.getCooldowns().addCooldown(this,60);
        }
    }

    public int getMaxUseTime(ItemStack stack) {
        return 1000;
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


    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }




}
