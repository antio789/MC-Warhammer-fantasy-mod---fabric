package warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.server.world.ServerWorld;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.List;
import java.util.Optional;

public class LordLastSeenSensor extends GolemLastSeenSensor {

   @Override
   protected void sense(ServerWorld world, LivingEntity entity) {
      LordLastSeenSensor.senseIronGolem(entity);
   }

   public static void senseIronGolem(LivingEntity entity) {
      Optional<List<LivingEntity>> optional = entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS);
      if (optional.isEmpty()) {
         return;
      }
      boolean bl = optional.get().stream().anyMatch(livingEntity -> livingEntity.getType().equals(Entityinit.DWARF) && ((DwarfEntity)livingEntity).getProfession().equals(DwarfProfessionRecord.Lord));
      if (bl) {
         GolemLastSeenSensor.rememberIronGolem(entity);
      }
   }
}