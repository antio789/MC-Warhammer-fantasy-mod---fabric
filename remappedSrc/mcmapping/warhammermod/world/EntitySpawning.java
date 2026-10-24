package warhammermod.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.*;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.level.levelgen.Heightmap;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.Objects;
import java.util.Optional;

import static net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn;
import static net.minecraft.world.entity.Mob.checkMobSpawnRules;

public class EntitySpawning {
    public static void SpawnRestriction(){
        SpawnPlacements.register(Entityinit.PEGASUS, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Goat::checkGoatSpawnRules);
        SpawnPlacements.register(Entityinit.SKAVEN,SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,SkavenEntity::canSpawn);
    }
    public static void addSpawn(){
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN), MobCategory.CREATURE,Entityinit.PEGASUS,3,1,3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN),MobCategory.MONSTER,Entityinit.SKAVEN,80,3,6);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS),MobCategory.MONSTER,Entityinit.SKAVEN,30,3,5);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.HAS_VILLAGE_PLAINS),MobCategory.MONSTER,Entityinit.SKAVEN,30,3,5);
    }




}
