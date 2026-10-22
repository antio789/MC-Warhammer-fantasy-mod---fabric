package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.Optional;

public class WalkTowardJobSiteTaskDwarf extends Behavior<DwarfEntity> {
    private static final int RUN_TIME = 1200;
    final float speed;

    public WalkTowardJobSiteTaskDwarf(float speed) {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT), 1200);
        this.speed = speed;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverWorld, DwarfEntity dwarfEntity) {
        return dwarfEntity.getBrain().getActiveNonCoreActivity().map(activity -> activity == Activity.IDLE || activity == Activity.WORK || activity == Activity.PLAY).orElse(true);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        return dwarfEntity.getBrain().hasMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    @Override
    protected void tick(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).ifPresent(pos ->
                net.minecraft.world.entity.ai.behavior.BehaviorUtils.setWalkAndLookTargetMemories((LivingEntity) dwarfEntity, pos.pos(), this.speed, 1));
    }

    @Override
    protected void stop(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        Optional<GlobalPos> optional = dwarfEntity.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        optional.ifPresent(pos -> {
            BlockPos blockPos = pos.pos();
            ServerLevel serverWorld2 = serverWorld.getServer().getLevel(pos.dimension());
            if (serverWorld2 == null) {
                return;
            }
            var pointOfInterestStorage = serverWorld2.getPoiManager();
            if (pointOfInterestStorage.exists(blockPos, poiType -> true)) {
                pointOfInterestStorage.release(blockPos);
            }
        });
        dwarfEntity.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}
