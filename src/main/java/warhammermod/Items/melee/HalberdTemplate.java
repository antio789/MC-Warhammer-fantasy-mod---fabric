package warhammermod.Items.melee;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public class HalberdTemplate extends Item {
    protected final float attackDamage;

    public HalberdTemplate(ToolMaterial material, Item.Properties properties, float damage, float attackspeed) {
        super(material.applySwordProperties(properties, damage, attackspeed).attributes(HammerTemplate.createAttributes(damage, attackspeed)));
        this.attackDamage = damage;
    }

    public float getDamage() {
        return this.attackDamage;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 2) {
            return InteractionResult.FAIL;
        }
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (getUseDuration(stack, entity) - 20 >= timeLeft && entity instanceof Player player) {
            float damage = getDamage() * 1.3F;
            boolean hitsomething = false;
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(2.5D, 0.25D, 2.5D))) {
                if (target != player && entity.distanceToSqr(target) < 9.0D) {
                    target.hurt(entity.damageSources().playerAttack(player), damage);
                    hitsomething = true;
                }
            }
            if (hitsomething) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
                stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                player.getCooldowns().addCooldown(stack, 60);
            }
        }
        return true;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 1000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }
}
