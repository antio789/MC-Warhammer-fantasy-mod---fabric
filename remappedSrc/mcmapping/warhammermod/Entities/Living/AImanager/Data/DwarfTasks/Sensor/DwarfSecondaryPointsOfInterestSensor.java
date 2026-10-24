/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.ArrayList;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.level.Level;

public class DwarfSecondaryPointsOfInterestSensor
extends Sensor<DwarfEntity> {
    private static final int RUN_TIME = 40;

    public DwarfSecondaryPointsOfInterestSensor() {
        super(40);
    }

    @Override
    protected void sense(ServerLevel serverWorld, DwarfEntity dwarfEntity) {
        ResourceKey<Level> registryKey = serverWorld.dimension();
        BlockPos blockPos = dwarfEntity.blockPosition();
        ArrayList<GlobalPos> list = Lists.newArrayList();
        int i = 4;
        for (int j = -4; j <= 4; ++j) {
            for (int k = -2; k <= 2; ++k) {
                for (int l = -4; l <= 4; ++l) {
                    BlockPos blockPos2 = blockPos.offset(j, k, l);
                    if (!dwarfEntity.getProfession().secondaryJobSites().contains(serverWorld.getBlockState(blockPos2).getBlock())) continue;
                    list.add(GlobalPos.of(registryKey, blockPos2));
                }
            }
        }
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        if (!list.isEmpty()) {
            brain.setMemory(MemoryModuleType.SECONDARY_JOB_SITE, list);
        } else {
            brain.eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
    }
}

