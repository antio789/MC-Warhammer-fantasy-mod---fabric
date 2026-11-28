package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import warhammermod.Entities.Living.DwarfEntity;

public class FollowCustomerTaskDwarf extends Behavior<DwarfEntity> {
    private final float speed;

    public FollowCustomerTaskDwarf(float speed) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), Integer.MAX_VALUE);
        this.speed = speed;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverWorld, DwarfEntity dwarfentity) {
        Player playerEntity = dwarfentity.getTradingPlayer();
        return dwarfentity.isAlive() && playerEntity != null && !dwarfentity.isInWater() && !dwarfentity.hurtMarked && dwarfentity.distanceToSqr(playerEntity) <= 16.0 && playerEntity.containerMenu != null;
    }

    @Override
    protected boolean canStillUse(ServerLevel serverWorld, DwarfEntity dwarfentity, long l) {
        return this.checkExtraStartConditions(serverWorld, dwarfentity);
    }

    @Override
    protected void start(ServerLevel serverWorld, DwarfEntity dwarfentity, long l) {
        this.update(dwarfentity);
    }

    @Override
    protected void stop(ServerLevel serverWorld, DwarfEntity dwarfentity, long l) {
        var brain = dwarfentity.getBrain();
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void tick(ServerLevel serverWorld, DwarfEntity dwarfentity, long l) {
        this.update(dwarfentity);
    }

    @Override
    protected boolean timedOut(long time) {
        return false;
    }

    private void update(DwarfEntity dwarf) {
        var brain = dwarf.getBrain();
        brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(dwarf.getTradingPlayer(), this.speed, 2));
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(dwarf.getTradingPlayer(), true));
    }
}
