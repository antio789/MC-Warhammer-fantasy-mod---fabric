package warhammermod.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.Objects;
import java.util.Optional;

import static net.minecraft.entity.mob.HostileEntity.isSpawnDark;
import static net.minecraft.entity.mob.MobEntity.canMobSpawn;

public class EntitySpawning {
    public static void SpawnRestriction(){
        SpawnRestriction.register(Entityinit.PEGASUS, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GoatEntity::canSpawn);
        SpawnRestriction.register(Entityinit.SKAVEN,SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,SkavenEntity::canSpawn);
    }
    public static void addSpawn(){
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN), SpawnGroup.CREATURE,Entityinit.PEGASUS,3,1,3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN),SpawnGroup.MONSTER,Entityinit.SKAVEN,80,3,6);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS),SpawnGroup.MONSTER,Entityinit.SKAVEN,30,3,5);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE),SpawnGroup.MONSTER,Entityinit.SKAVEN,30,3,5);
    }




}
