package warhammermod.Items.melee.specials;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
import warhammermod.Items.melee.HammerTemplate;

public class Ghal_Maraz extends HammerTemplate {
    public Ghal_Maraz(ToolMaterial material, Item.Properties properties, float damage, float attackspeed) {
        super(material, properties, damage, attackspeed);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return false;
        boolean hitsomething = false;
        float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(3.0D, 0.25D, 3.0D))) {
            if (target != player && entity.distanceToSqr(target) < 9.0D) {
                target.hurt(entity.damageSources().playerAttack(player), damage);
                hitsomething = true;
            }
        }
        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        if (hitsomething) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 0.5F, 1.0F);
        }
        applyeffects(stack, player, level);
        return true;
    }

    public boolean applyeffects(ItemStack stack, Player player, Level level) {
        if (!level.isClientSide() && !player.getCooldowns().isOnCooldown(stack)) {
            if (level.getRandom().nextFloat() < 0.8F) {
                if (level.getRandom().nextFloat() < 0.4F) {
                    if (level.getRandom().nextFloat() < 0.3F) {
                        applypotioneffect(player, 2);
                        return true;
                    }
                    applypotioneffect(player, 1);
                    return true;
                }
                applypotioneffect(player, 0);
                return true;
            }
        }
        return false;
    }

    public void applypotioneffect(Player player, int level) {
        if (level == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 150, 0));
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 300, 0));
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 0));
        } else if (level == 1) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 400, 1));
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 1));
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 1));
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 500, 1));
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 350, 1));
        }
        player.getCooldowns().addCooldown(this.getDefaultInstance(), 600);
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
