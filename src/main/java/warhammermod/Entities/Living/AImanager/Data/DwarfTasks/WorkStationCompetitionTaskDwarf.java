package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import warhammermod.Entities.Living.DwarfEntity;

public class WorkStationCompetitionTaskDwarf {
    public static BehaviorControl<DwarfEntity> create() {
        return BehaviorBuilder.create(context -> context.group(
                        context.present(MemoryModuleType.JOB_SITE),
                        context.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES))
                .apply(context, (jobSite, mobs) -> (world, entity, time) -> {
                    GlobalPos globalPos = context.get(jobSite);

                    world.getPoiManager().getType(globalPos.pos()).ifPresent(
                            poiType -> context.get(mobs).nearbyEntities().stream().filter(
                                    mob -> mob instanceof DwarfEntity entity1 && mob != entity && mob.isAlive() &&
                                            WorkStationCompetitionTaskDwarf.isUsingWorkStationAt(globalPos, poiType, entity1)
                            ).reduce(entity, WorkStationCompetitionTaskDwarf::keepJobSiteForMoreExperiencedVillager));
                    return true;
                }));
    }

    private static DwarfEntity keepJobSiteForMoreExperiencedVillager(LivingEntity first, LivingEntity livingEntity1) {
        DwarfEntity villagerEntity2;
        DwarfEntity villagerEntity = (DwarfEntity) first;
        if (livingEntity1 instanceof DwarfEntity second) {
            if (villagerEntity.getVillagerXp() > second.getVillagerXp()) {
                villagerEntity2 = second;
            } else {
                villagerEntity = second;
                villagerEntity2 = (DwarfEntity) first;
            }
            villagerEntity2.getBrain().eraseMemory(MemoryModuleType.JOB_SITE);
            return villagerEntity;
        }
        return (DwarfEntity) first;
    }

    private static boolean isUsingWorkStationAt(GlobalPos pos, Holder<PoiType> poiType, DwarfEntity villager) {
        var optional = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        return optional.isPresent() && pos.equals(optional.get()) && isPoiTypeHolder(poiType, villager.getProfession());
    }

    private static boolean isPoiTypeHolder(Holder<PoiType> poiType, warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord profession) {
        return profession.heldWorkstation().test(poiType);
    }
}
