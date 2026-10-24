/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;
import warhammermod.Entities.Living.DwarfEntity;

public class PanicTaskDwarf
extends MultiTickTask<DwarfEntity> {
    public PanicTaskDwarf() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarf, long l) {
        return wasHurt(dwarf) || isHostileNearby(dwarf);
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarf, long l) {
        if (PanicTaskDwarf.wasHurt(dwarf) || PanicTaskDwarf.isHostileNearby(dwarf)) {
            Brain<DwarfEntity> brain = dwarf.getBrain();
            if (!brain.hasActivity(Activity.PANIC)) {
                brain.forget(MemoryModuleType.PATH);
                brain.forget(MemoryModuleType.WALK_TARGET);
                brain.forget(MemoryModuleType.LOOK_TARGET);
                brain.forget(MemoryModuleType.BREED_TARGET);
                brain.forget(MemoryModuleType.INTERACTION_TARGET);
            }
            brain.doExclusively(Activity.PANIC);
        }
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, DwarfEntity dwarf, long l) {
        if (l % 100L == 0L) {
            dwarf.summonGolem(serverWorld, l, 3);
        }
    }

    public static boolean isHostileNearby(LivingEntity entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_HOSTILE);
    }

    public static boolean wasHurt(LivingEntity entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
    }
}

