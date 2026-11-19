package warhammermod.Entities.Living;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.tags.BiomeTags;
import org.jetbrains.annotations.Nullable;
import warhammermod.Entities.Living.AImanager.RifleAttackGoal;
import warhammermod.Entities.Living.AImanager.SkavenRangedAttackGoal;
import warhammermod.Entities.Living.AImanager.SkavenRangedUser;
import warhammermod.Entities.Projectile.StoneEntity;
import warhammermod.Entities.Projectile.WarpBulletEntity;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.IReloadItem;
import warhammermod.Items.ranged.RatlingGun;
import warhammermod.Items.ranged.SlingTemplate;
import warhammermod.Items.ranged.WarpgunTemplate;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.Items.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static warhammermod.utils.reference.*;

public class SkavenEntity extends PatrollingMonster implements SkavenRangedUser {

    public enum State {
        ATTACKING,
        AIMING,
        GUN_CHARGE,
        NEUTRAL;
    }

    public State getState() {
        if (this.isCharging()) {
            return State.GUN_CHARGE;
        } else if (this.isAggressive() && (this.getUseItem().getItem() instanceof RatlingGun || this.getUseItem().getItem() instanceof SlingTemplate)) {
            return State.AIMING;
        } else if (this.isAggressive()) {
            return State.ATTACKING;
        }
        return State.NEUTRAL;
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public boolean isaiming() {
        return this.entityData.get(AIMING);
    }

    private final SkavenRangedAttackGoal<SkavenEntity> SlingGoal = new SkavenRangedAttackGoal<>(this, 1.0D, 20, 15);
    private final SkavenRangedAttackGoal<SkavenEntity> RatlingGoal = new SkavenRangedAttackGoal<>(this, 1.0D, 20, 15, false);
    private final RangedAttackGoal aiRangedPotion = new RangedAttackGoal(this, 1, 55, 10F);
    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, false) {
        public void stop() {
            super.stop();
            setAggressive(false);
        }

        public void start() {
            super.start();
            setAggressive(true);
        }
    };

    public int getFirerate() {
        int i = 1;
        if (this.level().getDifficulty() != Difficulty.HARD) {
            i *= 1.5;
        }
        return (int) (firerate.get(getSkavenTypePosition()) * (double) i);
    }

    public void reassessWeaponGoal() {
        if (this.level() != null && !this.level().isClientSide()) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.SlingGoal);
            this.goalSelector.removeGoal(this.RatlingGoal);
            this.goalSelector.removeGoal(this.aiRangedPotion);
            ItemStack item = this.getMainHandItem();
            int i = firerate.get(getSkavenTypePosition());
            if (this.level().getDifficulty() != Difficulty.HARD) {
                i *= 1.5;
            }
            if (item.getItem() instanceof WarpgunTemplate) {
                this.goalSelector.addGoal(4, new RifleAttackGoal<SkavenEntity>(this, 1, 15));
            } else if (item.getItem() instanceof SlingTemplate) {
                this.SlingGoal.setAttackInterval(i);
                this.goalSelector.addGoal(4, this.SlingGoal);
            } else if (item.getItem() instanceof RatlingGun) {
                this.RatlingGoal.setAttackInterval(i * 3);
                this.goalSelector.addGoal(4, RatlingGoal);
            } else if (getSkaventype().equals(globadier)) {
                this.goalSelector.addGoal(4, aiRangedPotion);
            } else {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, SkavenEntity.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    /**
     * handle all of skaventypes
     */
    public static final EntityDataAccessor<String> SkavenType = SynchedEntityData.defineId(SkavenEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(SkavenEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AIMING = SynchedEntityData.defineId(SkavenEntity.class, EntityDataSerializers.BOOLEAN);

    private static final List<String> Types = List.of(slave, clanrat, stormvermin, gutter_runner, globadier, ratling_gunner);
    private static final List<Float> SkavenSize = List.of(1F, (1.7F / 1.6F), (1.8F / 1.6F), (1.7F / 1.6F), (1.7F / 1.6F), (1.7F / 1.6F));
    private static final List<Integer> Spawnchance = List.of(57, 33, 5, 2, 2, 1);
    private static final List<Float> reinforcementchance = List.of(0.08F, 0.1F, 0.14F, 0F, 0.08F, 0.11F);
    private static final List<Integer> firerate = List.of(28, 38, 0, 0, 55, 5);
    private static int Skaven_ranged_dropchance = 100;

    public static void setSkaven_ranged_dropchance(int skaven_ranged_dropchance) {
        Skaven_ranged_dropchance = skaven_ranged_dropchance;
    }

    public static int getSkaven_ranged_dropchance() {
        return Skaven_ranged_dropchance;
    }

    public ItemStack[][][] getSkavenEquipment() {
        return new ItemStack[][][]{
                {{}, {new ItemStack(ItemsInit.Sling)}},
                {{functions.getRandomsword(4, this.random), new ItemStack(ItemsInit.Skaven_shield)}, {new ItemStack(ItemsInit.Warplock_jezzail)}},
                {{new ItemStack(ItemsInit.stone_halberd)}, {functions.getRandomsword(5, this.random), new ItemStack(ItemsInit.Skaven_shield)}},
                {{new ItemStack(ItemsInit.iron_dagger), new ItemStack(ItemsInit.iron_dagger)}},
                {{}},
                {{new ItemStack(ItemsInit.RatlingGun)}},
        };
    }

    public String getSkaventype() {
        return entityData.get(SkavenType);
    }

    public int getSkavenTypePosition() {
        return Math.max(Types.indexOf(entityData.get(SkavenType)), 0);
    }

    public float getSkavenSize() {
        return SkavenSize.get(Math.max(Types.indexOf(getSkaventype()), 0));
    }

    private void updateSkavenSize() {
        this.refreshDimensions();
        handleAttributes();
    }

    public void setSkavenType(String type) {
        this.entityData.set(SkavenType, type);
    }

    private String getrandomtype() {
        int total = 0;
        for (int x : Spawnchance) {
            total += x;
        }
        int perc = random.nextInt(total);
        int chance = 0;
        for (int x = 0; x < Spawnchance.size(); x++) {
            chance += Spawnchance.get(x);
            if (perc < chance) {
                return Types.get(x);
            }
        }
        return Types.get(0);
    }

    /**
     * initializing
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SkavenType, Types.get(0));
        builder.define(CHARGING, false);
        builder.define(AIMING, false);
    }

    public SkavenEntity(EntityType<? extends SkavenEntity> type, Level level) {
        super(type, level);
        this.reassessWeaponGoal();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficultyIn, EntitySpawnReason reason, @Nullable SpawnGroupData spawnDataIn) {
        if (this.isPatrolLeader()) {
            setSkavenType(stormvermin);
        } else setSkavenType(getrandomtype());

        this.populateDefaultEquipmentSlots(random, difficultyIn);
        this.populateDefaultEquipmentEnchantments(world, random, difficultyIn);
        this.reassessWeaponGoal();
        this.handleAttributes();
        return super.finalizeSpawn(world, difficultyIn, reason, spawnDataIn);
    }

    /**
     * attributes
     */
    public void updateSkavenAttributes(double speed, double health, double armor, double AD) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
        this.getAttribute(Attributes.ARMOR).setBaseValue(armor);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AD);
    }

    public void handleAttributes() {
        double speed = 0.26;
        double max_health = 18;
        double armor = 0;
        double AD = 1.5;

        if (getSkaventype().equals(slave)) {
            max_health = 14;
            speed = 0.3;
        } else if (getSkaventype().equals(clanrat)) {
            AD = 2;
            max_health = 16;
            armor = 2;
        } else if (getSkaventype().equals(stormvermin)) {
            AD = 3;
            max_health = 20;
            armor = 5;
            speed = 0.25;
        } else if (getSkaventype().equals(gutter_runner)) {
            AD = 4;
            max_health = 14;
            speed = 0.5;
        }
        updateSkavenAttributes(speed, max_health, armor, AD);
    }

    public static AttributeSupplier.Builder createHostileAttributes() {
        return Monster.createMonsterAttributes();
    }

    /**
     * behaviour
     */
    public boolean isAffectedBySplashPotions() {
        return !getSkaventype().equals(globadier);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullprogress) {
        ItemStack stack = this.getMainHandItem();
        Item item = stack.getItem();
        if (item instanceof WarpgunTemplate) {
            attackEntitywithrifle(target, 7, 11, stack);
        } else if (item instanceof RatlingGun) {
            attackEntitywithrifle(target, 5, 26, stack);
        } else if (item instanceof SlingTemplate) {
            attackEntitywithstone(target, 15, stack);
        } else if (getSkaventype().equals(globadier)) {
            attackpotions(target);
        }
    }

    private void attackpotions(LivingEntity target) {
        var vec3d = target.getDeltaMovement();
        double d = target.getX() + vec3d.x - this.getX();
        double e = target.getEyeY() - 1.1F - this.getY();
        double f = target.getZ() + vec3d.z - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        Holder<Potion> potion = random.nextBoolean() ? Potions.HARMING : Potions.POISON;

        if (this.level() instanceof ServerLevel serverWorld) {
            ItemStack itemStack = PotionContents.createItemStack(Items.LINGERING_POTION, potion);
            ThrownLingeringPotion potionEntity = new ThrownLingeringPotion(serverWorld, this, itemStack);
            potionEntity.shoot(d, e + g * 0.2, f, 0.75F, 8.0F);
            serverWorld.addFreshEntity(potionEntity);
        }

        if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
        }
    }

    private void attackEntitywithrifle(LivingEntity target, int damage, float inaccuracy, ItemStack stack) {
        WarpBulletEntity bullet = new WarpBulletEntity(this, this.level(), (float) damage, ItemStack.EMPTY);
        int j = ModEnchantmentHelper.getLevel(level(), stack, Enchantments.POWER);
        if (j > 0) {
            bullet.setpowerDamage(j);
        }
        int k = ModEnchantmentHelper.getLevel(level(), stack, Enchantments.PUNCH) + 1;
        if (k > 0) {
            bullet.setknockbacklevel(k);
        }
        bullet.setPos(this.getX(), this.getEyeY() - 0.1f, this.getZ());
        bullet.setOwner(this);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY() + 0.3333333333333333D - bullet.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        bullet.shoot(d0, d1 + d3 * 0.06, d2, 3, (float) (inaccuracy - this.level().getDifficulty().getId() * 4));

        this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 2F / (random.nextFloat() * 0.4F + 1.2F) + 0.5F);
        this.level().addFreshEntity(bullet);
    }

    private void attackEntitywithstone(LivingEntity target, float inaccuracy, ItemStack stack) {
        StoneEntity stone = new StoneEntity(this, this.level(), 3, ItemStack.EMPTY);
        stone.setPos(this.getX(), this.getEyeY() - 0.1f, this.getZ());
        int j = ModEnchantmentHelper.getLevel(level(), stack, Enchantments.POWER);
        if (j > 0) {
            stone.setTotaldamage(stone.projectiledamage + (float) j * 0.5F + 0.5F);
        }
        int k = ModEnchantmentHelper.getLevel(level(), stack, Enchantments.PUNCH) + 1;
        if (k > 0) {
            stone.setknockbacklevel(k);
        }
        if (ModEnchantmentHelper.getLevel(level(), stack, Enchantments.FLAME) > 0) {
            stone.igniteForSeconds(100);
        }
        stone.setOwner(this);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY() + 0.3333333333333333D - stone.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        stone.shoot(d0, d1 + d3 * 0.2, d2, 1.3F, (float) (inaccuracy - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F));
        this.level().addFreshEntity(stone);
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (!super.hurtServer(world, source, amount)) {
            return false;
        } else {
            LivingEntity livingentity = this.getTarget();
            if (livingentity == null && source.getEntity() instanceof LivingEntity) {
                livingentity = (LivingEntity) source.getEntity();
            }

            if (livingentity != null && this.level().getDifficulty() == Difficulty.HARD && (double) this.random.nextFloat() < reinforcementchance.get(getSkavenTypePosition()) / 2 && world.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.SPAWN_MOBS)) {
                SkavenEntity skavenEntity = Entityinit.SKAVEN.create(world, EntitySpawnReason.REINFORCEMENT);
                int i = Mth.floor(this.getX());
                int j = Mth.floor(this.getY());
                int k = Mth.floor(this.getZ());

                for (int l = 0; l < 50; ++l) {
                    int i1 = i + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    int j1 = j + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    int k1 = k + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    BlockPos blockpos = new BlockPos(i1, j1, k1);
                    EntityType<?> entityType = skavenEntity.getType();
                    if (!net.minecraft.world.entity.SpawnPlacements.isSpawnPositionOk(entityType, this.level(), blockpos) || !net.minecraft.world.entity.SpawnPlacements.checkSpawnRules(entityType, world, EntitySpawnReason.REINFORCEMENT, blockpos, this.random))
                        continue;
                    skavenEntity.setPos(i1, j1, k1);
                    if (this.level().hasChunkAt(blockpos) && this.level().noCollision(skavenEntity))
                        continue;
                    skavenEntity.setTarget(livingentity);
                    skavenEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(skavenEntity.blockPosition()), EntitySpawnReason.REINFORCEMENT, null);
                    world.addFreshEntityWithPassengers(skavenEntity);
                    break;
                }
            }

            return true;
        }
    }

    /**
     * other
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return WHRegistry.ratambient;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return WHRegistry.rathurt;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return WHRegistry.ratdeath;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    public float getSoundPitch() {
        return 0.4F + this.random.nextFloat() * 0.5F;
    }

    @Override
    protected void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource random, DifficultyInstance difficulty) {
        int typepos = Math.max(Types.indexOf(getSkaventype()), 0);
        ItemStack[][][] equipment = getSkavenEquipment();
        int weapontype;
        if (equipment[typepos].length < 2) {
            weapontype = 0;
        } else {
            weapontype = this.random.nextInt(2);
        }
        if (equipment[typepos][weapontype].length > 0) {
            this.setItemSlot(EquipmentSlot.MAINHAND, equipment[typepos][weapontype][0]);
            if (equipment[typepos][weapontype].length > 1) {
                this.setItemSlot(EquipmentSlot.OFFHAND, equipment[typepos][weapontype][1]);
            }
        }
        Item item = getMainHandItem().getItem();
        if (item instanceof RatlingGun) {
            getMainHandItem().set(WHRegistry.AMMO, new Ammocomponent(random.nextInt(65), 64));
        }
        if (item instanceof IReloadItem) {
            this.setDropChance(EquipmentSlot.MAINHAND, 1.0f);
        }
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putString("SkavenType", this.getSkaventype());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput in) {
        super.readAdditionalSaveData(in);
        setSkavenType(in.getString("SkavenType").orElse(Types.get(0)));
        this.reassessWeaponGoal();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (SkavenType.equals(key)) {
            this.updateSkavenSize();
        }
        super.onSyncedDataUpdated(key);
    }

    public static boolean checkSkavenSpawnRules(EntityType<SkavenEntity> type, ServerLevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (world.getBiome(pos).is(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS) && pos.getY() > 50 && pos.getY() < 70 && random.nextFloat() < 0.5f) {
            return Monster.checkMonsterSpawnRules(type, world, spawnReason, pos, random);
        } else if (pos.getY() < 60) return Monster.checkMonsterSpawnRules(type, world, spawnReason, pos, random);
        return false;
    }

    @Override
    public boolean isBlocking() {
        if (this.getOffhandItem().getItem() instanceof net.minecraft.world.item.ShieldItem && getTarget() != null && this.random.nextFloat() < 0.25) {
            playSound(SoundEvents.SHIELD_BLOCK.value(), 1, 1);
            return true;
        } else return false;
    }

    @Override
    public void setChargingCrossbow(boolean charging) {
        this.entityData.set(CHARGING, charging);
    }

    @Override
    public void setAiming(boolean aiming) {
        this.entityData.set(AIMING, aiming);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.setNoActionTime(0);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
