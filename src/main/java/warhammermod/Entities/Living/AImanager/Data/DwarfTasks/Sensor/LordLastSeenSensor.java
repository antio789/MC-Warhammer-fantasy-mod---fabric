package warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.List;
import java.util.Optional;

public class LordLastSeenSensor extends GolemSensor {

    @Override
    protected void doTick(net.minecraft.server.level.ServerLevel level, LivingEntity entity) {
        LordLastSeenSensor.senseLord(entity);
    }

    public static void senseLord(LivingEntity entity) {
        Optional<List<LivingEntity>> optional = entity.getBrain().getMemory(MemoryModuleType.MOBS);
        if (optional.isEmpty()) {
            return;
        }
        boolean lordSeen = optional.get().stream().anyMatch(livingEntity -> livingEntity.getType().equals(Entityinit.DWARF) && ((DwarfEntity) livingEntity).getProfession() == DwarfProfessionRecord.Lord);
        if (lordSeen) {
            GolemSensor.golemDetected(entity);
        }
    }

    @Override
    public java.util.Set<net.minecraft.world.entity.ai.memory.MemoryModuleType<?>> requires() {
        return java.util.Set.of(MemoryModuleType.MOBS, MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    }
}
