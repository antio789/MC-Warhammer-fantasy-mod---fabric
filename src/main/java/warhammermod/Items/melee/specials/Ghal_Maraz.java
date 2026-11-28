package warhammermod.Items.melee.specials;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.joml.Vector2f;
import warhammermod.Items.melee.HammerTemplate;
import warhammermod.utils.ModEnchantmentHelper;

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
        applyeffects(stack, player, level);
        boolean hitsomething = false;
        if (!level.isClientSide()) {
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(3.0D, 0.25D, 3.0D))) {
                if (target != player && entity.distanceToSqr(target) < 9.0D) {
                    float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    Vector2f vec = calculateVector(player, target);
                    target.knockback(2.5D + ModEnchantmentHelper.getLevel(level, stack, Enchantments.KNOCKBACK), vec.x, vec.y,
                            entity.damageSources().playerAttack(player), damage);
                    target.hurt(entity.damageSources().playerAttack(player), damage);
                    hitsomething = true;
                }
            }
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
        if (hitsomething) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 0.5F, 1.0F);
        }
        return hitsomething;
    }

    public boolean applyeffects(ItemStack stack, Player player, Level level) {
        if (!level.isClientSide() && !player.getCooldowns().isOnCooldown(stack)) {
            if (level.getRandom().nextFloat() < 0.8F) {
                if (level.getRandom().nextFloat() < 0.4F) {
                    if (level.getRandom().nextFloat() < 0.3F) {
                        handleEffects(player, level, true);
                        applypotioneffect(player, 2);
                        return true;
                    }
                    handleEffects(player, level, true);
                    applypotioneffect(player, 1);
                    return true;
                }
                handleEffects(player, level, true);
                applypotioneffect(player, 0);
                return true;
            }
            handleEffects(player, level, false);
        }
        return false;
    }

    // 1.21.3 HandleEffects: action-bar message + toast sound (sendOverlayMessage = sendMessage(text, true))
    public void handleEffects(Player player, Level level, boolean success) {
        if (success) {
            player.sendOverlayMessage(Component.translatable("item.ghal_maraz.succes"));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, player.getSoundSource(), 0.5F, 1.6F);
        } else {
            player.sendOverlayMessage(Component.translatable("item.ghal_maraz.fail"));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_OUT, player.getSoundSource(), 0.5F, 1.5F);
        }
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, consumer, flag);
        consumer.accept(Component.translatable("tooltip.ghal_maraz"));
    }

    private Vector2f calculateVector(Entity e1, Entity e2) {
        float x = (float) (e1.getX() - e2.getX());
        float z = (float) (e1.getZ() - e2.getZ());
        float r = (float) Math.sqrt(x * x + z * z);
        return new Vector2f(x / r, z / r);
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
