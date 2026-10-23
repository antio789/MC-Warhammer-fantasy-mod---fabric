package warhammermod.Entities.Living;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import warhammermod.Entities.Living.AImanager.DwarfCombattasks;
import warhammermod.Entities.Living.AImanager.DwarfVillagerTasks;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor.LordLastSeenSensor;
import warhammermod.Entities.Living.AImanager.Data.TradeFactory;
import warhammermod.Entities.Living.AImanager.Data.DwarfTrades;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.WHRegistry;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class DwarfEntity extends AbstractVillager {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final EntityDataAccessor<Integer> Profession = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> Level = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);

    public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of(ItemsInitProxy.BEER, 8, Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
    private static final ImmutableSet<Item> WANTED_ITEMS = ImmutableSet.of(ItemsInitProxy.BEER, Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, Items.BEETROOT_SEEDS);

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
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD,
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
            MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.UNIVERSAL_ANGER, MemoryModuleType.ANGRY_AT
    );
    private static final ImmutableList<SensorType<? extends Sensor<? super DwarfEntity>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY,
            WHRegistry.Hostiles, WHRegistry.VISIBLE_VILLAGER_BABIES, WHRegistry.SECONDARY_POIS, WHRegistry.Lord_LastSeen);

    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<DwarfEntity, Holder<PoiType>>> POINTS_OF_INTEREST =
            ImmutableMap.of(MemoryModuleType.HOME, (dwarf, holder) -> holder.is(PoiTypes.HOME),
                    MemoryModuleType.JOB_SITE, (dwarf, holder) -> dwarf.getProfession().heldWorkstation().test(holder),
                    MemoryModuleType.POTENTIAL_JOB_SITE, (dwarf, holder) -> holder.is(DwarfProfessionRecord.ACQUIRABLE_JOB_SITE),
                    MemoryModuleType.MEETING_POINT, (dwarf, holder) -> holder.is(PoiTypes.MEETING));

    /**
     * initialization
     */

    public DwarfEntity(Level world) {
        super(Entityinit.DWARF, world);
    }

    public DwarfEntity(EntityType<? extends DwarfEntity> entityType, Level world) {
        super(entityType, world);
        ((net.minecraft.world.entity.ai.navigation.GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);
        this.setCanPickUpLoot(true);
        this.setVillagerDataBase();
        this.setEquipment(this.getProfession());
    }

    @Override
    public Brain<DwarfEntity> getBrain() {
        return (Brain<DwarfEntity>) super.getBrain();
    }

    public static Brain.Provider<DwarfEntity> createDwarfBrainProvider() {
        return Brain.provider(MEMORY_MODULES, SENSOR_TYPES, DwarfEntity::createDwarfActivities);
    }

    private static java.util.List<net.minecraft.world.entity.ai.ActivityData<DwarfEntity>> createDwarfActivities(DwarfEntity dwarf) {
        DwarfProfessionRecord profession = dwarf.getProfession();
        List<net.minecraft.world.entity.ai.ActivityData<DwarfEntity>> activities = new java.util.ArrayList<>();
        if (dwarf.isBaby()) {
            entityActivity(activities, net.minecraft.world.entity.schedule.Activity.PLAY, DwarfVillagerTasks.createPlayTasks(0.5F));
            entityActivity(activities, net.minecraft.world.entity.schedule.Activity.PANIC, DwarfCombattasks.createPanicTasks(0.5F));
        } else {
            entityActivity(activities, net.minecraft.world.entity.schedule.Activity.PANIC, DwarfCombattasks.createPanicTasks(0.5F));
            entityActivity(activities, net.minecraft.world.entity.schedule.Activity.WORK, DwarfVillagerTasks.createWorkTasks(profession, 0.5F),
                    ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
        }
        entityActivity(activities, net.minecraft.world.entity.schedule.Activity.CORE, DwarfVillagerTasks.createCoreTasks(profession, 0.5F));
        entityActivity(activities, net.minecraft.world.entity.schedule.Activity.MEET, DwarfVillagerTasks.createMeetTasks(0.5f),
                ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
        entityActivity(activities, net.minecraft.world.entity.schedule.Activity.REST, DwarfVillagerTasks.createRestTasks(0.5f));
        entityActivity(activities, net.minecraft.world.entity.schedule.Activity.IDLE, DwarfVillagerTasks.createIdleTasks(0.5f));
        entityActivity(activities, net.minecraft.world.entity.schedule.Activity.PRE_RAID, DwarfVillagerTasks.createPreRaidTasks(0.5f));
        entityActivity(activities, net.minecraft.world.entity.schedule.Activity.RAID, DwarfVillagerTasks.createRaidTasks(0.5f));
        entityActivity(activities, net.minecraft.world.entity.schedule.Activity.HIDE, DwarfVillagerTasks.createHideTasks(0.5f));
        return activities;
    }

    private static void entityActivity(java.util.List<net.minecraft.world.entity.ai.ActivityData<DwarfEntity>> activities,
                                       net.minecraft.world.entity.schedule.Activity activity,
                                       ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> behaviors) {
        activities.add(net.minecraft.world.entity.ai.ActivityData.create(activity, behaviors));
    }

    private static void entityActivity(java.util.List<net.minecraft.world.entity.ai.ActivityData<DwarfEntity>> activities,
                                       net.minecraft.world.entity.schedule.Activity activity,
                                       ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> behaviors,
                                       java.util.Set<Pair<MemoryModuleType<?>, MemoryStatus>> conditions) {
        activities.add(net.minecraft.world.entity.ai.ActivityData.create(activity, behaviors, conditions));
    }

    private void registerBrainGoals(Brain<DwarfEntity> entityBrain) {
        if (this.isBaby()) {
            entityBrain.setSchedule(net.minecraft.world.attribute.EnvironmentAttributes.BABY_VILLAGER_ACTIVITY);
        } else {
            entityBrain.setSchedule(net.minecraft.world.attribute.EnvironmentAttributes.VILLAGER_ACTIVITY);
        }
        entityBrain.setCoreActivities(ImmutableSet.of(net.minecraft.world.entity.schedule.Activity.CORE));
        entityBrain.setDefaultActivity(net.minecraft.world.entity.schedule.Activity.IDLE);
        entityBrain.updateActivityFromSchedule(this.level().environmentAttributes(), this.level().getGameTime(), this.position());
    }

    @Override
    protected Brain<DwarfEntity> makeBrain(Brain.Packed packed) {
        Brain<DwarfEntity> brain = createDwarfBrainProvider().makeBrain(this, packed);
        this.brain = brain;
        registerBrainGoals(brain);
        return brain;
    }

    public void refreshBrain(ServerLevel world) {
        Brain<DwarfEntity> brain = this.getBrain();
        brain.stopAll(world, this);
        this.brain = createDwarfBrainProvider().makeBrain(this, brain.pack());
        registerBrainGoals((Brain<DwarfEntity>) this.brain);
    }

    public boolean isNatural() {
        return this.natural;
    }

    @Override
    protected void customServerAiStep(ServerLevel world) {
        this.getBrain().tick(world, this);
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
        if (this.lastCustomer != null) {
            this.lastCustomer = null;
        }
        if (this.getProfessionID() == DwarfProfessionRecord.Warrior.ID() && this.isTrading()) {
            this.stopTrading();
        }
        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
        super.customServerAiStep(world);
    }

    @Override
    public void tick() {
        super.tick();
        this.decayGossip();
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        if (spawnReason == EntitySpawnReason.BREEDING) {
            this.setVillagerData(DwarfProfessionRecord.Warrior, this.getProfessionLevel());
        }
        if (spawnReason == EntitySpawnReason.STRUCTURE) {
            this.natural = true;
        }
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    /**
     * all custom profession
     */

    public int getProfessionID() {
        return Math.max(this.entityData.get(Profession), 0);
    }

    public void setVillagerData() {
        this.setVillagerData(getProfession(), getProfessionLevel());
    }

    public void setVillagerDataBase() {
        this.setVillagerData(DwarfProfessionRecord.Warrior, 1);
    }

    public void setVillagerData(DwarfProfessionRecord prof, int level) {
        if (getProfession() != prof) {
            this.offers = null;
            this.setEquipment(prof);
        }
        if (entityData.get(Profession) != DwarfProfessionRecord.Lord.ID()) {
            this.entityData.set(Profession, prof.ID());
        }
        this.entityData.set(Level, level);
    }

    public void setVillagerlevel(int level) {
        setVillagerData(getProfession(), level);
    }

    public DwarfProfessionRecord getProfession() {
        return WHRegistry.DWARF_PROFESSIONS.stream().filter(p -> p.ID() == getProfessionID()).findFirst().orElse(DwarfProfessionRecord.Warrior);
    }

    public int getProfessionLevel() {
        return Math.max(this.entityData.get(Level), 1);
    }

    public void setProfession(DwarfProfessionRecord profession, String from) {
        this.setVillagerData(profession, this.getProfessionLevel());
    }

    private final int[] xpforlevel = new int[]{0, 10, 70, 150, 250};

    public int getlevelfromxp(int xp) {
        return haslevel(xp) ? xpforlevel[xp] : 0;
    }

    public boolean haslevel(int level) {
        return level >= 1 && level < 5;
    }

    /**
     * all related to combat capabilities
     */
    protected void setEquipment(DwarfProfessionRecord prof) {
        if (hasItemInSlot(EquipmentSlot.MAINHAND)) {
            this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        if (hasItemInSlot(EquipmentSlot.OFFHAND)) {
            this.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        }
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, new ItemStack(prof.mainhainditem()));
        if (prof.offhanditem() != null) this.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, new ItemStack(prof.offhanditem()));
    }

    @Override
    public boolean isBlocking() {
        if (this.getOffhandItem().getItem() instanceof ShieldItem && this.isAggressive() && this.random.nextFloat() < 0.33) {
            playSound(SoundEvents.SHIELD_BLOCK.value(), 1, 1);
            return true;
        } else return false;
    }

    /**
     * where modifications are needed
     */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(Profession, DwarfProfessionRecord.Warrior.ID());
        builder.define(Level, 1);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putInt("level", this.getProfessionLevel());
        out.putInt("profession", this.getProfessionID());
        out.putByte("FoodLevel", (byte) this.foodLevel);
        out.store("Gossips", GossipContainer.CODEC, this.gossip);
        out.putInt("Xp", this.experience);
        out.putLong("LastRestockdwarf", this.lastRestockTime);
        out.putLong("LastGossipDecay", this.lastGossipDecayTime);
        out.putInt("RestocksTodayDwarf", this.restocksToday);
        if (this.natural) {
            out.putBoolean("AssignProfessionWhenSpawned", true);
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput in) {
        super.readAdditionalSaveData(in);
        int profession = in.getIntOr("profession", DwarfProfessionRecord.Warrior.ID());
        int level = in.getIntOr("level", 1);
        this.entityData.set(Profession, profession);
        this.entityData.set(Level, level);

        this.foodLevel = in.getByteOr("FoodLevel", (byte) 0);
        var gossipData = in.read("Gossips", GossipContainer.CODEC).orElseGet(GossipContainer::new);
        gossipData.getGossipEntries().forEach((uuid, map) -> map.forEach((type, value) -> this.gossip.add(uuid, type, value)));
        this.experience = in.getIntOr("Xp", 0);
        this.lastRestockTime = in.getLongOr("LastRestockDwarf", 0L);
        this.restocksToday = in.getIntOr("RestocksTodayDwarf", 0);
        this.setCanPickUpLoot(true);
        if (this.level() instanceof ServerLevel) {
            this.refreshBrain((ServerLevel) this.level());
        }
        if (in.getBooleanOr("AssignProfessionWhenSpawned", false)) {
            this.natural = true;
        }
    }

    public float getSoundPitch() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.4F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.6F;
    }

    @Nullable
    @Override
    public DwarfEntity getBreedOffspring(ServerLevel world, AgeableMob entity) {
        DwarfEntity dwarfEntity = new DwarfEntity(world);
        dwarfEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(dwarfEntity.blockPosition()), EntitySpawnReason.BREEDING, null);
        return dwarfEntity;
    }

    public void summonGolem(ServerLevel world, long time, int requiredCount) {
        this.SummonLord(world, time, requiredCount);
    }

    public void SummonLord(ServerLevel serverWorld, long time, int requiredCount) {
        if (this.canSummonGolem(time)) {
            AABB axisalignedbb = this.getBoundingBox().inflate(10.0D, 10.0D, 10.0D);
            List<DwarfEntity> list = serverWorld.getEntitiesOfClass(DwarfEntity.class, axisalignedbb);
            List<DwarfEntity> list1 = list.stream().filter(dwarfEntity -> dwarfEntity.canSummonGolem(time)).limit(5L).collect(Collectors.toList());
            if (list1.size() >= requiredCount) {
                if (this.tryspawnlord(serverWorld)) {
                    list.forEach(LordLastSeenSensor::senseLord);
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

    private boolean tryspawnlord(ServerLevel world) {
        int i = Mth.floor(this.getX());
        int j = Mth.floor(this.getY());
        int k = Mth.floor(this.getZ());
        DwarfEntity dwarfEntity = Entityinit.DWARF.create(world, EntitySpawnReason.BREEDING);
        for (int l = 0; l < 15; ++l) {
            int i1 = i + Mth.nextInt(this.random, 2, 6) * Mth.nextInt(this.random, -1, 1);
            int j1 = j + Mth.nextInt(this.random, 2, 6) * Mth.nextInt(this.random, -1, 1);
            int k1 = k + Mth.nextInt(this.random, 2, 6) * Mth.nextInt(this.random, -1, 1);
            BlockPos blockpos = new BlockPos(i1, j1, k1);
            EntityType<?> entityType = dwarfEntity.getType();
            if (!net.minecraft.world.entity.SpawnPlacements.isSpawnPositionOk(entityType, this.level(), blockpos) || !net.minecraft.world.entity.SpawnPlacements.checkSpawnRules(entityType, world, EntitySpawnReason.REINFORCEMENT, blockpos, this.random))
                continue;
            dwarfEntity.setPos(i1, j1, k1);
            if (!this.level().noCollision(dwarfEntity)) continue;
            dwarfEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(dwarfEntity.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            dwarfEntity.setVillagerData(DwarfProfessionRecord.Lord, this.getProfessionLevel());
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
    public boolean wantsToPickUp(ServerLevel level, ItemStack stack) {
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

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (this.level() instanceof ServerLevel) {
            this.refreshBrain((ServerLevel) this.level());
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!itemStack.is(Entityinit.DWARF_SPAWN_EGG) && this.isAlive() && !this.isTrading() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.sayNo();
                return InteractionResult.SUCCESS;
            } else {
                if (!this.level().isClientSide()) {
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

                return InteractionResult.SUCCESS;
            }
        } else {
            return super.mobInteract(player, hand);
        }
    }

    private void sayNo() {
        this.setUnhappyCounter(40);
        if (!this.level().isClientSide()) {
            this.playSound(SoundEvents.VILLAGER_NO);
        }
    }

    @Override
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
            while (var1.hasNext()) {
                MerchantOffer tradeOffer = (MerchantOffer) var1.next();
                tradeOffer.resetSpecialPriceDiff();
            }
        }
    }

    public boolean canRefreshTrades() {
        return true;
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
        MerchantOffers merchantOffers = this.getOffers();
        Player player = this.getTradingPlayer();
        if (player != null && !merchantOffers.isEmpty()) {
            player.sendMerchantOffers(player.containerMenu.containerId, merchantOffers, this.getProfessionLevel(), this.getVillagerXp(), this.showProgressBar(), this.canRefreshTrades());
        }
    }

    private boolean needsRestock() {
        for (MerchantOffer tradeOffer : this.getOffers()) {
            if (!tradeOffer.isOutOfStock()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean canRestock() {
        return this.restocksToday == 0 || this.restocksToday < 2 && this.level().getGameTime() > this.lastRestockTime + 2400L;
    }

    public boolean shouldRestock() {
        long l = this.lastRestockTime + 12000L;
        long m = this.level().getGameTime();
        boolean bl = m > l;
        long n = this.level().getOverworldClockTime();
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
                tradeOffer.addToSpecialPriceDiff(-Mth.floor((float) i * tradeOffer.getPriceMultiplier()));
            }
        }
        if (player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            MobEffectInstance statuseffect = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
            int j = statuseffect.getAmplifier();
            for (MerchantOffer tradeOffer : this.getOffers()) {
                double d = 0.3 + 0.0625 * (double) j;
                int k = (int) Math.floor(d * (double) tradeOffer.getBaseCostA().getCount());
                tradeOffer.addToSpecialPriceDiff(-Math.max(k, 1));
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
        this.playSound(this.getProfession().workSound());
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
    public void die(DamageSource damageSource) {
        LOGGER.info("DwarfVillager {} died, message: '{}'", this, damageSource.getLocalizedDeathMessage(this).getString());
        this.releaseAllTickets();
        super.die(damageSource);
    }

    private void releaseAllTickets() {
        this.releaseTicketFor(MemoryModuleType.HOME);
        this.releaseTicketFor(MemoryModuleType.JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.MEETING_POINT);
    }

    public void releaseTicketFor(MemoryModuleType<GlobalPos> pos) {
        if (!(this.level() instanceof ServerLevel)) {
            return;
        }
        MinecraftServer minecraftServer = ((ServerLevel) this.level()).getServer();
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
            }
        });
    }

    public boolean isReadyToBreed() {
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
                this.foodLevel += integer;
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

    public void setOffers(MerchantOffers offers) {
        this.offers = offers;
    }

    private boolean canLevelUp() {
        int i = this.getProfessionLevel();
        return canLevelUp(i) && this.experience >= getUpperLevelExperience(i);
    }

    private void levelUp() {
        this.setVillagerlevel(this.getProfessionLevel() + 1);
        if (this.level() instanceof ServerLevel serverLevel) {
            this.updateTrades(serverLevel);
        }
    }

    public static int getUpperLevelExperience(int level) {
        return canLevelUp(level) ? LEVEL_BASE_EXPERIENCE[level] : 0;
    }

    public static boolean canLevelUp(int level) {
        return level >= 1 && level < 5;
    }

    private static final int[] LEVEL_BASE_EXPERIENCE = new int[]{0, 10, 70, 150, 250};

    @Override
    protected net.minecraft.network.chat.Component getTypeName() {
        return net.minecraft.network.chat.Component.translatable(this.getType().toShortString() + "." + WHRegistry.DWARF_PROFESSIONS.getResourceKey(this.getProfession()).map(k -> k.identifier().getPath()).orElse("warrior"));
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 12) {
            this.spawnParticlesLocation(net.minecraft.core.particles.ParticleTypes.HEART);
        } else if (status == 13) {
            this.spawnParticlesLocation(net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER);
        } else if (status == 14) {
            this.spawnParticlesLocation(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER);
        } else if (status == 42) {
            this.spawnParticlesLocation(net.minecraft.core.particles.ParticleTypes.SPLASH);
        } else {
            super.handleEntityEvent(status);
        }
    }

    private void spawnParticlesLocation(net.minecraft.core.particles.SimpleParticleType type) {
        // approximated vanilla particle burst
        for (int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level().addParticle(type, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
        }
    }

    @Override
    protected void pickUpItem(ServerLevel level, net.minecraft.world.entity.item.ItemEntity item) {
        net.minecraft.world.entity.npc.InventoryCarrier.pickUpItem(level, this, this, item);
    }

    public boolean wantsToStartBreeding() {
        return this.getAvailableFood() >= 24;
    }

    public boolean canBreed() {
        return this.getAvailableFood() < 12;
    }

    private int getAvailableFood() {
        SimpleContainer simpleInventory = this.getInventory();
        return ITEM_FOOD_VALUES.entrySet().stream().mapToInt(item -> simpleInventory.countItem(item.getKey()) * item.getValue()).sum();
    }

    public boolean hasSeedToPlant() {
        return this.getInventory().hasAnyMatching(stack -> stack.is(net.minecraft.tags.ItemTags.VILLAGER_PLANTABLE_SEEDS));
    }

    @Override
    protected void updateTrades(ServerLevel serverLevel) {
        Int2ObjectMap<TradeFactory[]> int2ObjectMap2 = DwarfTrades.TRADES.get(this.getProfession());
        if (int2ObjectMap2 == null || int2ObjectMap2.isEmpty()) {
            return;
        }
        TradeFactory[] factorys = int2ObjectMap2.get(this.getProfessionLevel());
        if (factorys == null) {
            return;
        }
        MerchantOffers merchantOffers = this.getOffers();
        for (int i = 0; i < 2 && merchantOffers.size() < factorys.length * 2; ++i) {
            TradeFactory factory = factorys[this.random.nextInt(factorys.length)];
            MerchantOffer offer = factory.create(this.random);
            if (!merchantOffers.contains(offer)) {
                merchantOffers.add(offer);
            }
        }
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

    public GossipContainer getGossip() {
        return this.gossip;
    }

    public void sleepUnder(BlockPos pos) {
        this.startSleeping(pos);
        this.brain.setMemory(MemoryModuleType.LAST_SLEPT, this.level().getGameTime());
        this.brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        this.brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    public void wakeUp() {
        this.stopSleeping();
        this.brain.setMemory(MemoryModuleType.LAST_WOKEN, this.level().getGameTime());
    }

    /**
     * Proxy to avoid class-init cycle: ItemsInit references DwarfTrades which references professions which need the registry.
     */
    private static final class ItemsInitProxy {
        static final Item BEER = warhammermod.Items.ItemsInit.BEER;
    }
}
