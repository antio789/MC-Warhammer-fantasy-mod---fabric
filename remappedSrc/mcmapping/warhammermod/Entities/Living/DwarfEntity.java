package warhammermod.Entities.Living;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.village.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor.LordLastSeenSensor;
import warhammermod.Entities.Living.AImanager.Data.DwarfTrades;
import warhammermod.Entities.Living.AImanager.DwarfCombattasks;
import warhammermod.Entities.Living.AImanager.DwarfVillagerTasks;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class DwarfEntity extends AbstractVillager
        implements ReputationEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    //not using villagerdata as it is not easily modifiable, replaced by a decomposed method.
    private static final EntityDataAccessor<Integer> Profession = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> Level = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);

    public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of(ItemsInit.BEER,8, Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
    private static final Set<Item> WANTED_ITEMS = ImmutableSet.of(ItemsInit.BEER,Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, Items.BEETROOT_SEEDS);

    private int levelUpTimer;
    private boolean levelingUp;
    @Nullable
    private Player lastCustomer;
    private int foodLevel;
    private final GossipContainer gossip = new GossipContainer();
    private long gossipStartTime;
    private long lastGossipDecayTime;
    private int experience;
    private long lastRestockTime;
    private int restocksToday;
    private long lastRestockCheckTime;
    private boolean natural;

    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT,
            MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
            MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET,
            MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED,
            MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE,
            MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN,
            MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY,
            MemoryModuleType.ATTACK_COOLING_DOWN,MemoryModuleType.ATTACK_TARGET,MemoryModuleType.UNIVERSAL_ANGER,MemoryModuleType.ANGRY_AT,MemoryModuleType.ATTACK_COOLING_DOWN
    );
    private static final ImmutableList<SensorType<? extends Sensor<? super DwarfEntity>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY,
            WHRegistry.Hostiles, WHRegistry.VISIBLE_VILLAGER_BABIES, WHRegistry.SECONDARY_POIS, WHRegistry.Lord_LastSeen);

    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<DwarfEntity, Holder<PoiType>>> POINTS_OF_INTEREST =
            ImmutableMap.of(MemoryModuleType.HOME, (dwarf, registryEntry) -> registryEntry.is(PoiTypes.HOME),
                    MemoryModuleType.JOB_SITE, (dwarf, registryEntry) -> dwarf.getProfession().heldWorkstation().test(registryEntry),
                    MemoryModuleType.POTENTIAL_JOB_SITE, (dwarf, registryEntry) -> DwarfProfessionRecord.IS_ACQUIRABLE_JOB_SITE.test(registryEntry),
                    MemoryModuleType.MEETING_POINT, (dwarf, registryEntry) -> registryEntry.is(PoiTypes.MEETING));

    /**
     * initialization
     */

    public DwarfEntity(net.minecraft.world.level.Level world){
        super(Entityinit.DWARF,world);
    }

    public DwarfEntity(EntityType<? extends DwarfEntity> entityType, Level world) {
        super(entityType, world);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);
        this.setCanPickUpLoot(true);
        this.setVillagerDataBase();
        this.setEquipment(this.getProfession());
        //this.initDataTracker();
    }

    public Brain<DwarfEntity> getBrain() {
        return (Brain<DwarfEntity>) super.getBrain();
    }

    public Brain.Provider<DwarfEntity> createDwarfBrainProfile() {
        return Brain.provider(MEMORY_MODULES, SENSOR_TYPES);
    }

    @Override
    public Brain<?> makeBrain(Dynamic<?> dynamic) {
        Brain<DwarfEntity> brain = this.createDwarfBrainProfile().makeBrain(dynamic);

        this.initBrain(brain);
        return brain;
    }

    public void reinitializeBrain(ServerLevel world) {
        Brain<DwarfEntity> brain = (Brain<DwarfEntity>) this.brain;
        brain.stopAll(world, this);
        this.brain = brain.copyWithoutBehaviors();
        this.initBrain((Brain<DwarfEntity>) this.brain);
    }

    private void initBrain(Brain<DwarfEntity> entityBrain) {
        DwarfProfessionRecord dwarfProfession = this.getProfession();
        if (this.isBaby()) {
            entityBrain.setSchedule(Schedule.VILLAGER_BABY);
            entityBrain.addActivity(Activity.PLAY, DwarfVillagerTasks.createPlayTasks(0.5F));
            entityBrain.addActivity(Activity.PANIC, DwarfVillagerTasks.createPanicTasks(0.5F));
        } else {
            entityBrain.setSchedule(Schedule.VILLAGER_DEFAULT);
            entityBrain.addActivity(Activity.PANIC, DwarfCombattasks.createPanicTasks( 0.5F));
            entityBrain.addActivityWithConditions(Activity.WORK, DwarfVillagerTasks.createWorkTasks(dwarfProfession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
        }

        entityBrain.addActivity(Activity.CORE, DwarfVillagerTasks.createCoreTasks(dwarfProfession, 0.5F));
        VillagerProfession villagerProfession = VillagerProfession.NONE;
        entityBrain.addActivityWithConditions(Activity.MEET, DwarfVillagerTasks.createMeetTasks(0.5f), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
        entityBrain.addActivity(Activity.REST, DwarfVillagerTasks.createRestTasks(0.5f));
        entityBrain.addActivity(Activity.IDLE, DwarfVillagerTasks.createIdleTasks(0.5f));
        entityBrain.addActivity(Activity.PRE_RAID, DwarfVillagerTasks.createPreRaidTasks(0.5f));
        entityBrain.addActivity(Activity.RAID, DwarfVillagerTasks.createRaidTasks(0.5f));
        entityBrain.addActivity(Activity.HIDE, DwarfVillagerTasks.createHideTasks(0.5f));
        entityBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        entityBrain.setDefaultActivity(Activity.IDLE);
        entityBrain.setActiveActivityIfPossible(Activity.IDLE);
        entityBrain.updateActivityFromSchedule(this.level().getDayTime(), this.level().getGameTime());
    }



    //createvillagerattributes own separate class see @EntityAttributes


    public boolean isNatural() {
        return this.natural;
    }

    @Override
    protected void customServerAiStep() {
        Raid raid;
        this.level().getProfiler().push("DwarfBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        if (this.natural) {
            this.natural = false;
        }
        if (!this.isTrading() && this.levelUpTimer > 0) {
            --this.levelUpTimer;
            if (this.levelUpTimer <= 0) {
                if (this.levelingUp) {
                    this.levelUp();
                    this.levelingUp = false;
                }
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
            }
        }
        if (this.lastCustomer != null && this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level()).onReputationEvent(ReputationEventType.TRADE, this.lastCustomer, this);
            this.level().broadcastEntityEvent(this, EntityEvent.VILLAGER_HAPPY);
            this.lastCustomer = null;
        }
        if (!this.isNoAi() && this.random.nextInt(100) == 0 && (raid = ((ServerLevel)this.level()).getRaidAt(this.blockPosition())) != null && raid.isActive() && !raid.isOver()) {
            this.level().broadcastEntityEvent(this, EntityEvent.VILLAGER_SWEAT);
        }
        if (this.getProfessionID() == DwarfProfessionRecord.Warrior.ID() && this.isTrading()) {
            this.stopTrading();
        }

        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));

        super.customServerAiStep();
    }

    public void tick() {
        super.tick();
        if (this.getUnhappyCounter() > 0) {
            this.setUnhappyCounter(this.getUnhappyCounter() - 1);
        }

        this.decayGossip();
    }


    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        if (spawnReason == MobSpawnType.BREEDING) {
            this.setVillagerData(DwarfProfessionRecord.Warrior,this.getProfessionLevel());
        }
        if (spawnReason == MobSpawnType.STRUCTURE) {
            this.natural = true;
        }
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }


    /**
     * all custom profession
     */


    public int getProfessionID()
    {
        return Math.max(this.entityData.get(Profession), 0);//0farmer,1miner,2Engineer,3builder,4slayer,5lord,6 warrior
    }

    public void setVillagerData() {
        this.setVillagerData(getProfession(), getProfessionLevel());
    }

    public void setVillagerDataBase() {
        this.setVillagerData(DwarfProfessionRecord.Warrior,1);
    }



    public void setVillagerData(DwarfProfessionRecord prof,int level) {
        if (getProfession() != prof) {
            this.offers = null;
            this.setEquipment(prof);
            this.updateAttributes(prof);
        }
        if(entityData.get(Profession)!=DwarfProfessionRecord.Lord.ID()){
            this.entityData.set(Profession,prof.ID());}
        this.entityData.set(Level,level);
    }




    public void setVillagerlevel(int level) {
        setVillagerData(getProfession(),level);
    }

    public void setVillagerProfession(DwarfProfessionRecord profession){
        setVillagerData(getProfession(), getProfessionLevel());
        this.setEquipment(profession);
        this.updateAttributes(profession);
    }

/*
    public DwarfProfessionRecord getProfession(){
        for(DwarfProfessionRecord prof : DwarfProfession.Profession){
            if (prof.getID()==getProfessionID()){
                return prof;
            }
        }
        return DwarfProfession.Warrior;
    }

 */
    public DwarfProfessionRecord getProfession(){
        return this.registryAccess().registryOrThrow(WHRegistry.DwarfProfessionKey).stream().filter(p -> p.ID()==getProfessionID()).findFirst().orElse(DwarfProfessionRecord.Warrior);
    }


    public int getProfessionLevel(){
        return Math.max(this.entityData.get(Level),1);
    }

    public void setProfession(DwarfProfessionRecord profession,String from)
    {
        this.setVillagerData(profession,this.getProfessionLevel());
    }


    private final int[] xpforlevel = new int[]{0, 10, 70, 150, 250};

    @Environment(EnvType.CLIENT)
    public int getlevelfromxpclient(int p_221133_0_) {
        return haslevel(p_221133_0_) ? xpforlevel[p_221133_0_ - 1] : 0;
    }

    public int getlevelfromxp(int p_221127_0_) {
        return haslevel(p_221127_0_) ? xpforlevel[p_221127_0_] : 0;
    }

    public boolean haslevel(int p_221128_0_) {
        return p_221128_0_ >= 1 && p_221128_0_ < 5;
    }

    /**
     * all related to combat capabilities
     */
    protected void setEquipment(DwarfProfessionRecord prof)
    {
        if(hasItemInSlot(EquipmentSlot.MAINHAND)){
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        if(hasItemInSlot(EquipmentSlot.OFFHAND)){
            this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        }
        this.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(prof.mainhainditem()));
        if(prof.offhanditem()!=null) this.setItemSlot(EquipmentSlot.OFFHAND,new ItemStack(prof.offhanditem()));
    }

    protected void updateAttributes(DwarfProfessionRecord prof){
        if(prof.equals(DwarfProfessionRecord.Slayer)){
            this.getAttribute(Attributes.ARMOR).setBaseValue(0);
        }
    }

    @Environment(EnvType.CLIENT)
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isAggressive()){
            return AbstractIllager.IllagerArmPose.ATTACKING;
        } else {
            return AbstractIllager.IllagerArmPose.CROSSED;
        }
    }
    @Override
    public boolean isBlocking() {
        if(this.getOffhandItem().getItem() instanceof ShieldItem && this.isAggressive() && this.random.nextFloat()<0.33){
            playSound(SoundEvents.SHIELD_BLOCK,1,1);
            return true;
        }
        else return false;
    }

    /**
     * where modifications are needed
     */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(Profession, DwarfProfessionRecord.Warrior.ID());
        builder.define(Level,1);
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("level", this.getProfessionLevel());
        nbt.putInt("profession",this.getProfessionID());
        nbt.putByte("FoodLevel", (byte)this.foodLevel);
        nbt.put("Gossips", this.gossip.store(NbtOps.INSTANCE));
        nbt.putInt("Xp", this.experience);
        nbt.putLong("LastRestockdwarf", this.lastRestockTime);
        nbt.putLong("LastGossipDecay", this.lastGossipDecayTime);
        nbt.putInt("RestocksTodayDwarf", this.restocksToday);
        /* printing test
        System.out.println(getProfessionID() + " professionID from writing "+this.uuid);
        System.out.println(brain.getOptionalMemory(MemoryModuleType.JOB_SITE)+ " writing brain type WRITE_NBT "+uuid);
        if (nbt.contains("Brain", (int)NbtElement.COMPOUND_TYPE)) {
            Brain<DwarfEntity> brain2 = this.createDwarfBrainProfile().deserialize(new Dynamic<NbtElement>((DynamicOps<NbtElement>) NbtOps.INSTANCE, nbt.get("Brain")));
            System.out.println(brain2.getOptionalMemory(MemoryModuleType.JOB_SITE)+"write from brain do we have it? "+uuid);
        }
         */
        if (this.natural) {
            nbt.putBoolean("AssignProfessionWhenSpawned", true);
        }
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        /* printing debugging
        System.out.println("started readingfromnbt "+uuid);
        if (nbt.contains("Brain", (int)NbtElement.COMPOUND_TYPE)) {
            Brain<DwarfEntity> brain2 = this.createDwarfBrainProfile().deserialize(new Dynamic<NbtElement>((DynamicOps<NbtElement>) NbtOps.INSTANCE, nbt.get("Brain")));
            System.out.println(brain2.getOptionalMemory(MemoryModuleType.JOB_SITE)+"readnbt from brain do we have it? "+uuid);
        }
         */
        super.readAdditionalSaveData(nbt);
        if(nbt.contains("profession")&& nbt.contains("level")){
            this.entityData.set(Profession,nbt.getInt("profession"));
            this.entityData.set(Level,nbt.getInt("level"));
        }else {setVillagerDataBase();LOGGER.warn("warning no profession saved for this DwarfEntity");}

        if (nbt.contains("FoodLevel", Tag.TAG_BYTE)) {
            this.foodLevel = nbt.getByte("FoodLevel");
        }
        ListTag nbtList = nbt.getList("Gossips", Tag.TAG_COMPOUND);
        this.gossip.update(new Dynamic<>(NbtOps.INSTANCE, nbtList));
        if (nbt.contains("Xp", Tag.TAG_INT)) {
            this.experience = nbt.getInt("Xp");
        }
        this.lastRestockTime = nbt.getLong("LastRestockDwarf");
        this.restocksToday = nbt.getInt("RestocksTodayDwarf");
        this.setCanPickUpLoot(true);
        if (this.level() instanceof ServerLevel) {
            this.reinitializeBrain((ServerLevel)this.level());
        }
        this.restocksToday = nbt.getInt("RestocksToday");
        if (nbt.contains("AssignProfessionWhenSpawned")) {
            this.natural = nbt.getBoolean("AssignProfessionWhenSpawned");
        }
    }

    public float getVoicePitch()
    {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.4F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.6F;
    }


    @Nullable
    public DwarfEntity getBreedOffspring(ServerLevel world, AgeableMob entity) {
        double d0 = this.random.nextDouble();

        DwarfEntity dwarfEntity = new DwarfEntity(Entityinit.DWARF, world);
        dwarfEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(dwarfEntity.blockPosition()), MobSpawnType.BREEDING, (SpawnGroupData)null);
        return dwarfEntity;
    }

    public void summonGolem(ServerLevel world, long time, int requiredCount){
        this.SummonLord(world, time, requiredCount);
    }
    public void SummonLord(ServerLevel serverWorld, long time, int requiredCount) {
        if (this.canSummonGolem(time)) {
            AABB axisalignedbb = this.getBoundingBox().inflate(10.0D, 10.0D, 10.0D);
            List<DwarfEntity> list = serverWorld.getEntitiesOfClass(DwarfEntity.class, axisalignedbb);
            List<DwarfEntity> list1 = list.stream().filter((dwarfEntity) -> {
                return dwarfEntity.canSummonGolem(time);
            }).limit(5L).collect(Collectors.toList());
            if (list1.size() >= requiredCount) {
                if (this.tryspawnlord(serverWorld)) {
                    list.forEach(LordLastSeenSensor::golemDetected);
                }
            }
        }
    }


    public boolean canSummonGolem(long time) {
        if (!this.hasRecentlySlept(this.level().getGameTime())) {
            return false;
        } else {
            return !this.brain.hasMemoryValue(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
        }
    }

    private boolean tryspawnlord(ServerLevel world){
        int i = Mth.floor(this.getX());
        int j = Mth.floor(this.getY());
        int k = Mth.floor(this.getZ());
        DwarfEntity dwarfEntity = Entityinit.DWARF.create(world);
        for(int l = 0; l < 15; ++l) {
            int i1 = i + Mth.nextInt(this.random, 2, 6) * Mth.nextInt(this.random, -1, 1);
            int j1 = j + Mth.nextInt(this.random, 2, 6) * Mth.nextInt(this.random, -1, 1);
            int k1 = k + Mth.nextInt(this.random, 2, 6) * Mth.nextInt(this.random, -1, 1);
            BlockPos blockpos = new BlockPos(i1, j1, k1);
            EntityType<?> entityType = dwarfEntity.getType();
            if (!SpawnPlacements.isSpawnPositionOk(entityType, this.level(), blockpos) || !SpawnPlacements.checkSpawnRules(entityType, world, MobSpawnType.REINFORCEMENT, blockpos, this.level().random)) continue;
            dwarfEntity.setPos(i1, j1, k1);
            if ( !this.level().isUnobstructed(dwarfEntity) || !this.level().noCollision(dwarfEntity) || this.level().containsAnyLiquid(dwarfEntity.getBoundingBox())) continue;
            dwarfEntity.finalizeSpawn(world,world.getCurrentDifficultyAt(dwarfEntity.blockPosition()),MobSpawnType.MOB_SUMMONED,null);
            dwarfEntity.setVillagerData(DwarfProfessionRecord.Lord,this.getProfessionLevel());
            world.addFreshEntityWithPassengers(dwarfEntity);
            return true;
        }
        return false;
    }
    private boolean hasRecentlySlept(long gametime) {
        Optional<Long> optional = this.brain.getMemory(MemoryModuleType.LAST_SLEPT);
        if (optional.isPresent() && haslevelforlord()) {
            return gametime - optional.get() < 24000L;
        } else {
            return false;
        }
    }

    private boolean haslevelforlord() {
        return this.getProfessionLevel() == 5;
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        Item item = stack.getItem();
        return (WANTED_ITEMS.contains(item) || this.getProfession().gatherableItems().contains(item)) && this.getInventory().canAddItem(stack);
    }

    private void beginTradeWith(Player customer) {
        this.prepareOffersFor(customer);
        this.setTradingPlayer(customer);
        this.openTradingScreen(customer, this.getDisplayName(), this.getProfessionLevel());
    }

    @Override
    protected void stopTrading() {
        super.stopTrading();
        this.clearSpecialPrices();
    }
    /**
     * other no modifications
     */

    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (this.level() instanceof ServerLevel) {
            this.reinitializeBrain((ServerLevel)this.level());
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!itemStack.is(Entityinit.DWARF_SPAWN_EGG) && this.isAlive() && !this.isTrading() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.sayNo();
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else {
                if (!this.level().isClientSide) {
                    boolean bl = this.getOffers().isEmpty();
                    if (hand == InteractionHand.MAIN_HAND) {
                        if (bl) {
                            this.sayNo();
                        }

                        player.awardStat(Stats.TALKED_TO_VILLAGER);
                    }

                    if (bl) {
                        return InteractionResult.CONSUME;
                    }

                    this.beginTradeWith(player);
                }

                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        } else {
            return super.mobInteract(player, hand);
        }
    }

    private void sayNo() {
        this.setUnhappyCounter(40);
        if (!this.level().isClientSide()) {
            this.makeSound(SoundEvents.VILLAGER_NO);
        }
    }

    public void setTradingPlayer(@Nullable Player customer) {
        boolean bl = this.getTradingPlayer() != null && customer == null;
        super.setTradingPlayer(customer);
        if (bl) {
            this.stopTrading();
        }
    }

    private void clearSpecialPrices() {
        if (!this.level().isClientSide()) {
            Iterator var1 = this.getOffers().iterator();
            while(var1.hasNext()) {
                MerchantOffer tradeOffer = (MerchantOffer)var1.next();
                tradeOffer.resetSpecialPriceDiff();
            }
        }
    }

    public boolean canRestock() {
        return true;
    }

    public boolean isClientSide() {
        return this.level().isClientSide;
    }

    public void restock() {
        this.updateDemandBonus();
        for (MerchantOffer tradeOffer : this.getOffers()) {
            tradeOffer.resetUses();
        }
        this.sendOffersToCustomer();
        this.lastRestockTime = this.level().getGameTime();
        ++this.restocksToday;
    }

    private void sendOffersToCustomer() {
        MerchantOffers tradeOfferList = this.getOffers();
        Player playerEntity = this.getTradingPlayer();
        if (playerEntity != null && !tradeOfferList.isEmpty()) {
            playerEntity.sendMerchantOffers(playerEntity.containerMenu.containerId, tradeOfferList, this.getProfessionLevel(), this.getVillagerXp(), this.showProgressBar(), this.canRestock());
        }
    }

    private boolean needsRestock() {
        for (MerchantOffer tradeOffer : this.getOffers()) {
            if (!tradeOffer.needsRestock()) continue;
            return true;
        }
        return false;
    }

    private boolean canRestock() {
        return this.restocksToday == 0 || this.restocksToday < 2 && this.level().getGameTime() > this.lastRestockTime + 2400L;
    }

    public boolean shouldRestock() {
        long l = this.lastRestockTime + 12000L;
        long m = this.level().getGameTime();
        boolean bl = m > l;
        long n = this.level().getDayTime();
        if (this.lastRestockCheckTime > 0L) {
            long p = n / 24000L;
            long o = this.lastRestockCheckTime / 24000L;
            bl |= p > o;
        }
        this.lastRestockCheckTime = n;
        if (bl) {
            this.lastRestockTime = m;
            this.clearDailyRestockCount();
        }
        return this.canRestock() && this.needsRestock();
    }

    private void restockAndUpdateDemandBonus() {
        int i = 2 - this.restocksToday;
        if (i > 0) {
            for (MerchantOffer tradeOffer : this.getOffers()) {
                tradeOffer.resetUses();
            }
        }
        for (int j = 0; j < i; ++j) {
            this.updateDemandBonus();
        }
        this.sendOffersToCustomer();
    }

    private void updateDemandBonus() {
        for (MerchantOffer tradeOffer : this.getOffers()) {
            tradeOffer.updateDemand();
        }
    }

    private void prepareOffersFor(Player player) {
        int i = this.getReputation(player);
        if (i != 0) {
            for (MerchantOffer tradeOffer : this.getOffers()) {
                tradeOffer.addToSpecialPriceDiff(-Mth.floor((float)i * tradeOffer.getPriceMultiplier()));
            }
        }
        if (player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            MobEffectInstance statusEffectInstance = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
            int j = statusEffectInstance.getAmplifier();
            for (MerchantOffer tradeOffer2 : this.getOffers()) {
                double d = 0.3 + 0.0625 * (double)j;
                int k = (int)Math.floor(d * (double)tradeOffer2.getBaseCostA().getCount());
                tradeOffer2.addToSpecialPriceDiff(-Math.max(k, 1));
            }
        }
    }


    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        if (this.isTrading()) {
            return SoundEvents.VILLAGER_TRADE;
        }
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    public void playWorkSound() {
        this.makeSound(this.getProfession().workSound());
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        int i = 3 + this.random.nextInt(4);
        this.experience += offer.getXp();
        this.lastCustomer = this.getTradingPlayer();
        if (this.canLevelUp()) {
            this.levelUpTimer = 40;
            this.levelingUp = true;
            i += 5;
        }
        if (offer.shouldRewardExp()) {
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), i));
        }
    }

    @Override
    public void setLastHurtByMob(@Nullable LivingEntity attacker) {
        if (attacker != null && this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level()).onReputationEvent(ReputationEventType.VILLAGER_HURT, attacker, this);
            if (this.isAlive() && attacker instanceof Player) {
                this.level().broadcastEntityEvent(this, EntityEvent.VILLAGER_ANGRY);
            }
        }
        super.setLastHurtByMob(attacker);
    }

    @Override
    public void die(DamageSource damageSource) {
        LOGGER.info("DwarfVillager {} died, message: '{}'", (Object)this, (Object)damageSource.getLocalizedDeathMessage(this).getString());
        Entity entity = damageSource.getEntity();
        if (entity != null) {
            this.notifyDeath(entity);
        }
        this.releaseAllTickets();
        super.die(damageSource);
    }

    private void releaseAllTickets() {
        this.releaseTicketFor(MemoryModuleType.HOME);
        this.releaseTicketFor(MemoryModuleType.JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.MEETING_POINT);
    }

    private void notifyDeath(Entity killer) {
        Level world = this.level();
        if (!(world instanceof ServerLevel serverWorld)) {
            return;
        }
        Optional<NearestVisibleLivingEntities> optional = this.brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        if (optional.isEmpty()) {
            return;
        }
        optional.get().findAll(ReputationEventHandler.class::isInstance).forEach(observer -> serverWorld.onReputationEvent(ReputationEventType.VILLAGER_KILLED, killer, (ReputationEventHandler)((Object)observer)));
    }

    public void releaseTicketFor(MemoryModuleType<GlobalPos> pos) {

        if (!(this.level() instanceof ServerLevel)) {
            return;
        }
        MinecraftServer minecraftServer = ((ServerLevel)this.level()).getServer();
        this.brain.getMemory(pos).ifPresent(posx -> {
            ServerLevel serverWorld = minecraftServer.getLevel(posx.dimension());
            if (serverWorld == null) {
                return;
            }
            PoiManager pointOfInterestStorage = serverWorld.getPoiManager();
            Optional<Holder<PoiType>> optional = pointOfInterestStorage.getType(posx.pos());
            BiPredicate<DwarfEntity, Holder<PoiType>> biPredicate = POINTS_OF_INTEREST.get(pos);
            if (optional.isPresent() && biPredicate.test(this, optional.get())) {
                pointOfInterestStorage.release(posx.pos());
                DebugPackets.sendPoiTicketCountPacket(serverWorld, posx.pos());
            }
        });
    }

    @Override
    public boolean canBreed() {
        return this.foodLevel + this.getAvailableFood() >= 12 && !this.isSleeping() && this.getAge() == 0;
    }

    private boolean lacksFood() {
        return this.foodLevel < 12;
    }

    private void consumeAvailableFood() {
        if (!this.lacksFood() || this.getAvailableFood() == 0) {
            return;
        }
        for (int i = 0; i < this.getInventory().getContainerSize(); ++i) {
            int j;
            Integer integer;
            ItemStack itemStack = this.getInventory().getItem(i);
            if (itemStack.isEmpty() || (integer = ITEM_FOOD_VALUES.get(itemStack.getItem())) == null) continue;
            for (int k = j = itemStack.getCount(); k > 0; --k) {
                this.foodLevel += integer.intValue();
                this.getInventory().removeItem(i, 1);
                if (this.lacksFood()) continue;
                return;
            }
        }
    }

    public int getReputation(Player player) {
        return this.gossip.getReputation(player.getUUID(), gossipType -> true);
    }

    private void depleteFood(int amount) {
        this.foodLevel -= amount;
    }

    public void eatForBreeding() {
        this.consumeAvailableFood();
        this.depleteFood(12);
    }

    //only for zombieconversion
    public void setOffers(MerchantOffers offers) {
        this.offers = offers;
    }

    private boolean canLevelUp() {
        int i = this.getProfessionLevel();
        return canLevelUp(i) && this.experience >= getUpperLevelExperience(i);
    }

    private void levelUp() {
        this.setVillagerlevel(this.getProfessionLevel()+1);
        this.updateTrades();
    }
    //added to remove villagerData from dwarf for clarity
    public static int getUpperLevelExperience(int level) {
        return VillagerData.canLevelUp(level) ? LEVEL_BASE_EXPERIENCE[level] : 0;
    }

    public static boolean canLevelUp(int level) {
        return level >= 1 && level < 5;
    }
    private static final int[] LEVEL_BASE_EXPERIENCE = new int[]{0, 10, 70, 150, 250};

    @Override
    protected Component getTypeName() {
        return Component.translatable(this.getType().getDescriptionId() + "." + WHRegistry.DWARF_PROFESSIONS.getKey(this.getProfession()).getPath());
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.LOVE_HEARTS) {
            this.addParticlesAroundSelf(ParticleTypes.HEART);
        } else if (status == EntityEvent.VILLAGER_ANGRY) {
            this.addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
        } else if (status == EntityEvent.VILLAGER_HAPPY) {
            this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
        } else if (status == EntityEvent.VILLAGER_SWEAT) {
            this.addParticlesAroundSelf(ParticleTypes.SPLASH);
        } else {
            super.handleEntityEvent(status);
        }
    }

    @Override
    protected void pickUpItem(ItemEntity item) {
        InventoryCarrier.pickUpItem(this, this, item);
    }

    public boolean wantsToStartBreeding() {
        return this.getAvailableFood() >= 24;
    }

    public boolean canBreed() {
        return this.getAvailableFood() < 12;
    }

    private int getAvailableFood() {
        SimpleContainer simpleInventory = this.getInventory();
        return ITEM_FOOD_VALUES.entrySet().stream().mapToInt(item -> simpleInventory.countItem((Item)item.getKey()) * (Integer)item.getValue()).sum();
    }

    public boolean hasSeedToPlant() {return this.getInventory().hasAnyMatching((stack) -> stack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS));}

    @Override
    protected void updateTrades() {
        Int2ObjectMap<VillagerTrades.ItemListing[]> int2ObjectMap2 = DwarfTrades.TRADES.get(this.getProfession());
        if (int2ObjectMap2 == null || int2ObjectMap2.isEmpty()) {
            return;
        }
        VillagerTrades.ItemListing[] factorys = int2ObjectMap2.get(this.getProfessionLevel());
        if (factorys == null) {
            return;
        }
        MerchantOffers tradeOfferList = this.getOffers();
        this.addOffersFromItemListings(tradeOfferList, factorys, 2);
    }

    public void talkWithVillager(ServerLevel world, DwarfEntity dwarf, long time) {
        if ((time < this.gossipStartTime || time >= this.gossipStartTime + 1200L) && (time < dwarf.gossipStartTime || time >= dwarf.gossipStartTime + 1200L)) {
            this.gossip.transferFrom(dwarf.gossip, this.random, 10);
            this.gossipStartTime = time;
            dwarf.gossipStartTime = time;
            this.summonGolem(world, time, 5);
        }
    }

    private void decayGossip() {
        long l = this.level().getGameTime();
        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = l;
        } else if (l >= this.lastGossipDecayTime + 24000L) {
            this.gossip.decay();
            this.lastGossipDecayTime = l;
        }
    }

    @Override
    public void onReputationEventFrom(ReputationEventType interaction, Entity entity) {
        if (interaction == ReputationEventType.ZOMBIE_VILLAGER_CURED) {
            this.gossip.add(entity.getUUID(), GossipType.MAJOR_POSITIVE, 20);
            this.gossip.add(entity.getUUID(), GossipType.MINOR_POSITIVE, 25);
        } else if (interaction == ReputationEventType.TRADE) {
            this.gossip.add(entity.getUUID(), GossipType.TRADING, 2);
        } else if (interaction == ReputationEventType.VILLAGER_HURT) {
            this.gossip.add(entity.getUUID(), GossipType.MINOR_NEGATIVE, 25);
        } else if (interaction == ReputationEventType.VILLAGER_KILLED) {
            this.gossip.add(entity.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
        }
    }

    @Override
    public int getVillagerXp() {
        return this.experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    private void clearDailyRestockCount() {
        this.restockAndUpdateDemandBonus();
        this.restocksToday = 0;
    }
    //used by zombie conversion
    public GossipContainer getGossip() {
        return this.gossip;
    }
    //conversion from zombie not used
    public void readGossipDataNbt(Tag nbt) {
        this.gossip.update(new Dynamic<Tag>(NbtOps.INSTANCE, nbt));
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public void startSleeping(BlockPos pos) {
        super.startSleeping(pos);
        this.brain.setMemory(MemoryModuleType.LAST_SLEPT, this.level().getGameTime());
        this.brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        this.brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void stopSleeping() {
        super.stopSleeping();
        this.brain.setMemory(MemoryModuleType.LAST_WOKEN, this.level().getGameTime());
    }
}
