package warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor;

import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.GolemSensor;

public class LordLastSeenSensor extends GolemSensor {

   @Override
   protected void doTick(ServerLevel world, LivingEntity entity) {
      LordLastSeenSensor.checkForNearbyGolem(entity);
   }

   public static void checkForNearbyGolem(LivingEntity entity) {
      Optional<List<LivingEntity>> optional = entity.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
      if (optional.isEmpty()) {
         return;
      }
      boolean bl = optional.get().stream().anyMatch(livingEntity -> livingEntity.getType().equals(Entityinit.DWARF) && ((DwarfEntity)livingEntity).getProfession().equals(DwarfProfessionRecord.Lord));
      if (bl) {
         GolemSensor.golemDetected(entity);
      }
   }
}