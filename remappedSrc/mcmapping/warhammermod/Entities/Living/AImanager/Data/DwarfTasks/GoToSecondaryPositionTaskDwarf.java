/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import org.apache.commons.lang3.mutable.MutableLong;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.List;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class GoToSecondaryPositionTaskDwarf {
    public static BehaviorControl<DwarfEntity> create(MemoryModuleType<List<GlobalPos>> secondaryPositions, float speed, int completionRange, int primaryPositionActivationDistance, MemoryModuleType<GlobalPos> primaryPosition) {
        MutableLong mutableLong = new MutableLong(0L);
        return BehaviorBuilder.create(context -> context.group(context.registered(MemoryModuleType.WALK_TARGET), context.present(secondaryPositions), context.present(primaryPosition)).apply(context, (walkTarget, secondary, primary) -> (world, entity, time) -> {
            List list = (List)context.get(secondary);
            GlobalPos globalPos = (GlobalPos)context.get(primary);
            if (list.isEmpty()) {
                return false;
            }
            GlobalPos globalPos2 = (GlobalPos)list.get(world.getRandom().nextInt(list.size()));
            if (globalPos2 == null || world.dimension() != globalPos2.dimension() || !globalPos.pos().closerToCenterThan(entity.position(), (double)primaryPositionActivationDistance)) {
                return false;
            }
            if (time > mutableLong.getValue()) {
                walkTarget.set(new WalkTarget(globalPos2.pos(), speed, completionRange));
                mutableLong.setValue(time + 100L);
            }
            return true;
        }));
    }
}

