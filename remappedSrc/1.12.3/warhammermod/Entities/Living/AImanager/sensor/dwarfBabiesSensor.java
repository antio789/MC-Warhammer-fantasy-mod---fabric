package warhammermod.Entities.Living.AImanager.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import warhammermod.utils.Registry.Entityinit;

import java.util.List;
import java.util.Set;

public class dwarfBabiesSensor extends Sensor<LivingEntity> {
   public Set<MemoryModuleType<?>> getOutputMemoryModules() {
      return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
   }

   protected void sense(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      p_212872_2_.getBrain().remember(MemoryModuleType.VISIBLE_VILLAGER_BABIES, this.getVisibleVillagerBabies(p_212872_2_));
   }

   private List<LivingEntity> getVisibleVillagerBabies(LivingEntity entities) {
      return ImmutableList.copyOf(this.getVisibleMobs(entities).iterate(this::isVillagerBaby));
   }

   private boolean isVillagerBaby(LivingEntity entity) {
      return entity.getType() == Entityinit.DWARF && entity.isBaby();
   }

   private LivingTargetCache getVisibleMobs(LivingEntity entity) {
      return entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty());
   }
}