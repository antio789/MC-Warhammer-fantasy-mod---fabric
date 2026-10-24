package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class FarmerVillagerTaskDwarf extends Behavior<DwarfEntity> {
    private static final int MAX_RUN_TIME = 200;
    public static final float WALK_SPEED = 0.5F;
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;
    private final List<BlockPos> targetPositions = Lists.newArrayList();

    public FarmerVillagerTaskDwarf() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryStatus.VALUE_PRESENT));
    }

    protected boolean shouldRun(ServerLevel serverWorld, DwarfEntity dwarfEntity) {
        if (!serverWorld.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        } else if (dwarfEntity.getProfession() != DwarfProfessionRecord.FARMER) {
            return false;
        } else {
            BlockPos.MutableBlockPos mutable = dwarfEntity.blockPosition().mutable();
            this.targetPositions.clear();

            for(int i = -1; i <= 1; ++i) {
                for(int j = -1; j <= 1; ++j) {
                    for(int k = -1; k <= 1; ++k) {
                        mutable.set(dwarfEntity.getX() + (double)i, dwarfEntity.getY() + (double)j, dwarfEntity.getZ() + (double)k);
                        if (this.isSuitableTarget(mutable, serverWorld)) {
                            this.targetPositions.add(new BlockPos(mutable));
                        }
                    }
                }
            }

            this.currentTarget = this.chooseRandomTarget(serverWorld);
            return this.currentTarget != null;
        }
    }

    @Nullable
    private BlockPos chooseRandomTarget(ServerLevel world) {
        return this.targetPositions.isEmpty() ? null : (BlockPos)this.targetPositions.get(world.getRandom().nextInt(this.targetPositions.size()));
    }

    private boolean isSuitableTarget(BlockPos pos, ServerLevel world) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        Block block2 = world.getBlockState(pos.below()).getBlock();
        return block instanceof CropBlock && ((CropBlock)block).isMaxAge(blockState) || blockState.isAir() && block2 instanceof FarmBlock;
    }

    protected void run(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        if (l > this.nextResponseTime && this.currentTarget != null) {
            dwarfEntity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.currentTarget));
            dwarfEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.currentTarget), 0.5F, 1));
        }

    }

    protected void finishRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        dwarfEntity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = l + 40L;
    }

    protected void keepRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        if (this.currentTarget == null || this.currentTarget.closerToCenterThan(dwarfEntity.position(), 1.0)) {
            if (this.currentTarget != null && l > this.nextResponseTime) {
                BlockState blockState = serverWorld.getBlockState(this.currentTarget);
                Block block = blockState.getBlock();
                Block block2 = serverWorld.getBlockState(this.currentTarget.below()).getBlock();
                if (block instanceof CropBlock && ((CropBlock)block).isMaxAge(blockState)) {
                    serverWorld.destroyBlock(this.currentTarget, true, dwarfEntity);
                }

                if (blockState.isAir() && block2 instanceof FarmBlock && dwarfEntity.hasSeedToPlant()) {
                    SimpleContainer simpleInventory = dwarfEntity.getInventory();

                    for(int i = 0; i < simpleInventory.getContainerSize(); ++i) {
                        ItemStack itemStack = simpleInventory.getItem(i);
                        boolean bl = false;
                        if (!itemStack.isEmpty() && itemStack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS)) {
                            Item var13 = itemStack.getItem();
                            if (var13 instanceof BlockItem) {
                                BlockItem blockItem = (BlockItem)var13;
                                BlockState blockState2 = blockItem.getBlock().defaultBlockState();
                                serverWorld.setBlockAndUpdate(this.currentTarget, blockState2);
                                serverWorld.gameEvent(GameEvent.BLOCK_PLACE, this.currentTarget, GameEvent.Context.of(dwarfEntity, blockState2));
                                bl = true;
                            }
                        }

                        if (bl) {
                            serverWorld.playSound((Player)null, (double)this.currentTarget.getX(), (double)this.currentTarget.getY(), (double)this.currentTarget.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                            itemStack.shrink(1);
                            if (itemStack.isEmpty()) {
                                simpleInventory.setItem(i, ItemStack.EMPTY);
                            }
                            break;
                        }
                    }
                }

                if (block instanceof CropBlock && !((CropBlock)block).isMaxAge(blockState)) {
                    this.targetPositions.remove(this.currentTarget);
                    this.currentTarget = this.chooseRandomTarget(serverWorld);
                    if (this.currentTarget != null) {
                        this.nextResponseTime = l + 20L;
                        dwarfEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.currentTarget), 0.5F, 1));
                        dwarfEntity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.currentTarget));
                    }
                }
            }

            ++this.ticksRan;
        }
    }

    protected boolean shouldKeepRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        return this.ticksRan < 200;
    }
}
