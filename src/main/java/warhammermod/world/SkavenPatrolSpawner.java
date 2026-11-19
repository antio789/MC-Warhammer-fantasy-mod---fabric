package warhammermod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.monster.PatrollingMonster;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.utils.Registry.Entityinit;

public class SkavenPatrolSpawner implements CustomSpawner {

    private int nextTick;

    @Override
    public void tick(ServerLevel world, boolean spawnMonsters) {
        if (!spawnMonsters) {
            return;
        }
        if (!world.getGameRules().get(GameRules.SPAWN_PATROLS)) {
            return;
        }
        RandomSource random = world.getRandom();
        --this.nextTick;
        if (this.nextTick > 0) {
            return;
        }
        this.nextTick += 1500 + random.nextInt(600);
        if (world.isBrightOutside() && random.nextInt(1) == 0) {
            int playerlistsize = world.players().size();
            if (playerlistsize < 1) {
                return;
            }
            Player player = world.players().get(random.nextInt(playerlistsize));
            if (player.isSpectator() || world.isCloseToVillage(player.blockPosition(), 2)) {
                return;
            }

            int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
            int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
            BlockPos.MutableBlockPos mutable = player.blockPosition().mutable().move(j, 0, k);
            if (!world.hasChunksAt(mutable.getX() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getZ() + 10)) {
                return;
            }
            int patrol_size = 0;
            DifficultyInstance difficulty = world.getCurrentDifficultyAt(mutable);
            int o = (int) Math.ceil(difficulty.getEffectiveDifficulty()) + 3;
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
        }
    }

    /**
     * @param captain whether the skaven is the captain of a patrol
     */
    private boolean spawnSkaven(ServerLevel world, BlockPos pos, RandomSource random, boolean captain) {
        BlockState blockState = world.getBlockState(pos);
        if (!NaturalSpawner.isValidEmptySpawnBlock(world, pos, blockState, blockState.getFluidState(), Entityinit.SKAVEN)) {
            return false;
        }
        if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(Entityinit.SKAVEN, world, EntitySpawnReason.PATROL, pos, random)) {
            return false;
        }
        SkavenEntity patrolEntity = Entityinit.SKAVEN.create(world, EntitySpawnReason.PATROL);
        if (patrolEntity != null) {
            if (captain) {
                patrolEntity.setPatrolLeader(true);
                patrolEntity.findPatrolTarget();
            }
            patrolEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
            patrolEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), EntitySpawnReason.PATROL, null);
            world.addFreshEntityWithPassengers(patrolEntity);
            return true;
        }
        return false;
    }
}
