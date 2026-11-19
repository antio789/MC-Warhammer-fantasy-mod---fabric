package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import warhammermod.Entities.Living.DwarfEntity;

public class PanicTaskDwarf extends Behavior<DwarfEntity> {
    public PanicTaskDwarf() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean canStillUse(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        return wasHurt(dwarf) || isHostileNearby(dwarf);
    }

    @Override
    protected void start(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        if (PanicTaskDwarf.wasHurt(dwarf) || PanicTaskDwarf.isHostileNearby(dwarf)) {
            var brain = dwarf.getBrain();
            if (!brain.hasMemoryValue(MemoryModuleType.PATH)) {
                brain.eraseMemory(MemoryModuleType.PATH);
                brain.eraseMemory(MemoryModuleType.WALK_TARGET);
                brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
                brain.eraseMemory(MemoryModuleType.BREED_TARGET);
                brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            }
            brain.setActiveActivityIfPossible(Activity.PANIC);
        }
    }

    @Override
    protected void tick(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        if (l % 100L == 0L) {
            dwarf.summonGolem(serverWorld, l, 3);
        }
    }

    public static boolean isHostileNearby(LivingEntity entity) {
        return entity.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_HOSTILE);
    }

    public static boolean wasHurt(LivingEntity entity) {
        return entity.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
    }
}
