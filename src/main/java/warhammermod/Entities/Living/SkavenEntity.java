package warhammermod.Entities.Living;


import net.minecraft.block.BlockState;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
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

import java.util.ArrayList;
import java.util.Arrays;

import static warhammermod.utils.reference.*;


public class SkavenEntity extends PatrolEntity implements SkavenRangedUser {

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
        else if (this.isAttacking() && this.getActiveItem().getItem() instanceof RatlingGun || this.getActiveItem().getItem() instanceof SlingTemplate){
            return State.AIMING;
        }else if (this.isAttacking()) {
            return State.ATTACKING;
        }
        return State.NEUTRAL;
    }

    public boolean isCharging() {
        return this.dataTracker.get(CHARGING);
    }
    public boolean isaiming() {
        return this.dataTracker.get(AIMING);
    }

    private final SkavenRangedAttackGoal<SkavenEntity> SlingGoal = new SkavenRangedAttackGoal<>(this, 1.0D, 20,15);
    private final SkavenRangedAttackGoal<SkavenEntity> RatlingGoal = new SkavenRangedAttackGoal<>(this, 1.0D, 20,15,false);
    private final ProjectileAttackGoal aiRangedPotion = new ProjectileAttackGoal(this,1,55,10F);
    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, false) {
        public void stop() {
            super.stop();
            setAttacking(false);
        }

        public void start() {
            super.start();
            setAttacking(true);
        }
    };

    public int getFirerate(){
        int i=1;
        if (this.getWorld().getDifficulty() != Difficulty.HARD) {
            i *= 1.5;
        }
       return firerate.get(getSkavenTypePosition())*i;
    }

    public void reassessWeaponGoal() {
        if (this.getWorld() != null && !this.getWorld().isClient) {
            this.goalSelector.remove(this.meleeGoal);
            this.goalSelector.remove(this.SlingGoal);
            this.goalSelector.remove(this.aiRangedPotion);
            ItemStack item = this.getMainHandStack();
            int i = firerate.get(getSkavenTypePosition());
            if (this.getWorld().getDifficulty() != Difficulty.HARD) {
                i *= 1.5;
            }
            if(item.getItem() instanceof WarpgunTemplate){
                this.goalSelector.add(4,new RifleAttackGoal<SkavenEntity>(this,1,15));
            }
            else if ( item.getItem() instanceof SlingTemplate) {
                this.SlingGoal.setAttackInterval(i);
                this.goalSelector.add(4, this.SlingGoal);
            } else if(item.getItem() instanceof RatlingGun){
                this.RatlingGoal.setAttackInterval(i*3);
                this.goalSelector.add(4,RatlingGoal);
            } else if(getSkaventype().equals(globadier)){
                this.goalSelector.add(4,aiRangedPotion);
            }
            else{
                this.goalSelector.add(4, this.meleeGoal);
            }

        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        //this.goalSelector.add(3, new CrossbowAttackGoal<PillagerEntity>(this, 1.0, 8.0f));
        //this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 15.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 15.0f));
        this.targetSelector.add(1, new RevengeGoal(this, SkavenEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }

    /**
     * handle all of skaventypes
     *
     */
    public static final TrackedData<String> SkavenType = DataTracker.registerData(SkavenEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(SkavenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> AIMING = DataTracker.registerData(SkavenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

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
        return dataTracker.get(SkavenType);
    }
    public int getSkavenTypePosition(){
        int typepos = Math.max(Types.indexOf(dataTracker.get(SkavenType)),0);
        return typepos;
    }
    public float getSkavenSize(){
        int typepos = Math.max(Types.indexOf(getSkaventype()),0);
        return SkavenSize.get(typepos);
    }
    public float getScaleFactor() {
        return this.isBaby() ? 0.5f : getSkavenSize();
    }

    private void updateSkavenSize(){
        this.calculateDimensions();
        handleAttributes();
    }

    public void setSkavenType(String type){
        this.dataTracker.set(SkavenType,type);
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
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SkavenType, Types.get(0));
        builder.add(CHARGING,false);
        builder.add(AIMING,false);
    }


    public SkavenEntity(EntityType<? extends SkavenEntity> p_i48555_1_, World p_i48555_2_) {
        super(p_i48555_1_, p_i48555_2_);
        this.reassessWeaponGoal();

    }
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficultyIn, SpawnReason reason, @Nullable EntityData spawnDataIn) {
        if(this.isPatrolLeader()){setSkavenType(stormvermin);
        } else setSkavenType(getrandomtype());

        this.initEquipment(difficultyIn);
        this.updateEnchantments(world,random,difficultyIn);
        this.reassessWeaponGoal();
        this.handleAttributes();
        return super.initialize(world, difficultyIn, reason, spawnDataIn);
    }

        /**
         * attributes
         */
    public void updateSkavenAttributes(double speed,double health,double armor,double AD){
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(health);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(speed);
        this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(armor);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(AD);
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

    public static DefaultAttributeContainer.Builder createHostileAttributes()  {
        return HostileEntity.createHostileAttributes();
    }
    /**
     * behaviour
     */
    public boolean isAffectedBySplashPotions() {
        return !getSkaventype().equals(globadier);

    }

    public void attack(LivingEntity target, float pullprogress) {
        ItemStack stack = this.getMainHandStack();
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
        Vec3d vec3d = target.getVelocity();
        double d = target.getX() + vec3d.x - this.getX();
        double e = target.getEyeY() - (double)1.1F - this.getY();
        double f = target.getZ() + vec3d.z - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        RegistryEntry<Potion> registryEntry = random.nextBoolean() ? Potions.HARMING : Potions.POISON;


        if (this.getWorld() instanceof ServerWorld serverWorld) {
            ItemStack itemStack = PotionContentsComponent.createStack(Items.SPLASH_POTION, registryEntry);
            ProjectileEntity.spawnWithVelocity(PotionEntity::new, serverWorld, itemStack, this, d, e + g * 0.2, g, 0.75F, 8.0F);
        }

        if (!this.isSilent()) {
            this.getWorld()
                    .playSound(
                            null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F
                    );
        }

    }
    private void attackEntitywithrifle(LivingEntity target, int damage, float inaccuracy,ItemStack stack){
        System.out.println("firing");
        WarpBulletEntity bullet = new WarpBulletEntity(this,this.getWorld(),damage,stack);
        int j = ModEnchantmentHelper.getLevel(getWorld(),stack,Enchantments.POWER);
        if (j > 0) {
            bullet.setpowerDamage(j);
        }
        int k = ModEnchantmentHelper.getLevel(getWorld(),stack,Enchantments.PUNCH) + 1;
        if (k > 0) {
            bullet.setknockbacklevel(k);
        }
        bullet.setPosition(this.getX(), this.getEyeY()-0.1f, this.getZ());
        bullet.setOwner(this);
        double d0 = target.getX()  - this.getX();
        double d1 = target.getBodyY(0.3333333333333333D) - bullet.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        bullet.setVelocity(d0,d1+d3*0.06,d2,3,(float)(inaccuracy - this.getWorld().getDifficulty().getId() * 4));//inac14


        this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 1.0F, 2F / (random.nextFloat() * 0.4F + 1.2F) + 0.5F);
        this.getWorld().spawnEntity(bullet);

    }

    private void attackEntitywithstone(LivingEntity target, float inaccuracy,ItemStack stack){
        System.out.println("attacking");
        StoneEntity stone = new StoneEntity(this,getWorld(),3,stack);
        stone.setPosition(this.getX(), this.getEyeY()-0.1f, this.getZ());
        int j = ModEnchantmentHelper.getLevel(getWorld(),stack,Enchantments.POWER);
        if (j > 0) {
            stone.setTotaldamage(stone.projectiledamage + (float)j * 0.5F + 0.5F);
        }
        int k = ModEnchantmentHelper.getLevel(getWorld(),stack,Enchantments.PUNCH) + 1;
        if (k > 0) {
            stone.setknockbacklevel(k);
        }
        if (ModEnchantmentHelper.getLevel(getWorld(),stack,Enchantments.FLAME) > 0) {
            stone.setOnFireFor(100);
        }
        stone.setOwner(this);
        double d0 = target.getX()  - this.getX();
        double d1 = target.getBodyY(0.3333333333333333D) - stone.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = (double) Math.sqrt(d0 * d0 + d2 * d2);
        stone.setVelocity(d0,d1+d3*0.2,d2,1.3F,(float)(inaccuracy - this.getWorld().getDifficulty().getId() * 4));
        this.playSound( SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F));
        this.getWorld().spawnEntity(stone);

    }

    public boolean damage(ServerWorld world,DamageSource source, float amount){
        if (!super.damage(world, source, amount)) {
            return false;
        } else {
            LivingEntity livingentity = this.getTarget();
            if (livingentity == null && source.getAttacker() instanceof LivingEntity) {
                livingentity = (LivingEntity)source.getAttacker();
            }




            if (livingentity != null && this.getWorld().getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < reinforcementchance.get(getSkavenTypePosition())/2 && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                SkavenEntity skavenEntity = Entityinit.SKAVEN.create(world,SpawnReason.REINFORCEMENT);
                int i = MathHelper.floor(this.getX());
                int j = MathHelper.floor(this.getY());
                int k = MathHelper.floor(this.getZ());

                for(int l = 0; l < 50; ++l) {
                    int i1 = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int j1 = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int k1 = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    BlockPos blockpos = new BlockPos(i1, j1, k1);
                    EntityType<?> entityType = skavenEntity.getType();
                    if (!SpawnRestriction.isSpawnPosAllowed(entityType, this.getWorld(), blockpos) || !SpawnRestriction.canSpawn(entityType, world, SpawnReason.REINFORCEMENT, blockpos, this.getWorld().random)) continue;
                    skavenEntity.setPosition(i1, j1, k1);
                    if (this.getWorld().isPlayerInRange(i1, j1, k1, 7.0) || !this.getWorld().doesNotIntersectEntities(skavenEntity) || !this.getWorld().isSpaceEmpty(skavenEntity) || this.getWorld().containsFluid(skavenEntity.getBoundingBox())) continue;
                    skavenEntity.setTarget(livingentity);
                    skavenEntity.initialize(world, this.getWorld().getLocalDifficulty(skavenEntity.getBlockPos()), SpawnReason.REINFORCEMENT, null);
                    world.spawnEntityAndPassengers(skavenEntity);
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
        return SoundEvents.ENTITY_ZOMBIE_STEP;
    }
    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }
    public float getSoundPitch()
    {
        return 0.4F +this.random.nextFloat()*0.5F;
    }

    protected void initEquipment(LocalDifficulty difficulty)//add dropchance
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
            this.equipStack(EquipmentSlot.MAINHAND,SkavenEquipment[typepos][weapontype][0]);
            if(SkavenEquipment[typepos][weapontype].length>1){
                this.equipStack(EquipmentSlot.OFFHAND,SkavenEquipment[typepos][weapontype][1]);
            }
        }
        Item item = getMainHandStack().getItem();
        if(item instanceof RatlingGun gun){getMainHandStack().set(WHRegistry.AMMO,new Ammocomponent( random.nextBetween(0,64),64));}
        if(item instanceof IReloadItem){
            this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 1.0f;
        }
    }
    /*
    protected void updateEnchantments(LocalDifficulty difficulty) {
        float f = difficulty.getClampedLocalDifficulty();
        if(this.random.nextFloat() > 0.5F) this.enchantMainHandItem(f);
    }
*/
    public void writeCustomDataToNbt(NbtCompound compoundNBT) {
        super.writeCustomDataToNbt(compoundNBT);
        compoundNBT.putString("SkavenType", this.getSkaventype());

    }

    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        setSkavenType(compound.getString("SkavenType"));
        this.reassessWeaponGoal();
    }

    public void onTrackedDataSet(TrackedData<?> key) {
        if (SkavenType.equals(key)) {
            this.updateSkavenSize();
        }
        super.onTrackedDataSet(key);
    }

    public static boolean canSpawn(EntityType<SkavenEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        if (world.getBiome(pos).isIn(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS) && pos.getY() > 50 && pos.getY() < 70 && random.nextFloat() < 0.5f) {
            return HostileEntity.canSpawnInDark(type,world,spawnReason,pos,random);
        } else if(pos.getY()<60) return HostileEntity.canSpawnInDark(type,world,spawnReason,pos,random);
        return false;
    }

    protected float getDropChance(EquipmentSlot slot) {
        float f;
        switch (slot.getType()) {
            case HAND -> {
                f = this.handDropChances[slot.getEntitySlotId()];
                if (this.getMainHandStack().getItem() instanceof IReloadItem) f = 1;
            }
            case HUMANOID_ARMOR -> f = this.armorDropChances[slot.getEntitySlotId()];
            default -> f = 0.0F;
        }

        return f;
    }


    @Override
    public boolean isBlocking() {
        if(this.getOffHandStack().getItem() instanceof ShieldItem && getTarget()!=null && this.random.nextFloat()<0.25){
            playSound(SoundEvents.ITEM_SHIELD_BLOCK,1,1);
            return true;
        }
        else return false;
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        ItemStack stack = this.getMainHandStack();
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
    public void setCharging(boolean charging) {
        this.dataTracker.set(CHARGING, charging);
    }

    @Override
    public void setAiming(boolean aiming) {
        this.dataTracker.set(AIMING, aiming);
    }

    @Override
    public void postShoot() {
        this.despawnCounter = 0;
    }

    @Override
    public void tick() {
        super.tick();

    }
}
