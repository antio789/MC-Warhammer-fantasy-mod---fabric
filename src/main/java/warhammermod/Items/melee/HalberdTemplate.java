package warhammermod.Items.melee;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.WHRegistry;

public class HalberdTemplate extends Item {
    /**
     * Reach of the charged power thrust, matches the extended melee attack range
     * (vanilla spear uses 4.5 min / 6.5 max).
     */
    private static final float THRUST_RANGE = 6.5F;
    private static final float THRUST_CHARGE_TICKS = 20.0F;
    /** Length of the release-thrust swing cycle in ticks (vanilla whack is 6). */
    public static final int THRUST_SWING_TICKS = 12;

    protected final float attackDamage;

    public HalberdTemplate(ToolMaterial material, Item.Properties properties, float damage, float attackspeed) {
        super(material.applySwordProperties(properties, damage, attackspeed)
                .attributes(HammerTemplate.createAttributes(damage, attackspeed))
                // extended reach for a polearm, but normal attacks stay a vanilla
                // sword-style sweep; the line-piercing thrust is only the charged
                // power attack (releaseUsing)
                .component(DataComponents.ATTACK_RANGE, new AttackRange(2.0F, 4.5F, 2.0F, 6.5F, 0.125F, 0.5F))
                .component(DataComponents.MINIMUM_ATTACK_CHARGE, 1.0F));
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

    /**
     * Charged power thrust: a direct line attack from the eyes along the look
     * direction, piercing every entity in its path (replaces the 1.21.3
     * HalberdEntity projectile with an equivalent direct implementation).
     */
    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (getUseDuration(stack, entity) - THRUST_CHARGE_TICKS >= timeLeft && entity instanceof Player player) {
            float damage = getDamage() * 1.3F;
            int sharpness = ModEnchantmentHelper.getLevel(level, stack, Enchantments.SHARPNESS);
            if (sharpness > 0) {
                damage += sharpness * 0.5F + 0.5F;
            }
            int knockbackLevel = ModEnchantmentHelper.getLevel(level, stack, Enchantments.KNOCKBACK);

            Vec3 origin = player.getEyePosition();
            Vec3 look = player.getViewVector(1.0F);
            double range = THRUST_RANGE;

            // the thrust stops at walls
            BlockHitResult blockHit = level.clip(new ClipContext(origin, origin.add(look.scale(range)),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (blockHit.getType() != HitResult.Type.MISS) {
                range = blockHit.getLocation().distanceTo(origin);
            }
            Vec3 end = origin.add(look.scale(range));

            boolean hitSomething = false;
            AABB searchBox = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0D);
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, searchBox,
                    target -> target != player && target.isAlive() && !target.isSpectator())) {
                if (target.getBoundingBox().clip(origin, end).isPresent()) {
                    var damageSource = player.damageSources().playerAttack(player);
                    target.hurt(damageSource, damage);
                    double dx = player.getX() - target.getX();
                    double dz = player.getZ() - target.getZ();
                    target.knockback(0.4D + knockbackLevel * 0.4D, dx, dz, damageSource, 0.4F);
                    hitSomething = true;
                }
            }

            if (hitSomething && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                        player.getX() + look.x * (range * 0.6F),
                        player.getEyeY() - 0.4D,
                        player.getZ() + look.z * (range * 0.6F), 4, 0.4D, 0.2D, 0.4D, 0.0D);
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_KNOCKBACK, player.getSoundSource(), 1.0F, 1.0F);
            stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
            player.getCooldowns().addCooldown(stack, 60);
            // flag this one swing as a thrust: HalberdThrustSwingMixin makes
            // ItemStack.getSwingAnimation() return STAB while the flag is set,
            // so the vanilla spear thrust animation plays first and third
            // person; HalberdSwingMixin clears the flag when the swing ends
            stack.set(WHRegistry.HALBERD_THRUST, true);
            player.swing(InteractionHand.MAIN_HAND);
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
