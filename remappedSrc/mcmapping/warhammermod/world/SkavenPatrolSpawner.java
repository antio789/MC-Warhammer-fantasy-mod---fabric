package warhammermod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.reference;

public class SkavenPatrolSpawner implements CustomSpawner {


    private int cooldown;

    @Override
    public int tick(ServerLevel world, boolean spawnMonsters, boolean spawnAnimals) {
        if (!spawnMonsters) {
            return 0;
        }
        if (!world.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
            return 0;
        }
        RandomSource random = world.random;
        --this.cooldown;
        if (this.cooldown > 0) {
            return 0;
        }
        this.cooldown += 1500 + random.nextInt(600);//cd 2000 + rand 400
        long l = world.getDayTime() / 24000L;
        if (l < 5L) {
            return 0;
        }
        if (random.nextInt(1) != 0) {//5
            return 0;
        }
        int playerlistsize = world.players().size();
        if (playerlistsize < 1) {
            return 0;
        }
        Player playerEntity = world.players().get(random.nextInt(playerlistsize));
        if (playerEntity.isSpectator()) {
            return 0;
        }
        if (world.isCloseToVillage(playerEntity.blockPosition(), 2)) {
            return 0;
        }
        int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        BlockPos.MutableBlockPos mutable = playerEntity.blockPosition().mutable().move(j, 0, k);
        if (!world.hasChunksAt(mutable.getX() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getZ() + 10)) {
            return 0;
        }
        Holder<Biome> registryEntry = world.getBiome(mutable);
        if (registryEntry.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
            return 0;
        }
        int patrol_size = 0;
        int o = (int)Math.ceil(world.getCurrentDifficultyAt(mutable).getEffectiveDifficulty()) + 3;
        for (int p = 0; p < o; ++p) {
            ++patrol_size;
            mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
            if (p == 0) {
                if (!this.spawnSkaven(world, mutable, random, true)) {
                    break;
                }
            } else {
                this.spawnSkaven(world, mutable, random, false);
            }
            mutable.setX(mutable.getX() + random.nextInt(5) - random.nextInt(5));
            mutable.setZ(mutable.getZ() + random.nextInt(5) - random.nextInt(5));
        }
        return patrol_size;
    }

    /**
     * @param captain whether the pillager is the captain of a patrol
     */
    private boolean spawnSkaven(ServerLevel world, BlockPos pos, RandomSource random, boolean captain) {

        BlockState blockState = world.getBlockState(pos);
        if (!NaturalSpawner.isValidEmptySpawnBlock(world, pos, blockState, blockState.getFluidState(), Entityinit.SKAVEN)) {
            return false;
        }
        if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(Entityinit.SKAVEN, world, MobSpawnType.PATROL, pos, random)) {
            return false;
        }
        SkavenEntity patrolEntity = Entityinit.SKAVEN.create(world);
        if (patrolEntity != null) {
            if (captain) {
                patrolEntity.setPatrolLeader(true);
                patrolEntity.findPatrolTarget();
            }
            patrolEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
            patrolEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), MobSpawnType.PATROL, null);
            world.addFreshEntityWithPassengers(patrolEntity);
            return true;
        }
        return false;
    }
}
