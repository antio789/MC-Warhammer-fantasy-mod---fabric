package warhammermod.Entities.Living.AImanager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.poi.PointOfInterestTypes;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.*;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.Optional;


public class DwarfVillagerTasks {
   /*
   public static ImmutableList<Pair<Integer, ? extends MultiTickTask<? super DwarfEntity>>> createCoreTasks(DwarfProfession profession, float p_220638_1_) {
      return ImmutableList.of(Pair.of(0, new StayAboveWaterTask(0.8F)), Pair.of(0, OpenDoorsTask().create()), Pair.of(0, new LookAroundTask(45, 90)), Pair.of(0, new WHPanicTask()), Pair.of(0, new WakeUpTask()), Pair.of(0, new HideWhenBellRingsTask()), Pair.of(0, new StartRaidTask()), Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getPointOfInterest(), MemoryModuleType.JOB_SITE)), Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getPointOfInterest(), MemoryModuleType.POTENTIAL_JOB_SITE)), Pair.of(1, new WanderAroundTask()), Pair.of(2, new WHCompetitorScan(profession)), Pair.of(3, new WHTradeTask_LookAndFollowTradingPlayerSink(p_220638_1_)), Pair.of(5, new WalkToNearestVisibleWantedItemTask<LivingEntity>(p_220638_1_, false, 4)), Pair.of(6, new FindPointOfInterestTask(profession.getPointOfInterest(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())), Pair.of(7, new WHFindPotentialJobTask(p_220638_1_)), Pair.of(8, new WHFindJobTask_yieldjobsite(p_220638_1_)), Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME, false, Optional.of((byte)14))), Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14))), Pair.of(10, new WHAssignProfessionTask()), Pair.of(10, new WHResetProfession()));
   }
   */
   public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createCoreTasks(DwarfProfessionRecord profession, float speed) {
      return ImmutableList.of(Pair.of(0, new StayAboveWaterTask(0.8f)),
              Pair.of(0, OpenDoorsTask.create()), Pair.of(0, new UpdateLookControlTask(45, 90)),
              Pair.of(0, new PanicTaskDwarf()), Pair.of(0, WakeUpTask.create()),
              Pair.of(0, HideWhenBellRingsTask.create()), Pair.of(0, StartRaidTask.create()),
              Pair.of(0, ForgetCompletedPointOfInterestTask.create(profession.heldWorkstation(), MemoryModuleType.JOB_SITE)),
              Pair.of(0, ForgetCompletedPointOfInterestTask.create(profession.acquirableWorkstation(), MemoryModuleType.POTENTIAL_JOB_SITE)),
              Pair.of(1, new MoveToTargetTask()), Pair.of(2, WorkStationCompetitionTaskDwarf.create()),
              Pair.of(3, new FollowCustomerTaskDwarf(speed)), new Pair[]{Pair.of(5, WalkTowardsNearestVisibleWantedItemTask.create(speed, false, 4)),
                      Pair.of(6, FindPointOfInterestTask.create(profession.acquirableWorkstation(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())),
                      Pair.of(7, new WalkTowardJobSiteTaskDwarf(speed)), Pair.of(8, TakeJobSiteTaskDwarf.create(speed)),
                      Pair.of(10, FindPointOfInterestTask.create(poiType -> poiType.matchesKey(PointOfInterestTypes.HOME), MemoryModuleType.HOME, false, Optional.of((byte)14))),
                      Pair.of(10, FindPointOfInterestTask.create(poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING), MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14))), Pair.of(10, GoToWorkTaskDwarf.create()), Pair.of(10, LoseJobOnSiteLossTaskDwarf.create())});
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createWorkTasks(DwarfProfessionRecord profession, float speed) {
      VillagerWorkTaskDwarf villagerWorkTask = profession == DwarfProfessionRecord.FARMER ? new FarmerWorkTaskDwarf() : new VillagerWorkTaskDwarf();
      return ImmutableList.of(createBusyFollowTask(), Pair.of(5, new RandomTask(ImmutableList.of(Pair.of(villagerWorkTask, 7), Pair.of(GoAroundTask.create(MemoryModuleType.JOB_SITE, 0.4f, 4), 2), Pair.of(GoToPosTask.create(MemoryModuleType.JOB_SITE, 0.4f, 1, 10), 5), Pair.of(GoToSecondaryPositionTaskDwarf.create(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6, MemoryModuleType.JOB_SITE), 5), Pair.of(new FarmerVillagerTaskDwarf(), profession == DwarfProfessionRecord.FARMER ? 2 : 5), Pair.of(new BoneMealTaskDwarf(), profession == DwarfProfessionRecord.FARMER ? 4 : 7)))), Pair.of(10, new HoldTradeOffersTaskDwarf(400, 1600)), Pair.of(10, FindInteractionTargetTask.create(EntityType.PLAYER, 4)), Pair.of(2, VillagerWalkTowardsTaskDwarf.create(MemoryModuleType.JOB_SITE, speed, 9, 100, 1200)), Pair.of(3, new GiveGiftsToHeroTaskDwarf(100)), Pair.of(99, ScheduleActivityTask.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createPlayTasks(float speed) {
      return ImmutableList.of(Pair.of(0, new MoveToTargetTask(80, 120)), createFreeFollowTask(), Pair.of(5, PlayWithVillagerBabiesTask.create()), Pair.of(5, new RandomTask(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(Pair.of(FindEntityTask.create((EntityType)Entityinit.DWARF, (int)8, (MemoryModuleType)MemoryModuleType.INTERACTION_TARGET, (float)speed, (int)2), 2), Pair.of(FindEntityTask.create((EntityType)EntityType.CAT, (int)8, (MemoryModuleType)MemoryModuleType.INTERACTION_TARGET, (float)speed, (int)2), 1), Pair.of(GoToPointOfInterestTask.create((float)speed), 1), Pair.of(GoToLookTargetTask.create((float)speed, (int)2), 1), Pair.of(new JumpInBedTask(speed), 2), Pair.of(new WaitTask(20, 40), 2)))), Pair.of(99, ScheduleActivityTask.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createRestTasks( float speed) {
      return ImmutableList.of(Pair.of(2, VillagerWalkTowardsTaskDwarf.create(MemoryModuleType.HOME, speed, 1, 150, 1200)), Pair.of(3, ForgetCompletedPointOfInterestTask.create(poiType -> poiType.matchesKey(PointOfInterestTypes.HOME), MemoryModuleType.HOME)), Pair.of(3, new SleepTask()), Pair.of(5, new RandomTask(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(Pair.of(GoToHomeTask.create((float)speed), 1), Pair.of(GoIndoorsTask.create((float)speed), 4), Pair.of(GoToPointOfInterestTaskDwarf.create((float)speed, (int)4), 2), Pair.of(new WaitTask(20, 40), 2)))), createBusyFollowTask(), Pair.of(99, ScheduleActivityTask.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createMeetTasks( float speed) {
      return ImmutableList.of(Pair.of(2, Tasks.pickRandomly(ImmutableList.of(Pair.of(GoAroundTask.create((MemoryModuleType)MemoryModuleType.MEETING_POINT, (float)0.4f, (int)40), 2), Pair.of(MeetVillagerTaskDwarf.create(), 2)))), Pair.of(10, new HoldTradeOffersTaskDwarf(400, 1600)), Pair.of(10, FindInteractionTargetTask.create((EntityType)EntityType.PLAYER, (int)4)), Pair.of(2, VillagerWalkTowardsTaskDwarf.create((MemoryModuleType)MemoryModuleType.MEETING_POINT, (float)speed, (int)6, (int)100, (int)200)), Pair.of(3, new GiveGiftsToHeroTaskDwarf(100)), Pair.of(3, ForgetCompletedPointOfInterestTask.create(poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING), (MemoryModuleType)MemoryModuleType.MEETING_POINT)), Pair.of(3, new CompositeTask(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE, ImmutableList.of(Pair.of(new GatherItemsVillagerTaskDwarf(), 1)))), createFreeFollowTask(), Pair.of(99, ScheduleActivityTask.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createIdleTasks( float speed) {
      return ImmutableList.of(Pair.of(2, new RandomTask(ImmutableList.of(Pair.of(FindEntityTask.create((EntityType)Entityinit.DWARF, (int)8, (MemoryModuleType)MemoryModuleType.INTERACTION_TARGET, (float)speed, (int)2), 2), Pair.of(FindEntityTask.create((EntityType)Entityinit.DWARF, (int)8, PassiveEntity::isReadyToBreed, PassiveEntity::isReadyToBreed, (MemoryModuleType)MemoryModuleType.BREED_TARGET, (float)speed, (int)2), 1), Pair.of(FindEntityTask.create((EntityType)EntityType.CAT, (int)8, (MemoryModuleType)MemoryModuleType.INTERACTION_TARGET, (float)speed, (int)2), 1), Pair.of(GoToPointOfInterestTask.create((float)speed), 1), Pair.of(GoToLookTargetTask.create((float)speed, (int)2), 1), Pair.of(new JumpInBedTask(speed), 1), Pair.of(new WaitTask(30, 60), 1)))), Pair.of(3, new GiveGiftsToHeroTaskDwarf(100)), Pair.of(3, FindInteractionTargetTask.create((EntityType)EntityType.PLAYER, (int)4)), Pair.of(3, new HoldTradeOffersTaskDwarf(400, 1600)), Pair.of(3, new CompositeTask(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE, ImmutableList.of(Pair.of(new GatherItemsVillagerTaskDwarf(), 1)))), Pair.of(3, new CompositeTask(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE, ImmutableList.of(Pair.of(new VillagerBreedTaskDwarf(), 1)))), createFreeFollowTask(), Pair.of(99, ScheduleActivityTask.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createPanicTasks( float speed) {
      float f = speed * 1.5f;
      return ImmutableList.of(Pair.of(0, StopPanickingTask.create()),
              Pair.of(1, GoToRememberedPositionTask.createEntityBased(MemoryModuleType.NEAREST_HOSTILE, (float)f, (int)6, (boolean)false)),
              Pair.of(1, GoToRememberedPositionTask.createEntityBased(MemoryModuleType.HURT_BY_ENTITY, (float)f, (int)6, (boolean)false)),
              Pair.of(3, GoToPointOfInterestTask.create((float)f, (int)2, (int)2)),
              createBusyFollowTask());
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createPreRaidTasks( float speed) {
      return ImmutableList.of(Pair.of(0, RingBellTask.create()), Pair.of(0, Tasks.pickRandomly(ImmutableList.of(Pair.of(VillagerWalkTowardsTaskDwarf.create((MemoryModuleType)MemoryModuleType.MEETING_POINT, (float)(speed * 1.5f), (int)2, (int)150, (int)200), 6), Pair.of(GoToPointOfInterestTask.create((float)(speed * 1.5f)), 2)))), createBusyFollowTask(), Pair.of(99, EndRaidTask.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createRaidTasks(float speed) {
      return ImmutableList.of(Pair.of(0, TaskTriggerer.runIf((TaskRunnable)TaskTriggerer.predicate(DwarfVillagerTasks::wonRaid), (TaskRunnable)Tasks.pickRandomly(ImmutableList.of(Pair.of(SeekSkyTask.create((float)speed), 5), Pair.of(GoToPointOfInterestTask.create((float)(speed * 1.1f)), 2))))), Pair.of(0, new CelebrateRaidWinTaskDwarf(600, 600)), Pair.of(2, TaskTriggerer.runIf((TaskRunnable)TaskTriggerer.predicate(DwarfVillagerTasks::hasActiveRaid), (TaskRunnable)HideInHomeTask.create((int)24, (float)(speed * 1.4f), (int)1))), createBusyFollowTask(), Pair.of(99, EndRaidTask.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createHideTasks(float speed) {
      int i = 2;
      return ImmutableList.of(Pair.of(0, ForgetBellRingTask.create((int)15, (int)3)), Pair.of(1, HideInHomeTask.create((int)32, (float)(speed * 1.25f), (int)2)), createBusyFollowTask());
   }

   private static Pair<Integer, Task<LivingEntity>> createFreeFollowTask() {
      return Pair.of(5, new RandomTask(ImmutableList.of(Pair.of(LookAtMobTask.create((EntityType)EntityType.CAT, (float)8.0f), 8), Pair.of(LookAtMobTask.create((EntityType)EntityType.VILLAGER, (float)8.0f), 2), Pair.of(LookAtMobTask.create((EntityType)EntityType.PLAYER, (float)8.0f), 2), Pair.of(LookAtMobTask.create((SpawnGroup)SpawnGroup.CREATURE, (float)8.0f), 1), Pair.of(LookAtMobTask.create((SpawnGroup)SpawnGroup.WATER_CREATURE, (float)8.0f), 1), Pair.of(LookAtMobTask.create((SpawnGroup)SpawnGroup.AXOLOTLS, (float)8.0f), 1), Pair.of(LookAtMobTask.create((SpawnGroup)SpawnGroup.UNDERGROUND_WATER_CREATURE, (float)8.0f), 1), Pair.of(LookAtMobTask.create((SpawnGroup)SpawnGroup.WATER_AMBIENT, (float)8.0f), 1), Pair.of(LookAtMobTask.create((SpawnGroup)SpawnGroup.MONSTER, (float)8.0f), 1), Pair.of(new WaitTask(30, 60), 2))));
   }

   private static boolean hasActiveRaid(ServerWorld world, LivingEntity entity) {
      Raid raid = world.getRaidAt(entity.getBlockPos());
      return raid != null && raid.isActive() && !raid.hasWon() && !raid.hasLost();
   }

   private static boolean wonRaid(ServerWorld world, LivingEntity entity) {
      Raid raid = world.getRaidAt(entity.getBlockPos());
      return raid != null && raid.hasWon();
   }


   private static Pair<Integer, Task<LivingEntity>> createBusyFollowTask() {
      return Pair.of(5, new RandomTask(ImmutableList.of(Pair.of(LookAtMobTask.create(Entityinit.DWARF, 8.0f), 2), Pair.of(LookAtMobTask.create(EntityType.PLAYER, 8.0f), 2), Pair.of(new WaitTask(30, 60), 8))));
   }
}