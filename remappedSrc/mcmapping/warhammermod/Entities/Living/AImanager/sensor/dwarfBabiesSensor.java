package warhammermod.Entities.Living.AImanager.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import warhammermod.utils.Registry.Entityinit;

import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class dwarfBabiesSensor extends Sensor<LivingEntity> {
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
   }

   protected void doTick(ServerLevel p_212872_1_, LivingEntity p_212872_2_) {
      p_212872_2_.getBrain().setMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, this.getVisibleVillagerBabies(p_212872_2_));
   }

   private List<LivingEntity> getVisibleVillagerBabies(LivingEntity entities) {
      return ImmutableList.copyOf(this.getVisibleMobs(entities).findAll(this::isVillagerBaby));
   }

   private boolean isVillagerBaby(LivingEntity entity) {
      return entity.getType() == Entityinit.DWARF && entity.isBaby();
   }

   private NearestVisibleLivingEntities getVisibleMobs(LivingEntity entity) {
      return entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
   }
}