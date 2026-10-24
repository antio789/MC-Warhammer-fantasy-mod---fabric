/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.ArrayList;
import java.util.Set;

public class DwarfSecondaryPointsOfInterestSensor
extends Sensor<DwarfEntity> {
    private static final int RUN_TIME = 40;

    public DwarfSecondaryPointsOfInterestSensor() {
        super(40);
    }

    @Override
    protected void sense(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        RegistryKey<World> registryKey = serverWorld.getRegistryKey();
        BlockPos blockPos = dwarfEntity.getBlockPos();
        ArrayList<GlobalPos> list = Lists.newArrayList();
        int i = 4;
        for (int j = -4; j <= 4; ++j) {
            for (int k = -2; k <= 2; ++k) {
                for (int l = -4; l <= 4; ++l) {
                    BlockPos blockPos2 = blockPos.add(j, k, l);
                    if (!dwarfEntity.getProfession().secondaryJobSites().contains(serverWorld.getBlockState(blockPos2).getBlock())) continue;
                    list.add(GlobalPos.create(registryKey, blockPos2));
                }
            }
        }
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        if (!list.isEmpty()) {
            brain.remember(MemoryModuleType.SECONDARY_JOB_SITE, list);
        } else {
            brain.forget(MemoryModuleType.SECONDARY_JOB_SITE);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
    }
}

