package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import warhammermod.utils.Registry.Entityinit;

public class MeetVillagerTaskDwarf {
    private static final float WALK_SPEED = 0.3f;

    public static OneShot<LivingEntity> create() {
        return BehaviorBuilder.create(context -> context.group(
                        context.registered(MemoryModuleType.WALK_TARGET),
                        context.registered(MemoryModuleType.LOOK_TARGET),
                        context.present(MemoryModuleType.MEETING_POINT),
                        context.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES),
                        context.absent(MemoryModuleType.INTERACTION_TARGET))
                .apply(context, (walkTarget, lookTarget, meetingPoint, visibleMobs, interactionTarget) -> (world, entity, time) -> {
                    GlobalPos globalPos = context.get(meetingPoint);
                    NearestVisibleLivingEntities livingTargetCache = context.get(visibleMobs);
                    if (world.random.nextInt(100) == 0 && world.dimension() == globalPos.dimension() && globalPos.pos().closerThan(entity.position(), 4.0) && livingTargetCache.anyMatch(target -> Entityinit.DWARF.equals(target.getType()))) {
                        livingTargetCache.findClosest(target -> Entityinit.DWARF.equals(target.getType()) && target.distanceToSqr(entity) <= 32.0).ifPresent(target -> {
                            interactionTarget.set(target);
                            lookTarget.set(new net.minecraft.world.entity.ai.behavior.EntityTracker((Entity) target, true));
                            walkTarget.set(new WalkTarget((Entity) target, 0.3f, 1));
                        });
                        return true;
                    }
                    return false;
                }));
    }
}
