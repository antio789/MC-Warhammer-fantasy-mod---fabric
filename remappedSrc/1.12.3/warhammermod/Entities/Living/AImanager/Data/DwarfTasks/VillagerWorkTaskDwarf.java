/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.Optional;

public class VillagerWorkTaskDwarf
extends MultiTickTask<DwarfEntity> {
    private static final int RUN_TIME = 300;
    private static final double MAX_DISTANCE = 1.73;
    private long lastCheckedTime;

    public VillagerWorkTaskDwarf() {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        if (serverWorld.getTime() - this.lastCheckedTime < 300L) {
            return false;
        }
        if (serverWorld.random.nextInt(2) != 0) {
            return false;
        }
        this.lastCheckedTime = serverWorld.getTime();
        GlobalPos globalPos = dwarfEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE).get();
        return globalPos.dimension() == serverWorld.getRegistryKey() && globalPos.pos().isWithinDistance(dwarfEntity.getPos(), 1.73);
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        brain.remember(MemoryModuleType.LAST_WORKED_AT_POI, l);
        brain.getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE).ifPresent(pos -> brain.remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(pos.pos())));
        dwarfEntity.playWorkSound();
        this.performAdditionalWork(serverWorld, dwarfEntity);
        if (dwarfEntity.shouldRestock()) {
            dwarfEntity.restock();
        }
    }

    protected void performAdditionalWork(ServerWorld world, DwarfEntity entity) {
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        Optional<GlobalPos> optional = dwarfEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
        if (optional.isEmpty()) {
            return false;
        }
        GlobalPos globalPos = optional.get();
        return globalPos.dimension() == serverWorld.getRegistryKey() && globalPos.pos().isWithinDistance(dwarfEntity.getPos(), 1.73);
    }
}

