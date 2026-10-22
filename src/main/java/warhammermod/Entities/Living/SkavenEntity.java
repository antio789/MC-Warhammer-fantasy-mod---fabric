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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import warhammermod.Entities.Projectile.StoneEntity;
import warhammermod.Entities.Projectile.WarpBulletEntity;
import warhammermod.Items.ItemsInit;
import warhammermod.Items.ranged.RatlingGun;
import warhammermod.Items.ranged.SlingTemplate;
import warhammermod.Items.ranged.WarpgunTemplate;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkavenEntity extends Monster implements RangedAttackMob {

    public static final EntityDataAccessor<String> SkavenType = SynchedEntityData.defineId(SkavenEntity.class, EntityDataSerializers.STRING);
    private static final List<String> Types = List.of("slave", "clanrat", "stormvermin", "gutter_runner", "globadier", "ratling_gunner");
    private static final List<Float> SkavenSize = List.of(1F, 1.7F / 1.6F, 1.8F / 1.6F, 1.7F / 1.6F, 1.7F / 1.6F, 1.7F / 1.6F);
    private static final List<Integer> Spawnchance = List.of(38, 27, 15, 7, 4, 4);
    private static final List<Integer> firerate = List.of(28, 38, 0, 0, 55, 6);

    private final RangedAttackGoal bowGoal = new RangedAttackGoal(this, 1.0D, 20, 15.0F);
    private final RangedAttackGoal aiRangedPotion = new RangedAttackGoal(this, 1, 55, 10F);
    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, false);

    public SkavenEntity(EntityType<? extends SkavenEntity> type, Level level) {
        super(type, level);
        this.reassessWeaponGoal();
    }

    public void reassessWeaponGoal() {
        if (this.level() != null && !this.level().isClientSide()) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.bowGoal);
            this.goalSelector.removeGoal(this.aiRangedPotion);
            ItemStack item = this.getMainHandItem();
            int i = firerate.get(getSkavenTypePosition());
            if (item.getItem() instanceof WarpgunTemplate || item.getItem() instanceof RatlingGun || item.getItem() instanceof SlingTemplate) {
                this.goalSelector.addGoal(4, this.bowGoal);
            } else if (getSkaventype().equals("globadier")) {
                this.goalSelector.addGoal(4, aiRangedPotion);
            } else {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, SkavenEntity.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
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

    private void setSkavenType(String type) {
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

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SkavenType, Types.get(0));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData spawnData) {
        setSkavenType(getrandomtype());
        SpawnGroupData data = super.finalizeSpawn(world, difficulty, reason, spawnData);
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.reassessWeaponGoal();
        this.handleAttributes();
        updateSkavenEquipment();
        return data;
    }

    private void updateSkavenEquipment() {
        String type = getSkaventype();
        java.util.Random rand = new java.util.Random();
        if (type.equals("slave")) {
            this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, new ItemStack(ItemsInit.Sling));
        } else if (type.equals("clanrat")) {
            if (rand.nextBoolean()) {
                this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, new ItemStack(ItemsInit.Warplock_jezzail));
            } else {
                this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
                this.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, new ItemStack(ItemsInit.Skaven_shield));
            }
        } else if (type.equals("stormvermin")) {
            if (rand.nextInt(4) == 0) {
                this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, new ItemStack(ItemsInit.iron_halberd));
            } else {
                this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                this.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, new ItemStack(ItemsInit.Skaven_shield));
            }
        } else if (type.equals("gutter_runner")) {
            this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, new ItemStack(ItemsInit.iron_dagger));
        } else if (type.equals("ratling_gunner")) {
            this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, new ItemStack(ItemsInit.RatlingGun));
        }
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    public void handleAttributes() {
        double speed = 0.55;
        double max_health = 18;
        double armor = 0;
        double AD = 1.5;
        String type = getSkaventype();

        if (type.equals("slave")) {
            max_health = 14;
        } else if (type.equals("clanrat")) {
            AD = 2;
            max_health = 16;
            armor = 2;
        } else if (type.equals("stormvermin")) {
            AD = 3;
            max_health = 20;
            armor = 5;
        } else if (type.equals("gutter_runner")) {
            AD = 5;
            max_health = 17;
            speed = 0.75;
        }
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(max_health);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
        this.getAttribute(Attributes.ARMOR).setBaseValue(armor);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AD);
    }

    public static AttributeSupplier.Builder createMonsterAttributes() {
        return Monster.createMonsterAttributes();
    }

    public boolean isAffectedByPotions() {
        return !getSkaventype().equals("globadier");
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        net.minecraft.world.item.Item item = this.getMainHandItem().getItem();
        if (item instanceof WarpgunTemplate) {
            attackEntitywithrifle(target, 7, 11);
        } else if (item instanceof RatlingGun) {
            attackEntitywithrifle(target, 5, 26);
        } else if (item instanceof SlingTemplate) {
            attackEntitywithstone(target, 15);
        } else if (getSkaventype().equals("globadier")) {
            attackpotions(target);
        }
    }

    private void attackpotions(LivingEntity target) {
        var vec3d = target.getDeltaMovement();
        double d0 = target.getX() + vec3d.x - this.getX();
        double d1 = target.getEyeY() - 1.1F - this.getY();
        double d2 = target.getZ() + vec3d.z - this.getZ();
        double f = Math.sqrt(d0 * d0 + d2 * d2);
        Holder<Potion> potion = random.nextBoolean() ? Potions.HARMING : Potions.POISON;

        ThrownLingeringPotion potionentity = new ThrownLingeringPotion(this.level(), this, PotionContents.createItemStack(Items.LINGERING_POTION, potion));
        potionentity.shoot(d0, d1 + f * 0.2F, d2, 0.75F, 8.0F);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
        this.level().addFreshEntity(potionentity);
    }

    private void attackEntitywithrifle(LivingEntity target, int damage, float inaccuracy) {
        WarpBulletEntity bullet = new WarpBulletEntity(this, this.level(), damage);
        double d0 = target.getX() + 0.5D - this.getX();
        double d1 = target.getY() + 0.25D - this.getEyeY();
        double d2 = target.getZ() + 0.5D - this.getZ();
        bullet.shoot(d0, d1, d2, 1.6F, inaccuracy);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE.value(), this.getSoundSource(), 0.4F, 1.4F / (random.nextFloat() * 0.2F + 0.9F) + 0.4F);
        this.level().addFreshEntity(bullet);
    }

    private void attackEntitywithstone(LivingEntity target, float inaccuracy) {
        StoneEntity stoneentity = new StoneEntity(this, this.level(), 2.5F);
        double d0 = target.getX() + 0.5D - this.getX();
        double d1 = target.getY() + 0.5D - this.getEyeY();
        double d2 = target.getZ() + 0.5D - this.getZ();
        stoneentity.shoot(d0, d1, d2, 1.6F, inaccuracy);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARROW_SHOOT, this.getSoundSource(), 1.0F, 1.0F / (random.nextFloat() * 0.2F + 0.9F) + 0.4F);
        this.level().addFreshEntity(stoneentity);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putString("SkavenType", getSkaventype());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput in) {
        super.readAdditionalSaveData(in);
        String type = in.getString("SkavenType").orElse(Types.get(0));
        setSkavenType(type);
        this.refreshDimensions();
        this.handleAttributes();
        this.reassessWeaponGoal();
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

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

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
    }

    public static boolean checkSkavenSpawnRules(net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob> type, net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.world.entity.EntitySpawnReason reason, net.minecraft.core.BlockPos pos, net.minecraft.util.RandomSource random) {
        return net.minecraft.world.entity.monster.Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }
}
