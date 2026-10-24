package warhammermod.Entities.Living;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.functions;
import warhammermod.utils.reference;

import java.util.ArrayList;
import java.util.Arrays;

import static warhammermod.utils.reference.*;


public class SkavenEntity extends PatrollingMonster implements SkavenRangedUser {

    /**
     * goals
     */

    public enum State {
        ATTACKING,
        AIMING,
        GUN_CHARGE,
        NEUTRAL;

    }

    public State getState() {
        if (this.isCharging()) {
            return State.GUN_CHARGE;
        }
        else if (this.isAggressive() && this.getUseItem().getItem() instanceof RatlingGun || this.getUseItem().getItem() instanceof SlingTemplate){
            return State.AIMING;
        }else if (this.isAggressive()) {
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

    private final SkavenRangedAttackGoal<SkavenEntity> SlingGoal = new SkavenRangedAttackGoal<>(this, 1.0D, 20,15);
    private final SkavenRangedAttackGoal<SkavenEntity> RatlingGoal = new SkavenRangedAttackGoal<>(this, 1.0D, 20,15,false);
    private final RangedAttackGoal aiRangedPotion = new RangedAttackGoal(this,1,55,10F);
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

    public int getFirerate(){
        int i=1;
        if (this.level().getDifficulty() != Difficulty.HARD) {
            i *= 1.5;
        }
       return firerate.get(getSkavenTypePosition())*i;
    }

    public void reassessWeaponGoal() {
        if (this.level() != null && !this.level().isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.SlingGoal);
            this.goalSelector.removeGoal(this.aiRangedPotion);
            ItemStack item = this.getMainHandItem();
            int i = firerate.get(getSkavenTypePosition());
            if (this.level().getDifficulty() != Difficulty.HARD) {
                i *= 1.5;
            }
            if(item.getItem() instanceof WarpgunTemplate){
                this.goalSelector.addGoal(4,new RifleAttackGoal<SkavenEntity>(this,1,15));
            }
            else if ( item.getItem() instanceof SlingTemplate) {
                this.SlingGoal.setAttackInterval(i);
                this.goalSelector.addGoal(4, this.SlingGoal);
            } else if(item.getItem() instanceof RatlingGun){
                this.RatlingGoal.setAttackInterval(i*3);
                this.goalSelector.addGoal(4,RatlingGoal);
            } else if(getSkaventype().equals(globadier)){
                this.goalSelector.addGoal(4,aiRangedPotion);
            }
            else{
                this.goalSelector.addGoal(4, this.meleeGoal);
            }

        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        //this.goalSelector.add(3, new CrossbowAttackGoal<PillagerEntity>(this, 1.0, 8.0f));
        //this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
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
     *
     */
    public static final EntityDataAccessor<String> SkavenType = SynchedEntityData.defineId(SkavenEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(SkavenEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AIMING = SynchedEntityData.defineId(SkavenEntity.class, EntityDataSerializers.BOOLEAN);

    private static final ArrayList<String> Types = new ArrayList<String>(Arrays.asList(slave,clanrat,stormvermin,gutter_runner,globadier,ratling_gunner));
    private final ArrayList<Float> SkavenSize = new ArrayList<Float>(Arrays.asList(1F,(1.7F/1.6F),(1.8F/1.6F),(1.7F/1.6F),(1.7F/1.6F),(1.7F/1.6F)));
    private final ArrayList<Integer> Spawnchance = new ArrayList<Integer>(Arrays.asList(57,33,5,2,2,1));
    private final ArrayList<Float> reinforcementchance = new ArrayList<Float>(Arrays.asList(0.08F,0.1F,0.14F,0F,0.08F,0.11F));
    private final ArrayList<Integer> firerate = new ArrayList<Integer>(Arrays.asList(28,38,0,0,55,5));
    public static ItemStack[][][] SkavenEquipment =  {{{functions.getRandomspear(4)},{new ItemStack(ItemsInit.Sling)}},
            {{functions.getRandomsword(4),new ItemStack(ItemsInit.Skaven_shield)},{new ItemStack(ItemsInit.Warplock_jezzail)}},
            {{functions.getRandomHalberd(4)},{functions.getRandomsword(5),new ItemStack(ItemsInit.Skaven_shield)}},
            {{new ItemStack(ItemsInit.iron_dagger),new ItemStack(ItemsInit.iron_dagger)}},
            {{}},
            {{new ItemStack(ItemsInit.RatlingGun)}},
    };
    private static int Skaven_ranged_dropchance = 100;
    public static void setSkaven_ranged_dropchance(int skaven_ranged_dropchance) {
        Skaven_ranged_dropchance = skaven_ranged_dropchance;
    }
    public static int getSkaven_ranged_dropchance() {
        return Skaven_ranged_dropchance;
    }




    public String getSkaventype(){
        return entityData.get(SkavenType);
    }
    public int getSkavenTypePosition(){
        int typepos = Math.max(Types.indexOf(entityData.get(SkavenType)),0);
        return typepos;
    }
    public float getSkavenSize(){
        int typepos = Math.max(Types.indexOf(getSkaventype()),0);
        return SkavenSize.get(typepos);
    }
    public float getAgeScale() {
        return this.isBaby() ? 0.5f : getSkavenSize();
    }

    private void updateSkavenSize(){
        this.refreshDimensions();
        handleAttributes();
    }

    public void setSkavenType(String type){
        this.entityData.set(SkavenType,type);
    }

    private String getrandomtype(){
        int total=0;
        for(int x:Spawnchance){
            total+=x;
        }
        int perc = random.nextInt(total);
        int chance=0;
        for(int x=0;x<Spawnchance.size();x++){
            chance+=Spawnchance.get(x);
            if(perc<chance){
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
        builder.define(CHARGING,false);
        builder.define(AIMING,false);
    }


    public SkavenEntity(EntityType<? extends SkavenEntity> p_i48555_1_, Level p_i48555_2_) {
        super(p_i48555_1_, p_i48555_2_);
        this.reassessWeaponGoal();

    }
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
        if(this.isPatrolLeader()){setSkavenType(stormvermin);
        } else setSkavenType(getrandomtype());

        this.initEquipment(difficultyIn);
        this.populateDefaultEquipmentEnchantments(world,random,difficultyIn);
        this.reassessWeaponGoal();
        this.handleAttributes();
        return super.finalizeSpawn(world, difficultyIn, reason, spawnDataIn);
    }

        /**
         * attributes
         */
    public void updateSkavenAttributes(double speed,double health,double armor,double AD){
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
        this.getAttribute(Attributes.ARMOR).setBaseValue(armor);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AD);
    }
    public void handleAttributes(){
        double speed = 0.26;
        double max_health = 18;
        double armor=0;
        double AD=1.5;

        if(getSkaventype().equals(slave)){
            max_health=14;
            speed=0.3;
        }
        else if(getSkaventype().equals(clanrat)){
            AD=2;
            max_health=16;
            armor=2;
        }
        else if(getSkaventype().equals(stormvermin)){
            AD=3;
            max_health=20;
            armor=5;
            speed=0.25;
        }else if(getSkaventype().equals(gutter_runner)){
            AD=4;
            max_health=14;
            speed=0.5;
        }
        updateSkavenAttributes(speed,max_health,armor,AD);
    };

    public static AttributeSupplier.Builder createMonsterAttributes()  {
        return Monster.createMonsterAttributes();
    }
    /**
     * behaviour
     */
    public boolean isAffectedByPotions() {
        return !getSkaventype().equals(globadier);

    }

    public void attack(LivingEntity target, float pullprogress) {
        ItemStack stack = this.getMainHandItem();
        Item item = stack.getItem();
        if(item instanceof WarpgunTemplate){
            attackEntitywithrifle(target,7,11,stack);
        }
        else if(item instanceof RatlingGun){
            attackEntitywithrifle(target,5,26,stack);
        }
        else if(item instanceof SlingTemplate){
            attackEntitywithstone(target,15,stack);
        }
        else if(getSkaventype().equals(globadier)){
            attackpotions(target);
        }
    }
    private void attackpotions(LivingEntity target){
        Vec3 vec3d = target.getDeltaMovement();
        double d0 = target.getX() + vec3d.x - this.getX();
        double d1 = target.getEyeY() - (double)1.1F - this.getY();
        double d2 = target.getZ() + vec3d.z - this.getZ();
        double f = Math.sqrt(d0 * d0 + d2 * d2);
        Holder<Potion> registryEntry = random.nextBoolean() ? Potions.HARMING : Potions.POISON;

        ThrownPotion potionentity = new ThrownPotion(this.level(), this);
        potionentity.setItem(PotionContents.createItemStack(Items.LINGERING_POTION, registryEntry));
        potionentity.setXRot(potionentity.getXRot() - -20.0F);
        potionentity.shoot(d0, d1 + (double)(f * 0.2F), d2, 0.75F, 8.0F);
        if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
        }
        this.level().addFreshEntity(potionentity);

    }
    private void attackEntitywithrifle(LivingEntity target, int damage, float inaccuracy,ItemStack stack){
        System.out.println("firing");
        WarpBulletEntity bullet = new WarpBulletEntity(this,this.level(),damage,stack);
        int j = ModEnchantmentHelper.getLevel(level(),stack,Enchantments.POWER);
        if (j > 0) {
            bullet.setpowerDamage(j);
        }
        int k = ModEnchantmentHelper.getLevel(level(),stack,Enchantments.PUNCH) + 1;
        if (k > 0) {
            bullet.setknockbacklevel(k);
        }
        bullet.setPos(this.getX(), this.getEyeY()-0.1f, this.getZ());
        bullet.setOwner(this);
        double d0 = target.getX()  - this.getX();
        double d1 = target.getY(0.3333333333333333D) - bullet.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        bullet.shoot(d0,d1+d3*0.06,d2,3,(float)(inaccuracy - this.level().getDifficulty().getId() * 4));//inac14


        this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 2F / (random.nextFloat() * 0.4F + 1.2F) + 0.5F);
        this.level().addFreshEntity(bullet);

    }

    private void attackEntitywithstone(LivingEntity target, float inaccuracy,ItemStack stack){
        System.out.println("attacking");
        StoneEntity stone = new StoneEntity(this,level(),3,stack);
        stone.setPos(this.getX(), this.getEyeY()-0.1f, this.getZ());
        int j = ModEnchantmentHelper.getLevel(level(),stack,Enchantments.POWER);
        if (j > 0) {
            stone.setTotaldamage(stone.projectiledamage + (float)j * 0.5F + 0.5F);
        }
        int k = ModEnchantmentHelper.getLevel(level(),stack,Enchantments.PUNCH) + 1;
        if (k > 0) {
            stone.setknockbacklevel(k);
        }
        if (ModEnchantmentHelper.getLevel(level(),stack,Enchantments.FLAME) > 0) {
            stone.igniteForSeconds(100);
        }
        stone.setOwner(this);
        double d0 = target.getX()  - this.getX();
        double d1 = target.getY(0.3333333333333333D) - stone.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = (double) Math.sqrt(d0 * d0 + d2 * d2);
        stone.shoot(d0,d1+d3*0.2,d2,1.3F,(float)(inaccuracy - this.level().getDifficulty().getId() * 4));
        this.playSound( SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F));
        this.level().addFreshEntity(stone);

    }

    public boolean hurt(DamageSource source, float amount){
        if (!super.hurt(source, amount)) {
            return false;
        } else if (!(this.level() instanceof ServerLevel serverworld)) {
            return false;
        } else {
            LivingEntity livingentity = this.getTarget();
            if (livingentity == null && source.getEntity() instanceof LivingEntity) {
                livingentity = (LivingEntity)source.getEntity();
            }




            if (livingentity != null && this.level().getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < reinforcementchance.get(getSkavenTypePosition())/2 && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                SkavenEntity skavenEntity = Entityinit.SKAVEN.create(this.level());
                int i = Mth.floor(this.getX());
                int j = Mth.floor(this.getY());
                int k = Mth.floor(this.getZ());

                for(int l = 0; l < 50; ++l) {
                    int i1 = i + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    int j1 = j + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    int k1 = k + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                    BlockPos blockpos = new BlockPos(i1, j1, k1);
                    EntityType<?> entityType = skavenEntity.getType();
                    if (!SpawnPlacements.isSpawnPositionOk(entityType, this.level(), blockpos) || !SpawnPlacements.checkSpawnRules(entityType, serverworld, MobSpawnType.REINFORCEMENT, blockpos, this.level().random)) continue;
                    skavenEntity.setPos(i1, j1, k1);
                    if (this.level().hasNearbyAlivePlayer(i1, j1, k1, 7.0) || !this.level().isUnobstructed(skavenEntity) || !this.level().noCollision(skavenEntity) || this.level().containsAnyLiquid(skavenEntity.getBoundingBox())) continue;
                    skavenEntity.setTarget(livingentity);
                    skavenEntity.finalizeSpawn(serverworld, this.level().getCurrentDifficultyAt(skavenEntity.blockPosition()), MobSpawnType.REINFORCEMENT, null);
                    serverworld.addFreshEntityWithPassengers(skavenEntity);
                    break;
                }
            }

            return true;
        }
    }
    /**
     * other
     */
    protected SoundEvent getAmbientSound() {
        return WHRegistry.ratambient;
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return WHRegistry.rathurt;
    }

    protected SoundEvent getDeathSound() {
        return WHRegistry.ratdeath;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }
    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }
    public float getVoicePitch()
    {
        return 0.4F +this.random.nextFloat()*0.5F;
    }

    protected void initEquipment(DifficultyInstance difficulty)//add dropchance
    {
        int typepos = Math.max(Types.indexOf(getSkaventype()),0);
        int weapontype;
        if(SkavenEquipment[typepos].length<2){
            weapontype = 0;
        }
        else {
            weapontype = this.random.nextInt(2);
        }
        if(SkavenEquipment[typepos][weapontype].length>0){
            this.setItemSlot(EquipmentSlot.MAINHAND,SkavenEquipment[typepos][weapontype][0]);
            if(SkavenEquipment[typepos][weapontype].length>1){
                this.setItemSlot(EquipmentSlot.OFFHAND,SkavenEquipment[typepos][weapontype][1]);
            }
        }
        Item item = getMainHandItem().getItem();
        if(item instanceof RatlingGun gun){getMainHandItem().set(WHRegistry.AMMO,new Ammocomponent( random.nextIntBetweenInclusive(0,64),64));}
        if(item instanceof IReloadItem){
            this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 1.0f;
        }
    }
    /*
    protected void updateEnchantments(LocalDifficulty difficulty) {
        float f = difficulty.getClampedLocalDifficulty();
        if(this.random.nextFloat() > 0.5F) this.enchantMainHandItem(f);
    }
*/
    public void addAdditionalSaveData(CompoundTag compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putString("SkavenType", this.getSkaventype());

    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setSkavenType(compound.getString("SkavenType"));
        this.reassessWeaponGoal();
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (SkavenType.equals(key)) {
            this.updateSkavenSize();
        }
        super.onSyncedDataUpdated(key);
    }

    public static boolean canSpawn(EntityType<SkavenEntity> type, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        if (world.getBiome(pos).is(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS) && pos.getY() > 50 && pos.getY() < 70 && random.nextFloat() < 0.5f) {
            return Monster.checkMonsterSpawnRules(type,world,spawnReason,pos,random);
        } else if(pos.getY()<60) return Monster.checkMonsterSpawnRules(type,world,spawnReason,pos,random);
        return false;
    }

    protected float getEquipmentDropChance(EquipmentSlot slot) {
        float f;
        switch (slot.getType()) {
            case HAND -> {
                f = this.handDropChances[slot.getIndex()];
                if (this.getMainHandItem().getItem() instanceof IReloadItem) f = 1;
            }
            case HUMANOID_ARMOR -> f = this.armorDropChances[slot.getIndex()];
            default -> f = 0.0F;
        }

        return f;
    }


    @Override
    public boolean isBlocking() {
        if(this.getOffhandItem().getItem() instanceof ShieldItem && getTarget()!=null && this.random.nextFloat()<0.25){
            playSound(SoundEvents.SHIELD_BLOCK,1,1);
            return true;
        }
        else return false;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        ItemStack stack = this.getMainHandItem();
        Item item = stack.getItem();
        if(item instanceof WarpgunTemplate){
            attackEntitywithrifle(target,7,11,stack);
        }
        else if(item instanceof RatlingGun){
            attackEntitywithrifle(target,5,26,stack);
        }
        else if(item instanceof SlingTemplate){
            attackEntitywithstone(target,15,stack);
        }
        else if(getSkaventype().equals(globadier)){
            attackpotions(target);
        }
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
        this.noActionTime = 0;
    }

    @Override
    public void tick() {
        super.tick();

    }
}
