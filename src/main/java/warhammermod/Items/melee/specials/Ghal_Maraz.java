package warhammermod.Items.melee.specials;


import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import warhammermod.Items.melee.HammerTemplate;
import warhammermod.utils.ModEnchantmentHelper;

import java.util.List;

public class Ghal_Maraz extends HammerTemplate {
    public Ghal_Maraz(Item.Settings settings){
        super(ToolMaterial.NETHERITE,6,-2.5F,settings);
    }


    public ActionResult use(World level, PlayerEntity player, Hand hand) {
        player.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity entityLiving, int timeLeft) {
        PlayerEntity player = (PlayerEntity) entityLiving;
        applyeffects(stack,player,world);
        boolean hitsomething = false;
        if(!world.isClient() && world instanceof ServerWorld server) {
            for (LivingEntity targets : world.getNonSpectatingEntities(LivingEntity.class, entityLiving.getBoundingBox().expand(3D, 0.25D, 3D))) {
                if (targets != player && entityLiving.squaredDistanceTo(targets) < 9.0D) {
                    float f = (float) player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
                    Vec2f vec = calculatevector(player, targets);
                    targets.takeKnockback(2.5F + ModEnchantmentHelper.getLevel(world, stack, Enchantments.KNOCKBACK), (double) vec.x, vec.y);
                    targets.damage(server, player.getDamageSources().playerAttack(player), f);
                    hitsomething = true;
                }
            }
            stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
        }
        if(hitsomething) {
            world.playSound( null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 0.5F, 1.0F);
            return true;
        }
        return false;
    }


    public boolean applyeffects(ItemStack stack, PlayerEntity player,World world) {

        if (!world.isClient() && !player.getItemCooldownManager().isCoolingDown(stack))
        {
            if (world.random.nextFloat() < 0.8F)
            {
                if (world.random.nextFloat() < 0.4F)
                {
                    if (world.random.nextFloat() < 0.3F)
                    {
                        HandleEffects(player,world,true);
                        applypotioneffect(stack,player,2);
                        return true;
                    }
                    HandleEffects(player,world,true);
                    applypotioneffect(stack,player,1);
                    return true;
                }
                HandleEffects(player,world,true);
                applypotioneffect(stack,player,0);
                return true;
            }
            HandleEffects(player,world,false);
            return false;
        }
        return false;
    }

    public  void HandleEffects(PlayerEntity player, World world, Boolean succes){
        if(succes) {
            player.sendMessage(Text.translatable("item.ghal_maraz.succes"), true);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, player.getSoundCategory(), 0.5F, 1.6F);
        }
        else {
            player.sendMessage(Text.translatable("item.ghal_maraz.fail"),  true);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_OUT, player.getSoundCategory(), 0.5F, 1.5F);
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
    public void applypotioneffect(ItemStack stack,PlayerEntity player,int level){
        if(level ==0){
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,150,0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,300,0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH,300,0));
            player.getItemCooldownManager().set(stack, 600);
        }
        else if(level ==1){
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,200,1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,400,1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH,300,1));
            player.getItemCooldownManager().set(stack, 600);
        }
        else if(level ==2){
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,300,1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,500,1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH,350,1));
            player.getItemCooldownManager().set(stack, 600);
        }
    }



    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add((Text.translatable("tooltip.ghal_maraz")));
    }

    private Boolean hasEffects(PlayerEntity player){
        return player.hasStatusEffect(StatusEffects.REGENERATION) && player.hasStatusEffect(StatusEffects.RESISTANCE) && player.hasStatusEffect(StatusEffects.STRENGTH);
    }

    public int getMaxUseTime(ItemStack stack, LivingEntity user)  {
        return 1000;
    }

    private Vec2f calculatevector(Entity e1, Entity e2){
        float x = (float)(e1.getPos().x - e2.getPos().x);
        float z = (float)(e1.getPos().z - e2.getPos().z);
        float r = (float)(Math.sqrt(Math.pow(x,2)+Math.pow(z,2)));
        return new Vec2f(x/r,z/r);
    }


    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        return UseAction.BOW;
    }

    public boolean canRepair(ItemStack itemStack, ItemStack itemStack2) {
        return false;
    }

}
