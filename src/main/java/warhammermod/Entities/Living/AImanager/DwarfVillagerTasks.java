package warhammermod.Entities.Living.AImanager;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BecomePassiveIfMemoryPresent;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EraseMemoryIf;
import net.minecraft.world.entity.ai.behavior.GoToWantedItem;
import net.minecraft.world.entity.ai.behavior.InteractWith;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.InsideBrownianWalk;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.StrollAroundPoi;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.behavior.UpdateActivityFromSchedule;
import net.minecraft.world.entity.ai.behavior.VillagerCalmDown;
import net.minecraft.world.entity.ai.behavior.WakeUp;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.BoneMealTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.CelebrateRaidWinTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.FarmerVillagerTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.FarmerWorkTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.FollowCustomerTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.GatherItemsVillagerTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.GiveGiftsToHeroTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.GoToPointOfInterestTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.GoToSecondaryPositionTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.GoToWorkTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.HoldTradeOffersTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.LoseJobOnSiteLossTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.MeetVillagerTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.PanicTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.StopPanickingTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.TakeJobSiteTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.VillagerBreedTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.VillagerWalkTowardsTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.VillagerWorkTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.WalkTowardJobSiteTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.WorkStationCompetitionTaskDwarf;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.Optional;

public class DwarfVillagerTasks {

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createCoreTasks(DwarfProfessionRecord profession, float speed) {
        return ImmutableList.of(
                Pair.of(0, new Swim(0.8f)),
                Pair.of(0, InteractWithDoor.create()),
                Pair.of(0, new LookAtTargetSink(45, 90)),
                Pair.of(0, new PanicTaskDwarf()),
                Pair.of(0, WakeUp.create()),
                Pair.of(0, VillagerCalmDown.create()),
                Pair.of(0, BecomePassiveIfMemoryPresent.create(MemoryModuleType.HURT_BY, 40)),
                Pair.of(0, EraseMemoryIf.create((DwarfEntity entity) -> !PanicTaskDwarf.isHostileNearby(entity), MemoryModuleType.NEAREST_HOSTILE)),
                Pair.of(1, new MoveToTargetSink()),
                Pair.of(2, WorkStationCompetitionTaskDwarf.create()),
                Pair.of(3, new FollowCustomerTaskDwarf(speed)),
                Pair.of(5, GoToWantedItem.create(speed, false, 4)),
                Pair.of(6, AcquirePoi.create(profession.acquirableWorkstation(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty(), (serverLevel, blockPos) -> true)),
                Pair.of(7, new WalkTowardJobSiteTaskDwarf(speed)),
                Pair.of(8, TakeJobSiteTaskDwarf.create(speed)),
                Pair.of(10, AcquirePoi.create(poiType -> poiType.is(PoiTypes.HOME), MemoryModuleType.HOME, false, Optional.of((byte) 14))),
                Pair.of(10, AcquirePoi.create(poiType -> poiType.is(PoiTypes.MEETING), MemoryModuleType.MEETING_POINT, true, Optional.of((byte) 14))),
                Pair.of(10, GoToWorkTaskDwarf.create()),
                Pair.of(10, LoseJobOnSiteLossTaskDwarf.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createWorkTasks(DwarfProfessionRecord profession, float speed) {
        VillagerWorkTaskDwarf villagerWorkTask = profession == DwarfProfessionRecord.FARMER ? new FarmerWorkTaskDwarf() : new VillagerWorkTaskDwarf();
        return ImmutableList.of(createBusyFollowTask(), Pair.of(5, new RunOne<>(ImmutableList.of(
                Pair.of(villagerWorkTask, 7),
                Pair.of(StrollAroundPoi.create(MemoryModuleType.JOB_SITE, 0.4f, 4), 2),
                Pair.of(GoToSecondaryPositionTaskDwarf.create(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6, MemoryModuleType.JOB_SITE), 5),
                Pair.of(new FarmerVillagerTaskDwarf(), profession == DwarfProfessionRecord.FARMER ? 2 : 5),
                Pair.of(new BoneMealTaskDwarf(), profession == DwarfProfessionRecord.FARMER ? 4 : 7)))),
                Pair.of(10, new HoldTradeOffersTaskDwarf(400, 1600)),
                Pair.of(10, SetLookAndInteract.create(EntityTypes.PLAYER, 4)),
                Pair.of(2, VillagerWalkTowardsTaskDwarf.create(MemoryModuleType.JOB_SITE, speed, 9, 100, 1200)),
                Pair.of(3, new GiveGiftsToHeroTaskDwarf(100)),
                Pair.of(99, UpdateActivityFromSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createPlayTasks(float speed) {
        return ImmutableList.of(
                Pair.of(0, new MoveToTargetSink(80, 120)),
                createFreeFollowTask(),
                Pair.of(5, new RunOne<>(ImmutableList.of(
                        Pair.of(InteractWith.of(Entityinit.DWARF, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 2),
                        Pair.of(InteractWith.of(EntityTypes.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 1),
                        Pair.of(InsideBrownianWalk.create(speed), 1),
                        Pair.of(SetEntityLookTarget.create(EntityTypes.PLAYER, 8.0F), 1),
                        Pair.of(new DoNothing(20, 40), 2)))),
                Pair.of(99, UpdateActivityFromSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createRestTasks(float speed) {
        return ImmutableList.of(
                Pair.of(2, VillagerWalkTowardsTaskDwarf.create(MemoryModuleType.HOME, speed, 1, 150, 1200)),
                Pair.of(3, VillagerWalkTowardsTaskDwarf.create(MemoryModuleType.HOME, speed, 1, 150, 1200)),
                Pair.of(5, new RunOne<>(ImmutableList.of(
                        Pair.of(GoToPointOfInterestTaskDwarf.create(speed, 4), 2),
                        Pair.of(new DoNothing(20, 40), 2)))),
                createBusyFollowTask(),
                Pair.of(99, UpdateActivityFromSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createMeetTasks(float speed) {
        return ImmutableList.of(
                Pair.of(2, new RunOne<>(ImmutableList.of(
                        Pair.of(StrollAroundPoi.create(MemoryModuleType.MEETING_POINT, 0.4f, 40), 2),
                        Pair.of(MeetVillagerTaskDwarf.create(), 2)))),
                Pair.of(10, new HoldTradeOffersTaskDwarf(400, 1600)),
                Pair.of(10, SetLookAndInteract.create(EntityTypes.PLAYER, 4)),
                Pair.of(2, VillagerWalkTowardsTaskDwarf.create(MemoryModuleType.MEETING_POINT, speed, 6, 100, 200)),
                Pair.of(3, new GiveGiftsToHeroTaskDwarf(100)),
                Pair.of(3, GatherItemsVillagerTaskDwarf.create()),
                createFreeFollowTask(),
                Pair.of(99, UpdateActivityFromSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createIdleTasks(float speed) {
        return ImmutableList.of(
                Pair.of(2, new RunOne<>(ImmutableList.of(
                        Pair.of(InteractWith.of(Entityinit.DWARF, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 2),
                        Pair.of(InteractWith.of(EntityTypes.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 1),
                        Pair.of(InsideBrownianWalk.create(speed), 1),
                        Pair.of(SetEntityLookTarget.create(EntityTypes.PLAYER, 8.0F), 1),
                        Pair.of(new DoNothing(30, 60), 1)))),
                Pair.of(3, new GiveGiftsToHeroTaskDwarf(100)),
                Pair.of(3, SetLookAndInteract.create(EntityTypes.PLAYER, 4)),
                Pair.of(3, new HoldTradeOffersTaskDwarf(400, 1600)),
                Pair.of(3, GatherItemsVillagerTaskDwarf.create()),
                Pair.of(3, new VillagerBreedTaskDwarf()),
                createFreeFollowTask(),
                Pair.of(99, UpdateActivityFromSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createPanicTasks(float speed) {
        float f = speed * 1.5f;
        return ImmutableList.of(Pair.of(0, StopPanickingTaskDwarf.create()),
                Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_HOSTILE, f, 6, false)),
                Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, f, 6, false)),
                Pair.of(3, GoToPointOfInterestTaskDwarf.create(f, 2)),
                createBusyFollowTask());
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createPreRaidTasks(float speed) {
        return ImmutableList.of(
                Pair.of(0, SetEntityLookTarget.create(EntityTypes.PLAYER, 8.0F)),
                Pair.of(0, new RunOne<>(ImmutableList.of(
                        Pair.of(VillagerWalkTowardsTaskDwarf.create(MemoryModuleType.MEETING_POINT, speed * 1.5f, 2, 150, 200), 6),
                        Pair.of(GoToPointOfInterestTaskDwarf.create(speed * 1.5f, 2), 2)))),
                createBusyFollowTask(),
                Pair.of(99, UpdateActivityFromSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createRaidTasks(float speed) {
        return ImmutableList.of(
                Pair.of(0, new CelebrateRaidWinTaskDwarf(600, 600)),
                createBusyFollowTask(),
                Pair.of(99, UpdateActivityFromSchedule.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createHideTasks(float speed) {
        return ImmutableList.of(
                Pair.of(0, VillagerWalkTowardsTaskDwarf.create(MemoryModuleType.HIDING_PLACE, speed * 1.25f, 2, 150, 1000)),
                createBusyFollowTask(),
                Pair.of(99, UpdateActivityFromSchedule.create()));
    }

    private static Pair<Integer, BehaviorControl<LivingEntity>> createFreeFollowTask() {
        return Pair.of(5, new RunOne<>(ImmutableList.of(
                Pair.of(SetEntityLookTarget.create(EntityTypes.CAT, 8.0F), 8),
                Pair.of(SetEntityLookTarget.create(EntityTypes.VILLAGER, 8.0F), 2),
                Pair.of(SetEntityLookTarget.create(EntityTypes.PLAYER, 8.0F), 2),
                Pair.of(SetEntityLookTarget.create(MobCategory.CREATURE, 8.0F), 1),
                Pair.of(SetEntityLookTarget.create(MobCategory.WATER_CREATURE, 8.0F), 1),
                Pair.of(SetEntityLookTarget.create(MobCategory.AXOLOTLS, 8.0F), 1),
                Pair.of(SetEntityLookTarget.create(MobCategory.UNDERGROUND_WATER_CREATURE, 8.0F), 1),
                Pair.of(SetEntityLookTarget.create(MobCategory.WATER_AMBIENT, 8.0F), 1),
                Pair.of(SetEntityLookTarget.create(MobCategory.MONSTER, 8.0F), 1),
                Pair.of(new DoNothing(30, 60), 2))));
    }

    private static Pair<Integer, BehaviorControl<LivingEntity>> createBusyFollowTask() {
        return Pair.of(5, new RunOne<>(ImmutableList.of(
                Pair.of(SetEntityLookTarget.create(Entityinit.DWARF, 8.0f), 2),
                Pair.of(SetEntityLookTarget.create(EntityTypes.PLAYER, 8.0f), 2),
                Pair.of(new DoNothing(30, 60), 8))));
    }
}
