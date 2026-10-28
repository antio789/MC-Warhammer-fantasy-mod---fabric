package warhammermod.Entities.Projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ProjectileBase extends AbstractArrow {

    @Nullable
    private IntOpenHashSet piercedEntities;
    public float projectiledamage;
    protected float extradamage;
    protected int knocklevel;
    public float totaldamage;
    private ItemStack weapon;
    private static final EntityDataAccessor<Byte> custom_PIERCE_LEVEL = SynchedEntityData.defineId(ProjectileBase.class, EntityDataSerializers.BYTE);
    @Nullable
    private List<Entity> piercingKilledEntities;

    private void setcustomPierceLevel(byte level) {
        this.entityData.set(custom_PIERCE_LEVEL, level);
    }

    private byte getcustomPierceLevel() {
        return this.entityData.get(custom_PIERCE_LEVEL);
    }

    public void setpowerDamage(float powerIn) {
        extradamage = 1.5F * powerIn;
    }

    public void setknockbacklevel(int knockin) {
        knocklevel = knockin;
    }

    public float getTotalDamage() {
        return projectiledamage + extradamage;
    }

    public void applyspecialeffect(LivingEntity entity) {
    }

    public ProjectileBase(EntityType<? extends ProjectileBase> entityType, Level world) {
        super(entityType, world);
        pickup = Pickup.DISALLOWED;
    }

    public ProjectileBase(EntityType<? extends ProjectileBase> type, Level world, LivingEntity owner, ItemStack ammo, float damageIn, @Nullable ItemStack weapon) {
        this(type, world);
        this.setOwner(owner);
        this.setPickupItemStack(ammo);
        if (weapon != null && world instanceof ServerLevel serverWorld) {
            this.weapon = weapon.copy();
            int i = EnchantmentHelper.getPiercingCount(serverWorld, weapon, this.getPickupItemStackOrigin());
            if (i > 0) {
                this.setcustomPierceLevel((byte) i);
            }
            EnchantmentHelper.onProjectileSpawned(serverWorld, weapon, this, item -> this.weapon = null);
        }
        projectiledamage = damageIn;
    }

    @Override
    public ItemStack getWeaponItem() {
        return this.weapon;
    }

    public void setTotaldamage(float totaldamage) {
        this.projectiledamage = totaldamage;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        this.playSound(SoundEvents.STONE_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity hitentity = entityHitResult.getEntity();
        Entity shooterentity = this.getOwner();
        DamageSource damagesource;
        if (shooterentity == null) {
            damagesource = damageSources().arrow(this, this);
        } else {
            damagesource = damageSources().arrow(this, shooterentity);
            if (shooterentity instanceof LivingEntity) {
                ((LivingEntity) shooterentity).setLastHurtMob(hitentity);
            }
        }
        float fldamage = getTotalDamage();
        if (this.getcustomPierceLevel() > 0) {
            if (this.piercedEntities == null) {
                this.piercedEntities = new IntOpenHashSet(5);
            }

            if (this.piercingKilledEntities == null) {
                this.piercingKilledEntities = Lists.<Entity>newArrayListWithCapacity(5);
            }

            if (this.piercedEntities.size() >= this.getcustomPierceLevel() + 1) {
                this.discard();
                return;
            }

            this.piercedEntities.add(hitentity.getId());
        }
        boolean isEnderman = hitentity.getType() == EntityTypes.ENDERMAN;
        int intFireticks = hitentity.getRemainingFireTicks();
        if (this.isOnFire() && !isEnderman) {
            hitentity.igniteForSeconds(5);
        }

        boolean hurt = hitentity.hurtServer((ServerLevel) this.level(), damagesource, fldamage);
        if (hurt) {
            if (isEnderman) {
                return;
            }
            if (hitentity instanceof LivingEntity livingentity) {
                applyspecialeffect(livingentity);
                if (this.knocklevel > 0) {
                    this.doKnockback(livingentity, damagesource);
                }
                if (this.level() instanceof ServerLevel serverworld && shooterentity instanceof LivingEntity) {
                    EnchantmentHelper.doPostAttackEffectsWithItemSource(serverworld, livingentity, damagesource, this.getWeaponItem());
                }

                this.doPostHurtEffects(livingentity);
                if (livingentity != shooterentity && livingentity instanceof Player && shooterentity instanceof ServerPlayer && !this.isSilent()) {
                    ((ServerPlayer) shooterentity).connection.send(new net.minecraft.network.protocol.game.ClientboundGameEventPacket(net.minecraft.network.protocol.game.ClientboundGameEventPacket.PLAY_ARROW_HIT_SOUND, 0.0F));
                }
                if (!livingentity.isAlive() && this.piercingKilledEntities != null) {
                    this.piercingKilledEntities.add(shooterentity);
                }
            }

            this.playSound(SoundEvents.STONE_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getcustomPierceLevel() <= 0) {
                discard();
            }
        } else {
            hitentity.setRemainingFireTicks(intFireticks);
            this.deflect(ProjectileDeflection.REVERSE, hitentity, net.minecraft.world.entity.EntityReference.of(this.getOwner()), false);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.2D));
            if (!this.level().isClientSide() && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                discard();
            }
        }
        discard();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(custom_PIERCE_LEVEL, (byte) 0);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getId()));
    }
}
