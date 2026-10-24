package warhammermod.Entities.Living.AImanager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.GoToWantedItem;
import net.minecraft.world.entity.ai.behavior.InsideBrownianWalk;
import net.minecraft.world.entity.ai.behavior.InteractWith;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.JumpOnBed;
import net.minecraft.world.entity.ai.behavior.LocateHidingPlace;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToSkySeeingSpot;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.PlayTagWithOtherKids;
import net.minecraft.world.entity.ai.behavior.ReactToBell;
import net.minecraft.world.entity.ai.behavior.ResetRaidStatus;
import net.minecraft.world.entity.ai.behavior.RingBell;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetClosestHomeAsWalkTarget;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetHiddenState;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.SetRaidStatus;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.SleepInBed;
import net.minecraft.world.entity.ai.behavior.StrollAroundPoi;
import net.minecraft.world.entity.ai.behavior.StrollToPoi;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.behavior.TriggerGate;
import net.minecraft.world.entity.ai.behavior.UpdateActivityFromSchedule;
import net.minecraft.world.entity.ai.behavior.ValidateNearbyPoi;
import net.minecraft.world.entity.ai.behavior.VillageBoundRandomStroll;
import net.minecraft.world.entity.ai.behavior.VillagerCalmDown;
import net.minecraft.world.entity.ai.behavior.WakeUp;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.raid.Raid;
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
   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createCoreTasks(DwarfProfessionRecord profession, float speed) {
      return ImmutableList.of(Pair.of(0, new Swim(0.8f)),
              Pair.of(0, InteractWithDoor.create()), Pair.of(0, new LookAtTargetSink(45, 90)),
              Pair.of(0, new PanicTaskDwarf()), Pair.of(0, WakeUp.create()),
              Pair.of(0, ReactToBell.create()), Pair.of(0, SetRaidStatus.create()),
              Pair.of(0, ValidateNearbyPoi.create(profession.heldWorkstation(), MemoryModuleType.JOB_SITE)),
              Pair.of(0, ValidateNearbyPoi.create(profession.acquirableWorkstation(), MemoryModuleType.POTENTIAL_JOB_SITE)),
              Pair.of(1, new MoveToTargetSink()), Pair.of(2, WorkStationCompetitionTaskDwarf.create()),
              Pair.of(3, new FollowCustomerTaskDwarf(speed)), new Pair[]{Pair.of(5, GoToWantedItem.create(speed, false, 4)),
                      Pair.of(6, AcquirePoi.create(profession.acquirableWorkstation(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())),
                      Pair.of(7, new WalkTowardJobSiteTaskDwarf(speed)), Pair.of(8, TakeJobSiteTaskDwarf.create(speed)),
                      Pair.of(10, AcquirePoi.create(poiType -> poiType.is(PoiTypes.HOME), MemoryModuleType.HOME, false, Optional.of((byte)14))),
                      Pair.of(10, AcquirePoi.create(poiType -> poiType.is(PoiTypes.MEETING), MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14))), Pair.of(10, GoToWorkTaskDwarf.create()), Pair.of(10, LoseJobOnSiteLossTaskDwarf.create())});
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createWorkTasks(DwarfProfessionRecord profession, float speed) {
      VillagerWorkTaskDwarf villagerWorkTask = profession == DwarfProfessionRecord.FARMER ? new FarmerWorkTaskDwarf() : new VillagerWorkTaskDwarf();
      return ImmutableList.of(createBusyFollowTask(), Pair.of(5, new RunOne(ImmutableList.of(Pair.of(villagerWorkTask, 7), Pair.of(StrollAroundPoi.create(MemoryModuleType.JOB_SITE, 0.4f, 4), 2), Pair.of(StrollToPoi.create(MemoryModuleType.JOB_SITE, 0.4f, 1, 10), 5), Pair.of(GoToSecondaryPositionTaskDwarf.create(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6, MemoryModuleType.JOB_SITE), 5), Pair.of(new FarmerVillagerTaskDwarf(), profession == DwarfProfessionRecord.FARMER ? 2 : 5), Pair.of(new BoneMealTaskDwarf(), profession == DwarfProfessionRecord.FARMER ? 4 : 7)))), Pair.of(10, new HoldTradeOffersTaskDwarf(400, 1600)), Pair.of(10, SetLookAndInteract.create(EntityType.PLAYER, 4)), Pair.of(2, VillagerWalkTowardsTaskDwarf.create(MemoryModuleType.JOB_SITE, speed, 9, 100, 1200)), Pair.of(3, new GiveGiftsToHeroTaskDwarf(100)), Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createPlayTasks(float speed) {
      return ImmutableList.of(Pair.of(0, new MoveToTargetSink(80, 120)), createFreeFollowTask(), Pair.of(5, PlayTagWithOtherKids.create()), Pair.of(5, new RunOne(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(InteractWith.of((EntityType)Entityinit.DWARF, (int)8, (MemoryModuleType)MemoryModuleType.INTERACTION_TARGET, (float)speed, (int)2), 2), Pair.of(InteractWith.of((EntityType)EntityType.CAT, (int)8, (MemoryModuleType)MemoryModuleType.INTERACTION_TARGET, (float)speed, (int)2), 1), Pair.of(VillageBoundRandomStroll.create((float)speed), 1), Pair.of(SetWalkTargetFromLookTarget.create((float)speed, (int)2), 1), Pair.of(new JumpOnBed(speed), 2), Pair.of(new DoNothing(20, 40), 2)))), Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createRestTasks( float speed) {
      return ImmutableList.of(Pair.of(2, VillagerWalkTowardsTaskDwarf.create(MemoryModuleType.HOME, speed, 1, 150, 1200)), Pair.of(3, ValidateNearbyPoi.create(poiType -> poiType.is(PoiTypes.HOME), MemoryModuleType.HOME)), Pair.of(3, new SleepInBed()), Pair.of(5, new RunOne(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(SetClosestHomeAsWalkTarget.create((float)speed), 1), Pair.of(InsideBrownianWalk.create((float)speed), 4), Pair.of(GoToPointOfInterestTaskDwarf.create((float)speed, (int)4), 2), Pair.of(new DoNothing(20, 40), 2)))), createBusyFollowTask(), Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createMeetTasks( float speed) {
      return ImmutableList.of(Pair.of(2, TriggerGate.triggerOneShuffled(ImmutableList.of(Pair.of(StrollAroundPoi.create((MemoryModuleType)MemoryModuleType.MEETING_POINT, (float)0.4f, (int)40), 2), Pair.of(MeetVillagerTaskDwarf.create(), 2)))), Pair.of(10, new HoldTradeOffersTaskDwarf(400, 1600)), Pair.of(10, SetLookAndInteract.create((EntityType)EntityType.PLAYER, (int)4)), Pair.of(2, VillagerWalkTowardsTaskDwarf.create((MemoryModuleType)MemoryModuleType.MEETING_POINT, (float)speed, (int)6, (int)100, (int)200)), Pair.of(3, new GiveGiftsToHeroTaskDwarf(100)), Pair.of(3, ValidateNearbyPoi.create(poiType -> poiType.is(PoiTypes.MEETING), (MemoryModuleType)MemoryModuleType.MEETING_POINT)), Pair.of(3, new GateBehavior(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new GatherItemsVillagerTaskDwarf(), 1)))), createFreeFollowTask(), Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createIdleTasks( float speed) {
      return ImmutableList.of(Pair.of(2, new RunOne(ImmutableList.of(Pair.of(InteractWith.of((EntityType)Entityinit.DWARF, (int)8, (MemoryModuleType)MemoryModuleType.INTERACTION_TARGET, (float)speed, (int)2), 2), Pair.of(InteractWith.of((EntityType)Entityinit.DWARF, (int)8, AgeableMob::canBreed, AgeableMob::canBreed, (MemoryModuleType)MemoryModuleType.BREED_TARGET, (float)speed, (int)2), 1), Pair.of(InteractWith.of((EntityType)EntityType.CAT, (int)8, (MemoryModuleType)MemoryModuleType.INTERACTION_TARGET, (float)speed, (int)2), 1), Pair.of(VillageBoundRandomStroll.create((float)speed), 1), Pair.of(SetWalkTargetFromLookTarget.create((float)speed, (int)2), 1), Pair.of(new JumpOnBed(speed), 1), Pair.of(new DoNothing(30, 60), 1)))), Pair.of(3, new GiveGiftsToHeroTaskDwarf(100)), Pair.of(3, SetLookAndInteract.create((EntityType)EntityType.PLAYER, (int)4)), Pair.of(3, new HoldTradeOffersTaskDwarf(400, 1600)), Pair.of(3, new GateBehavior(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new GatherItemsVillagerTaskDwarf(), 1)))), Pair.of(3, new GateBehavior(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new VillagerBreedTaskDwarf(), 1)))), createFreeFollowTask(), Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createPanicTasks( float speed) {
      float f = speed * 1.5f;
      return ImmutableList.of(Pair.of(0, VillagerCalmDown.create()),
              Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_HOSTILE, (float)f, (int)6, (boolean)false)),
              Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, (float)f, (int)6, (boolean)false)),
              Pair.of(3, VillageBoundRandomStroll.create((float)f, (int)2, (int)2)),
              createBusyFollowTask());
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createPreRaidTasks( float speed) {
      return ImmutableList.of(Pair.of(0, RingBell.create()), Pair.of(0, TriggerGate.triggerOneShuffled(ImmutableList.of(Pair.of(VillagerWalkTowardsTaskDwarf.create((MemoryModuleType)MemoryModuleType.MEETING_POINT, (float)(speed * 1.5f), (int)2, (int)150, (int)200), 6), Pair.of(VillageBoundRandomStroll.create((float)(speed * 1.5f)), 2)))), createBusyFollowTask(), Pair.of(99, ResetRaidStatus.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createRaidTasks(float speed) {
      return ImmutableList.of(Pair.of(0, BehaviorBuilder.sequence((Trigger)BehaviorBuilder.triggerIf(DwarfVillagerTasks::wonRaid), (Trigger)TriggerGate.triggerOneShuffled(ImmutableList.of(Pair.of(MoveToSkySeeingSpot.create((float)speed), 5), Pair.of(VillageBoundRandomStroll.create((float)(speed * 1.1f)), 2))))), Pair.of(0, new CelebrateRaidWinTaskDwarf(600, 600)), Pair.of(2, BehaviorBuilder.sequence((Trigger)BehaviorBuilder.triggerIf(DwarfVillagerTasks::hasActiveRaid), (Trigger)LocateHidingPlace.create((int)24, (float)(speed * 1.4f), (int)1))), createBusyFollowTask(), Pair.of(99, ResetRaidStatus.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createHideTasks(float speed) {
      int i = 2;
      return ImmutableList.of(Pair.of(0, SetHiddenState.create((int)15, (int)3)), Pair.of(1, LocateHidingPlace.create((int)32, (float)(speed * 1.25f), (int)2)), createBusyFollowTask());
   }

   private static Pair<Integer, BehaviorControl<LivingEntity>> createFreeFollowTask() {
      return Pair.of(5, new RunOne(ImmutableList.of(Pair.of(SetEntityLookTarget.create((EntityType)EntityType.CAT, (float)8.0f), 8), Pair.of(SetEntityLookTarget.create((EntityType)EntityType.VILLAGER, (float)8.0f), 2), Pair.of(SetEntityLookTarget.create((EntityType)EntityType.PLAYER, (float)8.0f), 2), Pair.of(SetEntityLookTarget.create((MobCategory)MobCategory.CREATURE, (float)8.0f), 1), Pair.of(SetEntityLookTarget.create((MobCategory)MobCategory.WATER_CREATURE, (float)8.0f), 1), Pair.of(SetEntityLookTarget.create((MobCategory)MobCategory.AXOLOTLS, (float)8.0f), 1), Pair.of(SetEntityLookTarget.create((MobCategory)MobCategory.UNDERGROUND_WATER_CREATURE, (float)8.0f), 1), Pair.of(SetEntityLookTarget.create((MobCategory)MobCategory.WATER_AMBIENT, (float)8.0f), 1), Pair.of(SetEntityLookTarget.create((MobCategory)MobCategory.MONSTER, (float)8.0f), 1), Pair.of(new DoNothing(30, 60), 2))));
   }

   private static boolean hasActiveRaid(ServerLevel world, LivingEntity entity) {
      Raid raid = world.getRaidAt(entity.blockPosition());
      return raid != null && raid.isActive() && !raid.isVictory() && !raid.isLoss();
   }

   private static boolean wonRaid(ServerLevel world, LivingEntity entity) {
      Raid raid = world.getRaidAt(entity.blockPosition());
      return raid != null && raid.isVictory();
   }


   private static Pair<Integer, BehaviorControl<LivingEntity>> createBusyFollowTask() {
      return Pair.of(5, new RunOne(ImmutableList.of(Pair.of(SetEntityLookTarget.create(Entityinit.DWARF, 8.0f), 2), Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0f), 2), Pair.of(new DoNothing(30, 60), 8))));
   }
}