/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import warhammermod.Entities.Living.DwarfEntity;

public class FollowCustomerTaskDwarf extends MultiTickTask<DwarfEntity> {
    private final float speed;

    public FollowCustomerTaskDwarf(float speed) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED), Integer.MAX_VALUE);
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfentity) {
        PlayerEntity playerEntity = dwarfentity.getCustomer();
        return dwarfentity.isAlive() && playerEntity != null && !dwarfentity.isTouchingWater() && !dwarfentity.velocityModified && dwarfentity.squaredDistanceTo(playerEntity) <= 16.0 && playerEntity.currentScreenHandler != null;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfentity, long l) {
        return this.shouldRun(serverWorld, dwarfentity);
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfentity, long l) {
        this.update(dwarfentity);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, DwarfEntity dwarfentity, long l) {
        Brain<DwarfEntity> brain = dwarfentity.getBrain();
        brain.forget(MemoryModuleType.WALK_TARGET);
        brain.forget(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, DwarfEntity dwarfentity, long l) {
        this.update(dwarfentity);
    }

    @Override
    protected boolean isTimeLimitExceeded(long time) {
        return false;
    }

    private void update(DwarfEntity dwarf) {
        Brain<DwarfEntity> brain = dwarf.getBrain();
        brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget(dwarf.getCustomer(), false), this.speed, 2));
        brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(dwarf.getCustomer(), true));
    }
}

