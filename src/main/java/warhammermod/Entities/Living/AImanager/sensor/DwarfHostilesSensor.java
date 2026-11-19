package warhammermod.Entities.Living.AImanager.sensor;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestVisibleLivingEntitySensor;
import net.minecraft.server.level.ServerLevel;
import warhammermod.utils.Registry.Entityinit;

public class DwarfHostilesSensor extends NearestVisibleLivingEntitySensor {
    private static final ImmutableMap<net.minecraft.world.entity.EntityType<?>, Float> ACCEPTABLE_DISTANCE_FROM_HOSTILES = ImmutableMap.<net.minecraft.world.entity.EntityType<?>, Float>builder()
            .put(net.minecraft.world.entity.EntityTypes.DROWNED, 8.0F).put(net.minecraft.world.entity.EntityTypes.EVOKER, 12.0F)
            .put(net.minecraft.world.entity.EntityTypes.HUSK, 8.0F).put(net.minecraft.world.entity.EntityTypes.ILLUSIONER, 12.0F)
            .put(net.minecraft.world.entity.EntityTypes.PILLAGER, 15.0F).put(net.minecraft.world.entity.EntityTypes.RAVAGER, 12.0F)
            .put(net.minecraft.world.entity.EntityTypes.VEX, 8.0F).put(net.minecraft.world.entity.EntityTypes.VINDICATOR, 10.0F)
            .put(net.minecraft.world.entity.EntityTypes.ZOGLIN, 10.0F).put(net.minecraft.world.entity.EntityTypes.ZOMBIE, 8.0F)
            .put(net.minecraft.world.entity.EntityTypes.ZOMBIE_VILLAGER, 8.0F)
            .put(Entityinit.SKAVEN, 15F).build();

    @Override
    protected boolean isMatchingEntity(ServerLevel level, LivingEntity livingEntity, LivingEntity livingEntity2) {
        return this.isHostile(livingEntity2) && this.isClose(livingEntity, livingEntity2);
    }

    private boolean isClose(LivingEntity livingEntity, LivingEntity livingEntity2) {
        float f = ACCEPTABLE_DISTANCE_FROM_HOSTILES.get(livingEntity2.getType());
        return livingEntity2.distanceToSqr(livingEntity) <= (double) (f * f);
    }

    @Override
    protected MemoryModuleType<LivingEntity> getMemoryToSet() {
        return MemoryModuleType.NEAREST_HOSTILE;
    }

    private boolean isHostile(LivingEntity livingEntity) {
        return ACCEPTABLE_DISTANCE_FROM_HOSTILES.containsKey(livingEntity.getType());
    }
}
