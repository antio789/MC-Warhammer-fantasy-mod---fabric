/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.TargetUtil;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.Optional;

public class WalkTowardJobSiteTaskDwarf
extends MultiTickTask<DwarfEntity> {
    private static final int RUN_TIME = 1200;
    final float speed;

    public WalkTowardJobSiteTaskDwarf(float speed) {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleState.VALUE_PRESENT), 1200);
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        return dwarfEntity.getBrain().getFirstPossibleNonCoreActivity().map(activity -> activity == Activity.IDLE || activity == Activity.WORK || activity == Activity.PLAY).orElse(true);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        return dwarfEntity.getBrain().hasMemoryModule(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        TargetUtil.walkTowards((LivingEntity)dwarfEntity, dwarfEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().pos(), this.speed, 1);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        Optional<GlobalPos> optional = dwarfEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        optional.ifPresent(pos -> {
            BlockPos blockPos = pos.pos();
            ServerWorld serverWorld2 = serverWorld.getServer().getWorld(pos.dimension());
            if (serverWorld2 == null) {
                return;
            }
            PointOfInterestStorage pointOfInterestStorage = serverWorld2.getPointOfInterestStorage();
            if (pointOfInterestStorage.test(blockPos, poiType -> true)) {
                pointOfInterestStorage.releaseTicket(blockPos);
            }
            DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
        });
        dwarfEntity.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}

