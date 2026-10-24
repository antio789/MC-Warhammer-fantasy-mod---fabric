/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.TargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestType;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.Optional;

public class TakeJobSiteTaskDwarf {
    public static Task<DwarfEntity> create(float speed) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE), context.queryMemoryAbsent(MemoryModuleType.JOB_SITE), context.queryMemoryValue(MemoryModuleType.MOBS), context.queryMemoryOptional(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET)).apply(context, (potentialJobSite, jobSite, mobs, walkTarget, lookTarget) -> (world, entity, time) -> {
            if (entity.isBaby()) {
                return false;
            }
            if (entity.getProfession() != DwarfProfessionRecord.Warrior) {
                return false;
            }
            BlockPos blockPos = ((GlobalPos)context.getValue(potentialJobSite)).pos();
            Optional<RegistryEntry<PointOfInterestType>> optional = world.getPointOfInterestStorage().getType(blockPos);
            if (optional.isEmpty()) {
                return true;
            }
            (context.getValue(mobs)).stream().filter(mob -> mob instanceof DwarfEntity dwarf && mob != entity && mob.isAlive()
                    && TakeJobSiteTaskDwarf.canUseJobSite(optional.get(),dwarf,blockPos)).findFirst().ifPresent(dwarf ->
                    {
                        walkTarget.forget();
                        lookTarget.forget();
                        potentialJobSite.forget();
                        if (dwarf instanceof DwarfEntity dwarf1 && dwarf1.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE).isEmpty()){
                            TargetUtil.walkTowards((LivingEntity)dwarf1, blockPos, speed, 1);
                            dwarf1.getBrain().remember(MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.create(world.getRegistryKey(), blockPos));
                            DebugInfoSender.sendPointOfInterest(world, blockPos);
                        }
                    }
                    );
            return true;
        }));
    }

    private static boolean canUseJobSite(RegistryEntry<PointOfInterestType> poiType, DwarfEntity villager, BlockPos pos) {
        boolean bl = villager.getBrain().getOptionalRegisteredMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
        if (bl) {
            return false;
        }
        Optional<GlobalPos> optional = villager.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
        DwarfProfessionRecord villagerProfession = villager.getProfession();
        if (villagerProfession.heldWorkstation().test(poiType)) {
            return optional.map(globalPos -> globalPos.pos().equals(pos)).orElseGet(() -> TakeJobSiteTaskDwarf.canReachJobSite(villager, pos, poiType.value()));
        }
        return false;
    }

    private static boolean canReachJobSite(PathAwareEntity entity, BlockPos pos, PointOfInterestType poiType) {
        Path path = entity.getNavigation().findPathTo(pos, poiType.searchDistance());
        return path != null && path.reachesTarget();
    }
}

