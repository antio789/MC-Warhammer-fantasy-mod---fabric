/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestType;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.Optional;

public class WorkStationCompetitionTaskDwarf {
    public static Task<DwarfEntity> create() {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(MemoryModuleType.JOB_SITE), context.queryMemoryValue(MemoryModuleType.MOBS)).apply(context, (jobSite, mobs) -> (world, entity, time) -> {
            GlobalPos globalPos = context.getValue(jobSite);

            world.getPointOfInterestStorage().getType(globalPos.pos()).ifPresent(
                    poiType -> context.getValue(mobs).stream().filter(
                        mob -> mob instanceof DwarfEntity entity1 && mob !=entity && mob.isAlive() &&
                                WorkStationCompetitionTaskDwarf.isUsingWorkStationAt(globalPos,poiType,entity1)
                ).reduce(entity, WorkStationCompetitionTaskDwarf::keepJobSiteForMoreExperiencedVillager));
            return true;
        }));

    }





    //.reduce(entity,DwarfWorkStationCompetitionTask.keepJobSiteForMoreExperiencedVillager(entity,))


    private static DwarfEntity keepJobSiteForMoreExperiencedVillager(LivingEntity first, LivingEntity livingEntity1) {
        DwarfEntity villagerEntity2;
        DwarfEntity villagerEntity = (DwarfEntity) first;
        if (livingEntity1 instanceof DwarfEntity second){
            if (villagerEntity.getExperience() > second.getExperience()) {
                villagerEntity2 = second;
            } else {
                villagerEntity = second;
                villagerEntity2 = (DwarfEntity) first;
            }
            villagerEntity2.getBrain().forget(MemoryModuleType.JOB_SITE);
            return villagerEntity;
        }
        return (DwarfEntity) first;
    }

    private static boolean isUsingWorkStationAt(GlobalPos pos, RegistryEntry<PointOfInterestType> poiType, DwarfEntity villager) {
        Optional<GlobalPos> optional = villager.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
        return optional.isPresent() && pos.equals(optional.get()) && WorkStationCompetitionTaskDwarf.isCompletedWorkStation(poiType, villager.getProfession());
    }

    private static boolean isCompletedWorkStation(RegistryEntry<PointOfInterestType> poiType, DwarfProfessionRecord profession) {
        return profession.heldWorkstation().test(poiType);
    }
}

