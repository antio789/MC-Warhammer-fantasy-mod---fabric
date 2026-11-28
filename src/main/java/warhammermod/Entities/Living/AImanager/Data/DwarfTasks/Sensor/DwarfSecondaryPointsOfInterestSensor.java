package warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.level.Level;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.ArrayList;
import java.util.Set;

public class DwarfSecondaryPointsOfInterestSensor extends Sensor<DwarfEntity> {
    private static final int RUN_TIME = 40;

    public DwarfSecondaryPointsOfInterestSensor() {
        super(40);
    }

    @Override
    protected void doTick(ServerLevel serverWorld, DwarfEntity dwarfEntity) {
        ResourceKey<Level> registryKey = serverWorld.dimension();
        BlockPos blockPos = dwarfEntity.blockPosition();
        ArrayList<GlobalPos> list = Lists.newArrayList();
        for (int j = -4; j <= 4; ++j) {
            for (int k = -2; k <= 2; ++k) {
                for (int l = -4; l <= 4; ++l) {
                    BlockPos blockPos2 = blockPos.offset(j, k, l);
                    if (dwarfEntity.getProfession().secondaryJobSites().contains(serverWorld.getBlockState(blockPos2).getBlock()))
                        continue;
                    list.add(GlobalPos.of(registryKey, blockPos2));
                }
            }
        }
        if (!list.isEmpty()) {
            dwarfEntity.getBrain().setMemory(MemoryModuleType.SECONDARY_JOB_SITE, list);
        } else {
            dwarfEntity.getBrain().eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of();
    }
}
