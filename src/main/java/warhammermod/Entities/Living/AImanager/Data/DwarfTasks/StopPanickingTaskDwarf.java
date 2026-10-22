package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

//add something to run away if cannot access it.
public class StopPanickingTaskDwarf {
    private static final int MAX_DISTANCE = 36;

    public static BehaviorControl<LivingEntity> create() {
        return BehaviorBuilder.create(context -> context.group(
                        context.registered(MemoryModuleType.HURT_BY),
                        context.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE),
                        context.registered(MemoryModuleType.HURT_BY_ENTITY),
                        context.registered(MemoryModuleType.NEAREST_HOSTILE))
                .apply(context, (hurtBy, timecannotreach, hurtByEntity, nearestHostile) -> (world, entity, time) -> {
                    boolean bl = context.tryGet(hurtBy).isPresent() || context.tryGet(nearestHostile).isPresent() || context.tryGet(hurtByEntity).filter(hurtByx -> hurtByx.distanceToSqr(entity) <= 36.0).isPresent();
                    var attackTarget = entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
                    if (attackTarget.isPresent() && !attackTarget.get().isAlive()) {
                        entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                    }
                    if (!bl) {
                        hurtBy.erase();
                        hurtByEntity.erase();
                        entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                    } else if (entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && context.tryGet(hurtByEntity).isPresent()) {
                        entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, context.tryGet(hurtByEntity).get());
                    } else if (entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && context.tryGet(nearestHostile).isPresent()) {
                        entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, context.tryGet(nearestHostile).get());
                    }

                    return true;
                }));
    }
}
