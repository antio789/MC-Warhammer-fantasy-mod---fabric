/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import warhammermod.utils.Registry.Entityinit;

import java.util.Optional;

//add something to run away if cannot access it.
public class StopPanickingTaskDwarf {
    private static final int MAX_DISTANCE = 36;

    public static Task<LivingEntity> create() {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryOptional(MemoryModuleType.HURT_BY), context.queryMemoryOptional(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE) ,context.queryMemoryOptional(MemoryModuleType.HURT_BY_ENTITY), context.queryMemoryOptional(MemoryModuleType.NEAREST_HOSTILE)).apply(context, (hurtBy,timecannotreach, hurtByEntity, nearestHostile) -> (world, entity, time) -> {
            boolean bl;
            boolean bl2 = bl = context.getOptionalValue(hurtBy).isPresent() || context.getOptionalValue(nearestHostile).isPresent() || context.getOptionalValue(hurtByEntity).filter(hurtByx -> hurtByx.squaredDistanceTo(entity) <= 36.0).isPresent();
            if(entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isPresent()&&(!entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get().isAlive())){
                entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
            }
            if (!bl) {
                hurtBy.forget();
                hurtByEntity.forget();
                entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
                entity.getBrain().refreshActivities(world.getTimeOfDay(), world.getTime());
            }//else if(cannotReachTarget(entity,context.getOptionalValue(timecannotreach))){entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);//doesnt work properly needs to keep target in mind}
            else if(!entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isPresent() && context.getOptionalValue(hurtByEntity).isPresent()){
                entity.getBrain().remember(MemoryModuleType.ATTACK_TARGET,context.getOptionalValue(hurtByEntity).get());
            } else if(!entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isPresent() && context.getOptionalValue(nearestHostile).isPresent()) {
                entity.getBrain().remember(MemoryModuleType.ATTACK_TARGET,context.getOptionalValue(nearestHostile).get());
            }

                return true;
        }));
    }


    private static boolean cannotReachTarget(LivingEntity livingEntity, Optional<Long> optional) {
        return optional.isPresent() && livingEntity.getWorld().getTime() - optional.get() > 200L;
    }
}

