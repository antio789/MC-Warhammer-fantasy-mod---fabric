package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.Optional;

public class TakeJobSiteTaskDwarf {
    public static BehaviorControl<DwarfEntity> create(float speed) {
        return BehaviorBuilder.create(context -> context.group(
                        context.present(MemoryModuleType.POTENTIAL_JOB_SITE),
                        context.absent(MemoryModuleType.JOB_SITE),
                        context.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES),
                        context.registered(MemoryModuleType.WALK_TARGET),
                        context.registered(MemoryModuleType.LOOK_TARGET))
                .apply(context, (potentialJobSite, jobSite, mobs, walkTarget, lookTarget) -> (world, entity, time) -> {
                    if (entity.isBaby()) {
                        return false;
                    }
                    if (entity.getProfession() != DwarfProfessionRecord.Warrior) {
                        return false;
                    }
                    BlockPos blockPos = context.get(potentialJobSite).pos();
                    Optional<Holder<PoiType>> optional = world.getPoiManager().getType(blockPos);
                    if (optional.isEmpty()) {
                        return true;
                    }
                    context.get(mobs).nearbyEntities().stream().filter(mob -> mob instanceof DwarfEntity dwarf && mob != entity && mob.isAlive()
                            && TakeJobSiteTaskDwarf.canUseJobSite(optional.get(), dwarf, blockPos)).findFirst().ifPresent(dwarf -> {
                        walkTarget.erase();
                        lookTarget.erase();
                        potentialJobSite.erase();
                        if (dwarf instanceof DwarfEntity dwarf1 && dwarf1.getBrain().getMemory(MemoryModuleType.JOB_SITE).isEmpty()) {
                            BehaviorUtils.setWalkAndLookTargetMemories((LivingEntity) dwarf1, blockPos, speed, 1);
                            dwarf1.getBrain().setMemory(MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.of(world.dimension(), blockPos));
                        }
                    });
                    return true;
                }));
    }

    private static boolean canUseJobSite(Holder<PoiType> poiType, DwarfEntity villager, BlockPos pos) {
        boolean bl = villager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
        if (bl) {
            return false;
        }
        Optional<GlobalPos> optional = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        DwarfProfessionRecord villagerProfession = villager.getProfession();
        if (villagerProfession.heldWorkstation().test(poiType)) {
            return optional.map(globalPos -> globalPos.pos().equals(pos)).orElseGet(() -> TakeJobSiteTaskDwarf.canReachJobSite(villager, pos, poiType.value()));
        }
        return false;
    }

    private static boolean canReachJobSite(PathfinderMob entity, BlockPos pos, PoiType poiType) {
        Path path = entity.getNavigation().createPath(pos, poiType.validRange());
        return path != null && path.canReach();
    }
}
