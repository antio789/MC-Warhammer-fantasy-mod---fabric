/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import warhammermod.utils.Registry.Entityinit;

public class MeetVillagerTaskDwarf {
    private static final float WALK_SPEED = 0.3f;

    public static OneShot<LivingEntity> create() {
        return BehaviorBuilder.create(context -> context.group(context.registered(MemoryModuleType.WALK_TARGET), context.registered(MemoryModuleType.LOOK_TARGET), context.present(MemoryModuleType.MEETING_POINT), context.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES), context.absent(MemoryModuleType.INTERACTION_TARGET)).apply(context, (walkTarget, lookTarget, meetingPoint, visibleMobs, interactionTarget) -> (world, entity, time) -> {
            GlobalPos globalPos = (GlobalPos)context.get(meetingPoint);
            NearestVisibleLivingEntities livingTargetCache = (NearestVisibleLivingEntities)context.get(visibleMobs);
            if (world.getRandom().nextInt(100) == 0 && world.dimension() == globalPos.dimension() && globalPos.pos().closerToCenterThan(entity.position(), 4.0) && livingTargetCache.contains(target -> Entityinit.DWARF.equals(target.getType()))) {
                livingTargetCache.findClosest(target -> Entityinit.DWARF.equals(target.getType()) && target.distanceToSqr(entity) <= 32.0).ifPresent(target -> {
                    interactionTarget.set(target);
                    lookTarget.set(new EntityTracker((Entity)target, true));
                    walkTarget.set(new WalkTarget(new EntityTracker((Entity)target, false), 0.3f, 1));
                });
                return true;
            }
            return false;
        }));
    }
}

