package warhammermod.Entities.Projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
//would be nice to use vanilla enchantements
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
    private byte getcustomPierceLevel(){
        return this.entityData.get(custom_PIERCE_LEVEL);
    }
    public void setpowerDamage(float powerIn){
        extradamage=1.5F*powerIn;
    }
    public void setknockbacklevel(int knockin){
        knocklevel=knockin;
    }

    public float getTotalDamage(){
        return projectiledamage+extradamage;
    }

    public void applyspecialeffect(LivingEntity entity){
    }

    public ProjectileBase(EntityType<? extends ProjectileBase> entityType, Level world) {
        super(entityType, world);
    }
    public ProjectileBase(EntityType<? extends ProjectileBase> type, Level world,LivingEntity owner,ItemStack ammo,float damageIn, @Nullable ItemStack weapon) {
        this(type, world);
        this.setOwner(owner);
        this.setPickupItemStack(ammo);
        this.setCustomName(ammo.get(DataComponents.CUSTOM_NAME));
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
/*
    public ProjectileBase(EntityType<? extends ProjectileBase> type, World world,LivingEntity owner,float damageIn, @Nullable ItemStack weapon) {
        this(type, world, owner, null, damageIn, weapon);
    }

*/
        @Override
    public ItemStack getWeaponItem() {
        return this.weapon;
    }



    protected void onHitBlock(BlockHitResult blockHitResult){
        this.playSound(SoundEvents.STONE_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.remove(RemovalReason.DISCARDED);
    }

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
        boolean isEnderman = hitentity.getType() == EntityType.ENDERMAN;
        int intFireticks = hitentity.getRemainingFireTicks();
        if (this.isOnFire() && !isEnderman) {
            hitentity.igniteForSeconds(5);
        }

        if (hitentity.hurt(damagesource, fldamage)) {
            if (isEnderman) {
                return;
            }
            if (hitentity instanceof LivingEntity livingentity) {
                applyspecialeffect(livingentity);
                if (this.knocklevel > 0) {
                    double e = Math.max(0.0, 1.0 - livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    Vec3 vec3d = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knocklevel * 0.6D*e);
                    if (vec3d.lengthSqr() > 0.0D) {
                        livingentity.push(vec3d.x, 0.1D, vec3d.z);
                    }
                }
                Level hitworld = this.level();
                if (hitworld instanceof ServerLevel serverworld && shooterentity instanceof LivingEntity) {
                    EnchantmentHelper.doPostAttackEffectsWithItemSource(serverworld,livingentity,damagesource,this.getWeaponItem());
                }

                this.doPostHurtEffects(livingentity);
                if (livingentity != shooterentity && livingentity instanceof Player && shooterentity instanceof ServerPlayer && !this.isSilent()) {
                    ((ServerPlayer) shooterentity).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, ClientboundGameEventPacket.DEMO_PARAM_INTRO));
                }

            }

            this.playSound(SoundEvents.STONE_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getcustomPierceLevel() <= 0) {
                this.remove(RemovalReason.DISCARDED);
            }

        } else {
            hitentity.setRemainingFireTicks(intFireticks);
            this.deflect(ProjectileDeflection.REVERSE,hitentity,this.getOwner(),false);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.2D));
            if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
        this.remove(RemovalReason.DISCARDED);

    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("cmPierceLevel", this.getcustomPierceLevel());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
            super.defineSynchedData(builder);
            builder.define(custom_PIERCE_LEVEL,(byte)0);
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setcustomPierceLevel(nbt.getByte("cmPierceLevel"));
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getId()));
    }

}
