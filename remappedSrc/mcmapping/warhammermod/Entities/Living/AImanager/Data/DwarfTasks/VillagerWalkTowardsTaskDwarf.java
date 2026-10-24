/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import warhammermod.Entities.Living.DwarfEntity;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class VillagerWalkTowardsTaskDwarf {
    public static OneShot<DwarfEntity> create(MemoryModuleType<GlobalPos> destination, float speed, int completionRange, int maxDistance, int maxRunTime) {
        return BehaviorBuilder.create(context -> context.group(context.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE), context.absent(MemoryModuleType.WALK_TARGET), context.present(destination)).apply(context, (cantReachWalkTargetSince, walkTarget, destinationResult) -> (world, entity, time) -> {
            GlobalPos globalPos = (GlobalPos)context.get(destinationResult);
            Optional optional = context.tryGet(cantReachWalkTargetSince);
            if (globalPos.dimension() != world.dimension() || optional.isPresent() && world.getGameTime() - (Long)optional.get() > (long)maxRunTime) {
                entity.releaseTicketFor(destination);
                destinationResult.erase();
                cantReachWalkTargetSince.set(time);
            } else if (globalPos.pos().distManhattan(entity.blockPosition()) > maxDistance) {
                Vec3 vec3d = null;
                int l = 0;
                int m = 1000;
                while (vec3d == null || BlockPos.containing(vec3d).distManhattan(entity.blockPosition()) > maxDistance) {
                    vec3d = DefaultRandomPos.getPosTowards(entity, 15, 7, Vec3.atBottomCenterOf(globalPos.pos()), 1.5707963705062866);
                    if (++l != 1000) continue;
                    entity.releaseTicketFor(destination);
                    destinationResult.erase();
                    cantReachWalkTargetSince.set(time);
                    return true;
                }
                walkTarget.set(new WalkTarget(vec3d, speed, completionRange));
            } else if (globalPos.pos().distManhattan(entity.blockPosition()) > completionRange) {
                walkTarget.set(new WalkTarget(globalPos.pos(), speed, completionRange));
            }
            return true;
        }));
    }
}

