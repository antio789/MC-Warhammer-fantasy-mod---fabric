/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;

public class BoneMealTaskDwarf
extends Behavior<DwarfEntity> {
    private static final int MAX_DURATION = 80;
    private long startTime;
    private long lastEndEntityAge;
    private int duration;
    private Optional<BlockPos> pos = Optional.empty();

    public BoneMealTaskDwarf() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean shouldRun(ServerLevel serverWorld, DwarfEntity dwarfEntity) {
        if (dwarfEntity.tickCount % 10 != 0 || this.lastEndEntityAge != 0L && this.lastEndEntityAge + 160L > (long)dwarfEntity.tickCount) {
            return false;
        }
        if (dwarfEntity.getInventory().countItem(Items.BONE_MEAL) <= 0) {
            return false;
        }
        this.pos = this.findBoneMealPos(serverWorld, dwarfEntity);
        return this.pos.isPresent();
    }

    @Override
    protected boolean shouldKeepRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        return this.duration < 80 && this.pos.isPresent();
    }

    private Optional<BlockPos> findBoneMealPos(ServerLevel world, DwarfEntity entity) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        Optional<BlockPos> optional = Optional.empty();
        int i = 0;
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int l = -1; l <= 1; ++l) {
                    mutable.setWithOffset(entity.blockPosition(), j, k, l);
                    if (!this.canBoneMeal(mutable, world) || world.random.nextInt(++i) != 0) continue;
                    optional = Optional.of(mutable.immutable());
                }
            }
        }
        return optional;
    }

    private boolean canBoneMeal(BlockPos pos, ServerLevel world) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block instanceof CropBlock && !((CropBlock)block).isMaxAge(blockState);
    }

    @Override
    protected void run(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        this.addLookWalkTargets(dwarfEntity);
        dwarfEntity.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.BONE_MEAL));
        this.startTime = l;
        this.duration = 0;
    }

    private void addLookWalkTargets(DwarfEntity villager) {
        this.pos.ifPresent(pos -> {
            BlockPosTracker blockPosLookTarget = new BlockPosTracker((BlockPos)pos);
            villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, blockPosLookTarget);
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPosLookTarget, 0.5f, 1));
        });
    }

    @Override
    protected void finishRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        this.lastEndEntityAge = dwarfEntity.tickCount;
    }

    @Override
    protected void keepRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        BlockPos blockPos = this.pos.get();
        if (l < this.startTime || !blockPos.closerToCenterThan(dwarfEntity.position(), 1.0)) {
            return;
        }
        ItemStack itemStack = ItemStack.EMPTY;
        SimpleContainer simpleInventory = dwarfEntity.getInventory();
        int i = simpleInventory.getContainerSize();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack2 = simpleInventory.getItem(j);
            if (!itemStack2.is(Items.BONE_MEAL)) continue;
            itemStack = itemStack2;
            break;
        }
        if (!itemStack.isEmpty() && BoneMealItem.growCrop(itemStack, serverWorld, blockPos)) {
            serverWorld.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos, 15);
            this.pos = this.findBoneMealPos(serverWorld, dwarfEntity);
            this.addLookWalkTargets(dwarfEntity);
            this.startTime = l + 40L;
        }
        ++this.duration;
    }


}

