package warhammermod.Entities.Projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
//would be nice to use vanilla enchantements
public abstract class ProjectileBase extends PersistentProjectileEntity {

    @Nullable
    private IntOpenHashSet piercedEntities;
    public float projectiledamage;
    protected float extradamage;
    protected int knocklevel;
    public float totaldamage;
    private ItemStack weapon;
    private static final TrackedData<Byte> custom_PIERCE_LEVEL = DataTracker.registerData(ProjectileBase.class, TrackedDataHandlerRegistry.BYTE);
    @Nullable
    private List<Entity> piercingKilledEntities;

    private void setcustomPierceLevel(byte level) {
        this.dataTracker.set(custom_PIERCE_LEVEL, level);
    }
    private byte getcustomPierceLevel(){
        return this.dataTracker.get(custom_PIERCE_LEVEL);
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

    public ProjectileBase(EntityType<? extends ProjectileBase> entityType, World world) {
        super(entityType, world);
    }
    public ProjectileBase(EntityType<? extends ProjectileBase> type, World world,LivingEntity owner,ItemStack ammo,float damageIn, @Nullable ItemStack weapon) {
        this(type, world);
        this.setOwner(owner);
        this.setStack(ammo);
        this.setCustomName(ammo.get(DataComponentTypes.CUSTOM_NAME));
        if (weapon != null && world instanceof ServerWorld serverWorld) {
            this.weapon = weapon.copy();
            int i = EnchantmentHelper.getProjectilePiercing(serverWorld, weapon, this.getItemStack());
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
    public ItemStack getWeaponStack() {
        return this.weapon;
    }



    protected void onBlockHit(BlockHitResult blockHitResult){
        this.playSound(SoundEvents.BLOCK_STONE_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.remove(RemovalReason.DISCARDED);
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitentity = entityHitResult.getEntity();
        Entity shooterentity = this.getOwner();
        DamageSource damagesource;
        if (shooterentity == null) {
            damagesource = getDamageSources().arrow(this, this);
        } else {
            damagesource = getDamageSources().arrow(this, shooterentity);
            if (shooterentity instanceof LivingEntity) {
                ((LivingEntity) shooterentity).onAttacking(hitentity);
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
        int intFireticks = hitentity.getFireTicks();
        if (this.isOnFire() && !isEnderman) {
            hitentity.setOnFireFor(5);
        }

        if (hitentity.clientDamage(damagesource, fldamage)) {
            if (isEnderman) {
                return;
            }
            if (hitentity instanceof LivingEntity livingentity) {
                applyspecialeffect(livingentity);
                if (this.knocklevel > 0) {
                    double e = Math.max(0.0, 1.0 - livingentity.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE));
                    Vec3d vec3d = this.getVelocity().multiply(1.0D, 0.0D, 1.0D).normalize().multiply((double)this.knocklevel * 0.6D*e);
                    if (vec3d.lengthSquared() > 0.0D) {
                        livingentity.addVelocity(vec3d.x, 0.1D, vec3d.z);
                    }
                }
                World hitworld = this.getWorld();
                if (hitworld instanceof ServerWorld serverworld && shooterentity instanceof LivingEntity) {
                    EnchantmentHelper.onTargetDamaged(serverworld,livingentity,damagesource,this.getWeaponStack());
                }

                this.onHit(livingentity);
                if (livingentity != shooterentity && livingentity instanceof PlayerEntity && shooterentity instanceof ServerPlayerEntity && !this.isSilent()) {
                    ((ServerPlayerEntity) shooterentity).networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
                }

            }

            this.playSound(SoundEvents.BLOCK_STONE_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getcustomPierceLevel() <= 0) {
                this.remove(RemovalReason.DISCARDED);
            }

        } else {
            hitentity.setFireTicks(intFireticks);
            this.deflect(ProjectileDeflection.SIMPLE,hitentity,this.getOwner(),false);
            this.setVelocity(this.getVelocity().multiply(0.2D));
            if (!this.getWorld().isClient && this.getVelocity().lengthSquared() < 1.0E-7D) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
        this.remove(RemovalReason.DISCARDED);

    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("cmPierceLevel", this.getcustomPierceLevel());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
            super.initDataTracker(builder);
            builder.add(custom_PIERCE_LEVEL,(byte)0);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setcustomPierceLevel(nbt.getByte("cmPierceLevel"));
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getId()));
    }

}
