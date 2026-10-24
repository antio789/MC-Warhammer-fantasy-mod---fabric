/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class VillagerWorkTaskDwarf
extends Behavior<DwarfEntity> {
    private static final int RUN_TIME = 300;
    private static final double MAX_DISTANCE = 1.73;
    private long lastCheckedTime;

    public VillagerWorkTaskDwarf() {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
    }

    @Override
    protected boolean shouldRun(ServerLevel serverWorld, DwarfEntity dwarfEntity) {
        if (serverWorld.getGameTime() - this.lastCheckedTime < 300L) {
            return false;
        }
        if (serverWorld.random.nextInt(2) != 0) {
            return false;
        }
        this.lastCheckedTime = serverWorld.getGameTime();
        GlobalPos globalPos = dwarfEntity.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
        return globalPos.dimension() == serverWorld.dimension() && globalPos.pos().closerToCenterThan(dwarfEntity.position(), 1.73);
    }

    @Override
    protected void run(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        brain.setMemory(MemoryModuleType.LAST_WORKED_AT_POI, l);
        brain.getMemory(MemoryModuleType.JOB_SITE).ifPresent(pos -> brain.setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos.pos())));
        dwarfEntity.playWorkSound();
        this.performAdditionalWork(serverWorld, dwarfEntity);
        if (dwarfEntity.shouldRestock()) {
            dwarfEntity.restock();
        }
    }

    protected void performAdditionalWork(ServerLevel world, DwarfEntity entity) {
    }

    @Override
    protected boolean shouldKeepRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        Optional<GlobalPos> optional = dwarfEntity.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (optional.isEmpty()) {
            return false;
        }
        GlobalPos globalPos = optional.get();
        return globalPos.dimension() == serverWorld.dimension() && globalPos.pos().closerToCenterThan(dwarfEntity.position(), 1.73);
    }
}

