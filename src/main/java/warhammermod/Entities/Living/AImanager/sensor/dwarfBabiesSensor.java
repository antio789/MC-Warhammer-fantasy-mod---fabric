package warhammermod.Entities.Living.AImanager.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import warhammermod.utils.Registry.Entityinit;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class dwarfBabiesSensor extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
    }

    @Override
    protected void doTick(ServerLevel level, LivingEntity entity) {
        entity.getBrain().setMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, this.getVisibleVillagerBabies(entity));
    }

    private List<LivingEntity> getVisibleVillagerBabies(LivingEntity entities) {
        return ImmutableList.copyOf(this.getVisibleEntities(entities).findAll(this::isVillagerBaby));
    }

    private boolean isVillagerBaby(LivingEntity entity) {
        return entity.getType() == Entityinit.DWARF && entity.isBaby();
    }

    private NearestVisibleLivingEntities getVisibleEntities(LivingEntity entity) {
        Optional<NearestVisibleLivingEntities> optional = entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        return optional.orElseGet(NearestVisibleLivingEntities::empty);
    }
}
