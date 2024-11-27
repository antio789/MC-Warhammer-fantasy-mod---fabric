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
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.village.*;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.apache.logging.log4j.core.jmx.Server;
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

public class DwarfEntity extends MerchantEntity
        implements InteractionObserver {
    private static final Logger LOGGER = LogUtils.getLogger();

    //not using villagerdata as it is not easily modifiable, replaced by a decomposed method.
    private static final TrackedData<Integer> Profession = DataTracker.registerData(DwarfEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> Level = DataTracker.registerData(DwarfEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of(ItemsInit.BEER,8, Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
    private static final Set<Item> WANTED_ITEMS = ImmutableSet.of(ItemsInit.BEER,Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, Items.BEETROOT_SEEDS);

    private int levelUpTimer;
    private boolean levelingUp;
    @Nullable
    private PlayerEntity lastCustomer;
    private int foodLevel;
    private final VillagerGossips gossip = new VillagerGossips();
    private long gossipStartTime;
    private long lastGossipDecayTime;
    private int experience;
    private long lastRestockTime;
    private int restocksToday;
    private long lastRestockCheckTime;
    private boolean natural;

    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT,
            MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS,
            MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
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

    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<DwarfEntity, RegistryEntry<PointOfInterestType>>> POINTS_OF_INTEREST =
            ImmutableMap.of(MemoryModuleType.HOME, (dwarf, registryEntry) -> registryEntry.matchesKey(PointOfInterestTypes.HOME),
                    MemoryModuleType.JOB_SITE, (dwarf, registryEntry) -> dwarf.getProfession().heldWorkstation().test(registryEntry),
                    MemoryModuleType.POTENTIAL_JOB_SITE, (dwarf, registryEntry) -> DwarfProfessionRecord.IS_ACQUIRABLE_JOB_SITE.test(registryEntry),
                    MemoryModuleType.MEETING_POINT, (dwarf, registryEntry) -> registryEntry.matchesKey(PointOfInterestTypes.MEETING));

    /**
     * initialization
     */

    public DwarfEntity(net.minecraft.world.World world){
        super(Entityinit.DWARF,world);
    }

    public DwarfEntity(EntityType<? extends DwarfEntity> entityType, World world) {
        super(entityType, world);
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.getNavigation().setCanSwim(true);
        this.setCanPickUpLoot(true);
        this.setVillagerDataBase();
        this.setEquipment(this.getProfession());
        //this.initDataTracker();
    }

    public Brain<DwarfEntity> getBrain() {
        return (Brain<DwarfEntity>) super.getBrain();
    }

    public Brain.Profile<DwarfEntity> createDwarfBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSOR_TYPES);
    }

    @Override
    public Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        Brain<DwarfEntity> brain = this.createDwarfBrainProfile().deserialize(dynamic);

        this.initBrain(brain);
        return brain;
    }

    public void reinitializeBrain(ServerWorld world) {
        Brain<DwarfEntity> brain = (Brain<DwarfEntity>) this.brain;
        brain.stopAllTasks(world, this);
        this.brain = brain.copy();
        this.initBrain((Brain<DwarfEntity>) this.brain);
    }

    private void initBrain(Brain<DwarfEntity> entityBrain) {
        DwarfProfessionRecord dwarfProfession = this.getProfession();
        if (this.isBaby()) {
            entityBrain.setSchedule(Schedule.VILLAGER_BABY);
            entityBrain.setTaskList(Activity.PLAY, DwarfVillagerTasks.createPlayTasks(0.5F));
            entityBrain.setTaskList(Activity.PANIC, DwarfVillagerTasks.createPanicTasks(0.5F));
        } else {
            entityBrain.setSchedule(Schedule.VILLAGER_DEFAULT);
            entityBrain.setTaskList(Activity.PANIC, DwarfCombattasks.createPanicTasks( 0.5F));
            entityBrain.setTaskList(Activity.WORK, DwarfVillagerTasks.createWorkTasks(dwarfProfession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT)));
        }

        entityBrain.setTaskList(Activity.CORE, DwarfVillagerTasks.createCoreTasks(dwarfProfession, 0.5F));
        entityBrain.setTaskList(Activity.MEET, DwarfVillagerTasks.createMeetTasks(0.5f), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleState.VALUE_PRESENT)));
        entityBrain.setTaskList(Activity.REST, DwarfVillagerTasks.createRestTasks(0.5f));
        entityBrain.setTaskList(Activity.IDLE, DwarfVillagerTasks.createIdleTasks(0.5f));
        entityBrain.setTaskList(Activity.PRE_RAID, DwarfVillagerTasks.createPreRaidTasks(0.5f));
        entityBrain.setTaskList(Activity.RAID, DwarfVillagerTasks.createRaidTasks(0.5f));
        entityBrain.setTaskList(Activity.HIDE, DwarfVillagerTasks.createHideTasks(0.5f));
        entityBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        entityBrain.setDefaultActivity(Activity.IDLE);
        entityBrain.doExclusively(Activity.IDLE);
        entityBrain.refreshActivities(this.getWorld().getTimeOfDay(), this.getWorld().getTime());
    }



    //createvillagerattributes own separate class see @EntityAttributes


    public boolean isNatural() {
        return this.natural;
    }

    @Override
    protected void mobTick(ServerWorld world) {
        Profiler profiler = Profilers.get();
        profiler.push("villagerBrain");
        this.getBrain().tick(world, this);
        profiler.pop();
        if (this.natural) {
            this.natural = false;
        }
        if (!this.hasCustomer() && this.levelUpTimer > 0) {
            --this.levelUpTimer;
            if (this.levelUpTimer <= 0) {
                if (this.levelingUp) {
                    this.levelUp();
                    this.levelingUp = false;
                }
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
            }
        }
        if (this.lastCustomer != null) {
            world.handleInteraction(EntityInteraction.TRADE, this.lastCustomer, this);
            world.sendEntityStatus(this, EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES);
            this.lastCustomer = null;
        }
        if (!this.isAiDisabled() && this.random.nextInt(100) == 0) {
            Raid raid = world.getRaidAt(this.getBlockPos());
            if (raid != null && raid.isActive() && !raid.isFinished()) {
                world.sendEntityStatus(this, EntityStatuses.ADD_SPLASH_PARTICLES);
            }
        }
        if (this.getProfessionID() == DwarfProfessionRecord.Warrior.ID() && this.hasCustomer()) {
            this.resetCustomer();
        }

        this.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));

        super.mobTick(world);
    }

    public void tick() {
        super.tick();
        if (this.getHeadRollingTimeLeft() > 0) {
            this.setHeadRollingTimeLeft(this.getHeadRollingTimeLeft() - 1);
        }

        this.decayGossip();
    }


    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        if (spawnReason == SpawnReason.BREEDING) {
            this.setVillagerData(DwarfProfessionRecord.Warrior,this.getProfessionLevel());
        }
        if (spawnReason == SpawnReason.STRUCTURE) {
            this.natural = true;
        }
        return super.initialize(world, difficulty, spawnReason, entityData);
    }


    /**
     * all custom profession
     */


    public int getProfessionID()
    {
        return Math.max(this.dataTracker.get(Profession), 0);//0farmer,1miner,2Engineer,3builder,4slayer,5lord,6 warrior
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
        if(dataTracker.get(Profession)!=DwarfProfessionRecord.Lord.ID()){
            this.dataTracker.set(Profession,prof.ID());}
        this.dataTracker.set(Level,level);
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
        return this.getRegistryManager().getOrThrow(WHRegistry.DwarfProfessionKey).stream().filter(p -> p.ID()==getProfessionID()).findFirst().orElse(DwarfProfessionRecord.Warrior);
    }


    public int getProfessionLevel(){
        return Math.max(this.dataTracker.get(Level),1);
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
        if(hasStackEquipped(EquipmentSlot.MAINHAND)){
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        if(hasStackEquipped(EquipmentSlot.OFFHAND)){
            this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        }
        this.equipStack(EquipmentSlot.MAINHAND,new ItemStack(prof.mainhainditem()));
        if(prof.offhanditem()!=null) this.equipStack(EquipmentSlot.OFFHAND,new ItemStack(prof.offhanditem()));
    }

    protected void updateAttributes(DwarfProfessionRecord prof){
        if(prof.equals(DwarfProfessionRecord.Slayer)){
            this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(0);
        }
    }

    @Environment(EnvType.CLIENT)
    public IllagerEntity.State getArmPose() {
        if (this.isAttacking()){
            return IllagerEntity.State.ATTACKING;
        } else {
            return IllagerEntity.State.CROSSED;
        }
    }
    @Override
    public boolean isBlocking() {
        if(this.getOffHandStack().getItem() instanceof ShieldItem && this.isAttacking() && this.random.nextFloat()<0.33){
            playSound(SoundEvents.ITEM_SHIELD_BLOCK,1,1);
            return true;
        }
        else return false;
    }

    /**
     * where modifications are needed
     */

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(Profession, DwarfProfessionRecord.Warrior.ID());
        builder.add(Level,1);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("level", this.getProfessionLevel());
        nbt.putInt("profession",this.getProfessionID());
        nbt.putByte("FoodLevel", (byte)this.foodLevel);
        nbt.put("Gossips", this.gossip.serialize(NbtOps.INSTANCE));
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

    public void readCustomDataFromNbt(NbtCompound nbt) {
        /* printing debugging
        System.out.println("started readingfromnbt "+uuid);
        if (nbt.contains("Brain", (int)NbtElement.COMPOUND_TYPE)) {
            Brain<DwarfEntity> brain2 = this.createDwarfBrainProfile().deserialize(new Dynamic<NbtElement>((DynamicOps<NbtElement>) NbtOps.INSTANCE, nbt.get("Brain")));
            System.out.println(brain2.getOptionalMemory(MemoryModuleType.JOB_SITE)+"readnbt from brain do we have it? "+uuid);
        }
         */
        super.readCustomDataFromNbt(nbt);
        if(nbt.contains("profession")&& nbt.contains("level")){
            this.dataTracker.set(Profession,nbt.getInt("profession"));
            this.dataTracker.set(Level,nbt.getInt("level"));
        }else {setVillagerDataBase();LOGGER.warn("warning no profession saved for this DwarfEntity");}

        if (nbt.contains("FoodLevel", NbtElement.BYTE_TYPE)) {
            this.foodLevel = nbt.getByte("FoodLevel");
        }
        NbtList nbtList = nbt.getList("Gossips", NbtElement.COMPOUND_TYPE);
        this.gossip.deserialize(new Dynamic<>(NbtOps.INSTANCE, nbtList));
        if (nbt.contains("Xp", NbtElement.INT_TYPE)) {
            this.experience = nbt.getInt("Xp");
        }
        this.lastRestockTime = nbt.getLong("LastRestockDwarf");
        this.restocksToday = nbt.getInt("RestocksTodayDwarf");
        this.setCanPickUpLoot(true);
        if (this.getWorld() instanceof ServerWorld) {
            this.reinitializeBrain((ServerWorld)this.getWorld());
        }
        this.restocksToday = nbt.getInt("RestocksToday");
        if (nbt.contains("AssignProfessionWhenSpawned")) {
            this.natural = nbt.getBoolean("AssignProfessionWhenSpawned");
        }
    }

    public float getSoundPitch()
    {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.4F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.6F;
    }


    @Nullable
    public DwarfEntity createChild(ServerWorld world, PassiveEntity entity) {
        double d0 = this.random.nextDouble();

        DwarfEntity dwarfEntity = new DwarfEntity(Entityinit.DWARF, world);
        dwarfEntity.initialize(world, world.getLocalDifficulty(dwarfEntity.getBlockPos()), SpawnReason.BREEDING, (EntityData)null);
        return dwarfEntity;
    }

    public void summonGolem(ServerWorld world, long time, int requiredCount){
        this.SummonLord(world, time, requiredCount);
    }
    public void SummonLord(ServerWorld serverWorld, long time, int requiredCount) {
        if (this.canSummonGolem(time)) {
            Box axisalignedbb = this.getBoundingBox().expand(10.0D, 10.0D, 10.0D);
            List<DwarfEntity> list = serverWorld.getNonSpectatingEntities(DwarfEntity.class, axisalignedbb);
            List<DwarfEntity> list1 = list.stream().filter((dwarfEntity) -> {
                return dwarfEntity.canSummonGolem(time);
            }).limit(5L).collect(Collectors.toList());
            if (list1.size() >= requiredCount) {
                if (this.tryspawnlord(serverWorld)) {
                    list.forEach(LordLastSeenSensor::rememberIronGolem);
                }
            }
        }
    }


    public boolean canSummonGolem(long time) {
        if (!this.hasRecentlySlept(this.getWorld().getTime())) {
            return false;
        } else {
            return !this.brain.hasMemoryModule(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
        }
    }

    private boolean tryspawnlord(ServerWorld world){
        int i = MathHelper.floor(this.getX());
        int j = MathHelper.floor(this.getY());
        int k = MathHelper.floor(this.getZ());
        DwarfEntity dwarfEntity = Entityinit.DWARF.create(world,SpawnReason.BREEDING);
        for(int l = 0; l < 15; ++l) {
            int i1 = i + MathHelper.nextInt(this.random, 2, 6) * MathHelper.nextInt(this.random, -1, 1);
            int j1 = j + MathHelper.nextInt(this.random, 2, 6) * MathHelper.nextInt(this.random, -1, 1);
            int k1 = k + MathHelper.nextInt(this.random, 2, 6) * MathHelper.nextInt(this.random, -1, 1);
            BlockPos blockpos = new BlockPos(i1, j1, k1);
            EntityType<?> entityType = dwarfEntity.getType();
            if (!SpawnRestriction.isSpawnPosAllowed(entityType, this.getWorld(), blockpos) || !SpawnRestriction.canSpawn(entityType, world, SpawnReason.REINFORCEMENT, blockpos, this.getWorld().random)) continue;
            dwarfEntity.setPosition(i1, j1, k1);
            if ( !this.getWorld().doesNotIntersectEntities(dwarfEntity) || !this.getWorld().isSpaceEmpty(dwarfEntity) || this.getWorld().containsFluid(dwarfEntity.getBoundingBox())) continue;
            dwarfEntity.initialize(world,world.getLocalDifficulty(dwarfEntity.getBlockPos()),SpawnReason.MOB_SUMMONED,null);
            dwarfEntity.setVillagerData(DwarfProfessionRecord.Lord,this.getProfessionLevel());
            world.spawnEntityAndPassengers(dwarfEntity);
            return true;
        }
        return false;
    }
    private boolean hasRecentlySlept(long gametime) {
        Optional<Long> optional = this.brain.getOptionalRegisteredMemory(MemoryModuleType.LAST_SLEPT);
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
    public boolean canGather(ServerWorld world,ItemStack stack) {
        Item item = stack.getItem();
        return (WANTED_ITEMS.contains(item) || this.getProfession().gatherableItems().contains(item)) && this.getInventory().canInsert(stack);
    }

    private void beginTradeWith(PlayerEntity customer) {
        this.prepareOffersFor(customer);
        this.setCustomer(customer);
        this.sendOffers(customer, this.getDisplayName(), this.getProfessionLevel());
    }

    @Override
    protected void resetCustomer() {
        super.resetCustomer();
        this.clearSpecialPrices();
    }
    /**
     * other no modifications
     */

    protected void onGrowUp() {
        super.onGrowUp();
        if (this.getWorld() instanceof ServerWorld) {
            this.reinitializeBrain((ServerWorld)this.getWorld());
        }
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isOf(Entityinit.DWARF_SPAWN_EGG) && this.isAlive() && !this.hasCustomer() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.sayNo();
                return ActionResult.SUCCESS;
            } else {
                if (!this.getWorld().isClient) {
                    boolean bl = this.getOffers().isEmpty();
                    if (hand == Hand.MAIN_HAND) {
                        if (bl) {
                            this.sayNo();
                        }

                        player.incrementStat(Stats.TALKED_TO_VILLAGER);
                    }

                    if (bl) {
                        return ActionResult.CONSUME;
                    }

                    this.beginTradeWith(player);
                }

                return ActionResult.SUCCESS;
            }
        } else {
            return super.interactMob(player, hand);
        }
    }

    private void sayNo() {
        this.setHeadRollingTimeLeft(40);
        if (!this.getWorld().isClient()) {
            this.playSound(SoundEvents.ENTITY_VILLAGER_NO);
        }
    }

    public void setCustomer(@Nullable PlayerEntity customer) {
        boolean bl = this.getCustomer() != null && customer == null;
        super.setCustomer(customer);
        if (bl) {
            this.resetCustomer();
        }
    }

    private void clearSpecialPrices() {
        if (!this.getWorld().isClient()) {
            Iterator var1 = this.getOffers().iterator();
            while(var1.hasNext()) {
                TradeOffer tradeOffer = (TradeOffer)var1.next();
                tradeOffer.clearSpecialPrice();
            }
        }
    }

    public boolean canRefreshTrades() {
        return true;
    }

    public boolean isClient() {
        return this.getWorld().isClient;
    }

    public void restock() {
        this.updateDemandBonus();
        for (TradeOffer tradeOffer : this.getOffers()) {
            tradeOffer.resetUses();
        }
        this.sendOffersToCustomer();
        this.lastRestockTime = this.getWorld().getTime();
        ++this.restocksToday;
    }

    private void sendOffersToCustomer() {
        TradeOfferList tradeOfferList = this.getOffers();
        PlayerEntity playerEntity = this.getCustomer();
        if (playerEntity != null && !tradeOfferList.isEmpty()) {
            playerEntity.sendTradeOffers(playerEntity.currentScreenHandler.syncId, tradeOfferList, this.getProfessionLevel(), this.getExperience(), this.isLeveledMerchant(), this.canRefreshTrades());
        }
    }

    private boolean needsRestock() {
        for (TradeOffer tradeOffer : this.getOffers()) {
            if (!tradeOffer.hasBeenUsed()) continue;
            return true;
        }
        return false;
    }

    private boolean canRestock() {
        return this.restocksToday == 0 || this.restocksToday < 2 && this.getWorld().getTime() > this.lastRestockTime + 2400L;
    }

    public boolean shouldRestock() {
        long l = this.lastRestockTime + 12000L;
        long m = this.getWorld().getTime();
        boolean bl = m > l;
        long n = this.getWorld().getTimeOfDay();
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
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.resetUses();
            }
        }
        for (int j = 0; j < i; ++j) {
            this.updateDemandBonus();
        }
        this.sendOffersToCustomer();
    }

    private void updateDemandBonus() {
        for (TradeOffer tradeOffer : this.getOffers()) {
            tradeOffer.updateDemandBonus();
        }
    }

    private void prepareOffersFor(PlayerEntity player) {
        int i = this.getReputation(player);
        if (i != 0) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.increaseSpecialPrice(-MathHelper.floor((float)i * tradeOffer.getPriceMultiplier()));
            }
        }
        if (player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
            int j = statusEffectInstance.getAmplifier();
            for (TradeOffer tradeOffer2 : this.getOffers()) {
                double d = 0.3 + 0.0625 * (double)j;
                int k = (int)Math.floor(d * (double)tradeOffer2.getOriginalFirstBuyItem().getCount());
                tradeOffer2.increaseSpecialPrice(-Math.max(k, 1));
            }
        }
    }


    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        if (this.hasCustomer()) {
            return SoundEvents.ENTITY_VILLAGER_TRADE;
        }
        return SoundEvents.ENTITY_VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    public void playWorkSound() {
        this.playSound(this.getProfession().workSound());
    }

    @Override
    protected void afterUsing(TradeOffer offer) {
        int i = 3 + this.random.nextInt(4);
        this.experience += offer.getMerchantExperience();
        this.lastCustomer = this.getCustomer();
        if (this.canLevelUp()) {
            this.levelUpTimer = 40;
            this.levelingUp = true;
            i += 5;
        }
        if (offer.shouldRewardPlayerExperience()) {
            this.getWorld().spawnEntity(new ExperienceOrbEntity(this.getWorld(), this.getX(), this.getY() + 0.5, this.getZ(), i));
        }
    }

    @Override
    public void setAttacker(@Nullable LivingEntity attacker) {
        if (attacker != null && this.getWorld() instanceof ServerWorld) {
            ((ServerWorld)this.getWorld()).handleInteraction(EntityInteraction.VILLAGER_HURT, attacker, this);
            if (this.isAlive() && attacker instanceof PlayerEntity) {
                this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES);
            }
        }
        super.setAttacker(attacker);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        LOGGER.info("DwarfVillager {} died, message: '{}'", (Object)this, (Object)damageSource.getDeathMessage(this).getString());
        Entity entity = damageSource.getAttacker();
        if (entity != null) {
            this.notifyDeath(entity);
        }
        this.releaseAllTickets();
        super.onDeath(damageSource);
    }

    private void releaseAllTickets() {
        this.releaseTicketFor(MemoryModuleType.HOME);
        this.releaseTicketFor(MemoryModuleType.JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.MEETING_POINT);
    }

    private void notifyDeath(Entity killer) {
        World world = this.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        Optional<LivingTargetCache> optional = this.brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS);
        if (optional.isEmpty()) {
            return;
        }
        optional.get().iterate(InteractionObserver.class::isInstance).forEach(observer -> serverWorld.handleInteraction(EntityInteraction.VILLAGER_KILLED, killer, (InteractionObserver)((Object)observer)));
    }

    public void releaseTicketFor(MemoryModuleType<GlobalPos> pos) {

        if (!(this.getWorld() instanceof ServerWorld)) {
            return;
        }
        MinecraftServer minecraftServer = ((ServerWorld)this.getWorld()).getServer();
        this.brain.getOptionalRegisteredMemory(pos).ifPresent(posx -> {
            ServerWorld serverWorld = minecraftServer.getWorld(posx.dimension());
            if (serverWorld == null) {
                return;
            }
            PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
            Optional<RegistryEntry<PointOfInterestType>> optional = pointOfInterestStorage.getType(posx.pos());
            BiPredicate<DwarfEntity, RegistryEntry<PointOfInterestType>> biPredicate = POINTS_OF_INTEREST.get(pos);
            if (optional.isPresent() && biPredicate.test(this, optional.get())) {
                pointOfInterestStorage.releaseTicket(posx.pos());
                DebugInfoSender.sendPointOfInterest(serverWorld, posx.pos());
            }
        });
    }

    @Override
    public boolean isReadyToBreed() {
        return this.foodLevel + this.getAvailableFood() >= 12 && !this.isSleeping() && this.getBreedingAge() == 0;
    }

    private boolean lacksFood() {
        return this.foodLevel < 12;
    }

    private void consumeAvailableFood() {
        if (!this.lacksFood() || this.getAvailableFood() == 0) {
            return;
        }
        for (int i = 0; i < this.getInventory().size(); ++i) {
            int j;
            Integer integer;
            ItemStack itemStack = this.getInventory().getStack(i);
            if (itemStack.isEmpty() || (integer = ITEM_FOOD_VALUES.get(itemStack.getItem())) == null) continue;
            for (int k = j = itemStack.getCount(); k > 0; --k) {
                this.foodLevel += integer.intValue();
                this.getInventory().removeStack(i, 1);
                if (this.lacksFood()) continue;
                return;
            }
        }
    }

    public int getReputation(PlayerEntity player) {
        return this.gossip.getReputationFor(player.getUuid(), gossipType -> true);
    }

    private void depleteFood(int amount) {
        this.foodLevel -= amount;
    }

    public void eatForBreeding() {
        this.consumeAvailableFood();
        this.depleteFood(12);
    }

    //only for zombieconversion
    public void setOffers(TradeOfferList offers) {
        this.offers = offers;
    }

    private boolean canLevelUp() {
        int i = this.getProfessionLevel();
        return canLevelUp(i) && this.experience >= getUpperLevelExperience(i);
    }

    private void levelUp() {
        this.setVillagerlevel(this.getProfessionLevel()+1);
        this.fillRecipes();
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
    protected Text getDefaultName() {
        return Text.translatable(this.getType().getTranslationKey() + "." + WHRegistry.DWARF_PROFESSIONS.getId(this.getProfession()).getPath());
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.ADD_VILLAGER_HEART_PARTICLES) {
            this.produceParticles(ParticleTypes.HEART);
        } else if (status == EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES) {
            this.produceParticles(ParticleTypes.ANGRY_VILLAGER);
        } else if (status == EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES) {
            this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
        } else if (status == EntityStatuses.ADD_SPLASH_PARTICLES) {
            this.produceParticles(ParticleTypes.SPLASH);
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    protected void loot(ServerWorld world, ItemEntity item) {
        InventoryOwner.pickUpItem(world, this, this, item);
    }

    public boolean wantsToStartBreeding() {
        return this.getAvailableFood() >= 24;
    }

    public boolean canBreed() {
        return this.getAvailableFood() < 12;
    }

    private int getAvailableFood() {
        SimpleInventory simpleInventory = this.getInventory();
        return ITEM_FOOD_VALUES.entrySet().stream().mapToInt(item -> simpleInventory.count((Item)item.getKey()) * (Integer)item.getValue()).sum();
    }

    public boolean hasSeedToPlant() {return this.getInventory().containsAny((stack) -> stack.isIn(ItemTags.VILLAGER_PLANTABLE_SEEDS));}

    @Override
    protected void fillRecipes() {
        Int2ObjectMap<TradeOffers.Factory[]> int2ObjectMap2 = DwarfTrades.TRADES.get(this.getProfession());
        if (int2ObjectMap2 == null || int2ObjectMap2.isEmpty()) {
            return;
        }
        TradeOffers.Factory[] factorys = int2ObjectMap2.get(this.getProfessionLevel());
        if (factorys == null) {
            return;
        }
        TradeOfferList tradeOfferList = this.getOffers();
        this.fillRecipesFromPool(tradeOfferList, factorys, 2);
    }

    public void talkWithVillager(ServerWorld world, DwarfEntity dwarf, long time) {
        if ((time < this.gossipStartTime || time >= this.gossipStartTime + 1200L) && (time < dwarf.gossipStartTime || time >= dwarf.gossipStartTime + 1200L)) {
            this.gossip.shareGossipFrom(dwarf.gossip, this.random, 10);
            this.gossipStartTime = time;
            dwarf.gossipStartTime = time;
            this.summonGolem(world, time, 5);
        }
    }

    private void decayGossip() {
        long l = this.getWorld().getTime();
        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = l;
        } else if (l >= this.lastGossipDecayTime + 24000L) {
            this.gossip.decay();
            this.lastGossipDecayTime = l;
        }
    }

    @Override
    public void onInteractionWith(EntityInteraction interaction, Entity entity) {
        if (interaction == EntityInteraction.ZOMBIE_VILLAGER_CURED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_POSITIVE, 20);
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_POSITIVE, 25);
        } else if (interaction == EntityInteraction.TRADE) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.TRADING, 2);
        } else if (interaction == EntityInteraction.VILLAGER_HURT) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_NEGATIVE, 25);
        } else if (interaction == EntityInteraction.VILLAGER_KILLED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_NEGATIVE, 25);
        }
    }

    @Override
    public int getExperience() {
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
    public VillagerGossips getGossip() {
        return this.gossip;
    }
    //conversion from zombie not used
    public void readGossipDataNbt(NbtElement nbt) {
        this.gossip.deserialize(new Dynamic<NbtElement>(NbtOps.INSTANCE, nbt));
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    public void sleep(BlockPos pos) {
        super.sleep(pos);
        this.brain.remember(MemoryModuleType.LAST_SLEPT, this.getWorld().getTime());
        this.brain.forget(MemoryModuleType.WALK_TARGET);
        this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void wakeUp() {
        super.wakeUp();
        this.brain.remember(MemoryModuleType.LAST_WOKEN, this.getWorld().getTime());
    }
}
