package warhammermod.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.level.levelgen.Heightmap;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.utils.Registry.Entityinit;

public class Spawn {
    public static void addEntitySpawn() {
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.MONSTER, Entityinit.SKAVEN, 30, 3, 6);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.CREATURE, Entityinit.Pegasus, 4, 1, 4);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.CREATURE, Entityinit.DWARF, 2, 1, 2);

        SpawnPlacements.register(Entityinit.SKAVEN, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SkavenEntity::checkSkavenSpawnRules);
        SpawnPlacements.register(Entityinit.Pegasus, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(Entityinit.DWARF, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, reason, pos, random) -> true);
    }
}
