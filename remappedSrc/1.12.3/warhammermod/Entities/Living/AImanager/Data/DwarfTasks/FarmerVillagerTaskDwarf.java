package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.List;

public class FarmerVillagerTaskDwarf extends MultiTickTask<DwarfEntity> {
    private static final int MAX_RUN_TIME = 200;
    public static final float WALK_SPEED = 0.5F;
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;
    private final List<BlockPos> targetPositions = Lists.newArrayList();

    public FarmerVillagerTaskDwarf() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
    }

    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        if (!serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        } else if (dwarfEntity.getProfession() != DwarfProfessionRecord.FARMER) {
            return false;
        } else {
            BlockPos.Mutable mutable = dwarfEntity.getBlockPos().mutableCopy();
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
    private BlockPos chooseRandomTarget(ServerWorld world) {
        return this.targetPositions.isEmpty() ? null : (BlockPos)this.targetPositions.get(world.getRandom().nextInt(this.targetPositions.size()));
    }

    private boolean isSuitableTarget(BlockPos pos, ServerWorld world) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        Block block2 = world.getBlockState(pos.down()).getBlock();
        return block instanceof CropBlock && ((CropBlock)block).isMature(blockState) || blockState.isAir() && block2 instanceof FarmlandBlock;
    }

    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        if (l > this.nextResponseTime && this.currentTarget != null) {
            dwarfEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
            dwarfEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
        }

    }

    protected void finishRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        dwarfEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = l + 40L;
    }

    protected void keepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        if (this.currentTarget == null || this.currentTarget.isWithinDistance(dwarfEntity.getPos(), 1.0)) {
            if (this.currentTarget != null && l > this.nextResponseTime) {
                BlockState blockState = serverWorld.getBlockState(this.currentTarget);
                Block block = blockState.getBlock();
                Block block2 = serverWorld.getBlockState(this.currentTarget.down()).getBlock();
                if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState)) {
                    serverWorld.breakBlock(this.currentTarget, true, dwarfEntity);
                }

                if (blockState.isAir() && block2 instanceof FarmlandBlock && dwarfEntity.hasSeedToPlant()) {
                    SimpleInventory simpleInventory = dwarfEntity.getInventory();

                    for(int i = 0; i < simpleInventory.size(); ++i) {
                        ItemStack itemStack = simpleInventory.getStack(i);
                        boolean bl = false;
                        if (!itemStack.isEmpty() && itemStack.isIn(ItemTags.VILLAGER_PLANTABLE_SEEDS)) {
                            Item var13 = itemStack.getItem();
                            if (var13 instanceof BlockItem) {
                                BlockItem blockItem = (BlockItem)var13;
                                BlockState blockState2 = blockItem.getBlock().getDefaultState();
                                serverWorld.setBlockState(this.currentTarget, blockState2);
                                serverWorld.emitGameEvent(GameEvent.BLOCK_PLACE, this.currentTarget, GameEvent.Emitter.of(dwarfEntity, blockState2));
                                bl = true;
                            }
                        }

                        if (bl) {
                            serverWorld.playSound((PlayerEntity)null, (double)this.currentTarget.getX(), (double)this.currentTarget.getY(), (double)this.currentTarget.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            itemStack.decrement(1);
                            if (itemStack.isEmpty()) {
                                simpleInventory.setStack(i, ItemStack.EMPTY);
                            }
                            break;
                        }
                    }
                }

                if (block instanceof CropBlock && !((CropBlock)block).isMature(blockState)) {
                    this.targetPositions.remove(this.currentTarget);
                    this.currentTarget = this.chooseRandomTarget(serverWorld);
                    if (this.currentTarget != null) {
                        this.nextResponseTime = l + 20L;
                        dwarfEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
                        dwarfEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
                    }
                }
            }

            ++this.ticksRan;
        }
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        return this.ticksRan < 200;
    }
}
