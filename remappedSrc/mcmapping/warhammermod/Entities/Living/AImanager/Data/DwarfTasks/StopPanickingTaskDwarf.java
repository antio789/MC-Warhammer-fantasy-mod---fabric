/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import warhammermod.utils.Registry.Entityinit;

import java.util.Optional;

//add something to run away if cannot access it.
public class StopPanickingTaskDwarf {
    private static final int MAX_DISTANCE = 36;

    public static BehaviorControl<LivingEntity> create() {
        return BehaviorBuilder.create(context -> context.group(context.registered(MemoryModuleType.HURT_BY), context.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE) ,context.registered(MemoryModuleType.HURT_BY_ENTITY), context.registered(MemoryModuleType.NEAREST_HOSTILE)).apply(context, (hurtBy,timecannotreach, hurtByEntity, nearestHostile) -> (world, entity, time) -> {
            boolean bl;
            boolean bl2 = bl = context.tryGet(hurtBy).isPresent() || context.tryGet(nearestHostile).isPresent() || context.tryGet(hurtByEntity).filter(hurtByx -> hurtByx.distanceToSqr(entity) <= 36.0).isPresent();
            if(entity.getBrain().getMemoryInternal(MemoryModuleType.ATTACK_TARGET).isPresent()&&(!entity.getBrain().getMemoryInternal(MemoryModuleType.ATTACK_TARGET).get().isAlive())){
                entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }
            if (!bl) {
                hurtBy.erase();
                hurtByEntity.erase();
                entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                entity.getBrain().updateActivityFromSchedule(world.getDayTime(), world.getGameTime());
            }//else if(cannotReachTarget(entity,context.getOptionalValue(timecannotreach))){entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);//doesnt work properly needs to keep target in mind}
            else if(!entity.getBrain().getMemoryInternal(MemoryModuleType.ATTACK_TARGET).isPresent() && context.tryGet(hurtByEntity).isPresent()){
                entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET,context.tryGet(hurtByEntity).get());
            } else if(!entity.getBrain().getMemoryInternal(MemoryModuleType.ATTACK_TARGET).isPresent() && context.tryGet(nearestHostile).isPresent()) {
                entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET,context.tryGet(nearestHostile).get());
            }

                return true;
        }));
    }


    private static boolean cannotReachTarget(LivingEntity livingEntity, Optional<Long> optional) {
        return optional.isPresent() && livingEntity.level().getGameTime() - optional.get() > 200L;
    }
}

