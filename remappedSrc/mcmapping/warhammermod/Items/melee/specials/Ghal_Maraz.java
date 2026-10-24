package warhammermod.Items.melee.specials;


import warhammermod.Items.melee.HammerTemplate;
import warhammermod.utils.ModEnchantmentHelper;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

public class Ghal_Maraz extends HammerTemplate {
    public Ghal_Maraz(Item.Properties builder){
        super(Tiers.NETHERITE,builder,6,-2.5F);
    }


    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    public void releaseUsing(ItemStack stack, Level world, LivingEntity entityLiving, int timeLeft) {
        updated=8;
        Player player = (Player) entityLiving;
        applyeffects(stack,player,world);
        boolean hitsomething = false;
        for (LivingEntity targets : world.getEntitiesOfClass(LivingEntity.class, entityLiving.getBoundingBox().inflate(3D, 0.25D, 3D)))
        {
            if (targets != player && entityLiving.distanceToSqr(targets) < 9.0D)
            {
                float f = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                Vec2 vec = calculatevector(player,targets);
                targets.knockback(2.5F + ModEnchantmentHelper.getLevel(world, stack, Enchantments.KNOCKBACK), (double)  vec.x,vec.y);
                targets.hurt(player.damageSources().playerAttack(player), f);
                hitsomething=true;
            }
        }
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        if(hitsomething) {
            world.playSound( null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 0.5F, 1.0F);
        }
    }
    int updated=-1;

    public boolean applyeffects(ItemStack stack, Player player,Level world) {

        if (!world.isClientSide() && !player.getCooldowns().isOnCooldown(stack.getItem()))
        {
            if (world.random.nextFloat() < 0.8F)
            {
                if (world.random.nextFloat() < 0.4F)
                {
                    if (world.random.nextFloat() < 0.3F)
                    {
                        HandleEffects(player,world,true);
                        applypotioneffect(world,player,2);
                        return true;
                    }
                    HandleEffects(player,world,true);
                    applypotioneffect(world,player,1);
                    return true;
                }
                HandleEffects(player,world,true);
                applypotioneffect(world,player,0);
                return true;
            }
            HandleEffects(player,world,false);
            return false;
        }
        return false;
    }

    public  void HandleEffects(Player player, Level world, Boolean succes){
        if(succes) {
            player.displayClientMessage(Component.translatable("item.ghal_maraz.succes"), true);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, player.getSoundSource(), 0.5F, 1.6F);
        }
        else {
            player.displayClientMessage(Component.translatable("item.ghal_maraz.fail"),  true);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_OUT, player.getSoundSource(), 0.5F, 1.5F);
        }
    }
    /*
    public void inventoryTick(ItemStack itemStack, World level, Entity entity, int i, boolean bl) {
        if(level.isClient && entity instanceof  PlayerEntity){
            if(updated==0) {
                updated=-1;
                PlayerEntity player = (PlayerEntity) entity;
                if (hasEffects(player)) {
                    //level..particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                } else
                    MinecraftClient.getInstance().particleManager.addEmitter(entity, ParticleTypes.EFFECT, 30);
            }else if(updated>0) updated--;
        }
        super.inventoryTick(itemStack,level,entity,i,bl);
    }
*/
    public void applypotioneffect(Level wprmd,Player player,int level){
        if(level ==0){
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,150,0));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,300,0));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,300,0));
            player.getCooldowns().addCooldown(this, 600);
        }
        else if(level ==1){
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,200,1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,400,1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,300,1));
            player.getCooldowns().addCooldown(this, 600);
        }
        else if(level ==2){
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,300,1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,500,1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,350,1));
            player.getCooldowns().addCooldown(this, 600);
        }
    }



    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add((Component.translatable("tooltip.ghal_maraz")));
    }

    private Boolean hasEffects(Player player){
        return player.hasEffect(MobEffects.REGENERATION) && player.hasEffect(MobEffects.DAMAGE_RESISTANCE) && player.hasEffect(MobEffects.DAMAGE_BOOST);
    }

    public int getUseDuration(ItemStack stack, LivingEntity user)  {
        return 1000;
    }

    private Vec2 calculatevector(Entity e1, Entity e2){
        float x = (float)(e1.position().x - e2.position().x);
        float z = (float)(e1.position().z - e2.position().z);
        float r = (float)(Math.sqrt(Math.pow(x,2)+Math.pow(z,2)));
        return new Vec2(x/r,z/r);
    }


    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
        return false;
    }

}
