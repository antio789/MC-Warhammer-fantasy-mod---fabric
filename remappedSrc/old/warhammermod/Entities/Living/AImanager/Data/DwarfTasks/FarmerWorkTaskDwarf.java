/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableList;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.ItemsInit;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;

public class FarmerWorkTaskDwarf
extends VillagerWorkTaskDwarf {
    private static final List<Item> COMPOSTABLES = ImmutableList.of(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS);

    @Override
    protected void performAdditionalWork(ServerLevel world, DwarfEntity entity) {
        Optional<GlobalPos> optional = entity.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (optional.isEmpty()) {
            return;
        }
        GlobalPos globalPos = optional.get();
        BlockState blockState = world.getBlockState(globalPos.pos());
        if (blockState.is(Blocks.COMPOSTER)) {
            this.craftAndDropBread(entity);
            this.compostSeeds(world, entity, globalPos, blockState);
        }
    }

    private void compostSeeds(ServerLevel world, DwarfEntity entity, GlobalPos pos, BlockState composterState) {
        BlockPos blockPos = pos.pos();
        if (composterState.getValue(ComposterBlock.LEVEL) == 8) {
            composterState = ComposterBlock.extractProduce(entity, composterState, world, blockPos);
        }
        int i = 20;
        int j = 10;
        int[] is = new int[COMPOSTABLES.size()];
        SimpleContainer simpleInventory = entity.getInventory();
        int k = simpleInventory.getContainerSize();
        BlockState blockState = composterState;
        for (int l = k - 1; l >= 0 && i > 0; --l) {
            int o;
            ItemStack itemStack = simpleInventory.getItem(l);
            int m = COMPOSTABLES.indexOf(itemStack.getItem());
            if (m == -1) continue;
            int n = itemStack.getCount();
            is[m] = o = is[m] + n;
            int p = Math.min(Math.min(o - 10, i), n);
            if (p <= 0) continue;
            i -= p;
            for (int q = 0; q < p; ++q) {
                if ((blockState = ComposterBlock.insertItem(entity, blockState, world, itemStack, blockPos)).getValue(ComposterBlock.LEVEL) != 7) continue;
                this.syncComposterEvent(world, composterState, blockPos, blockState);
                return;
            }
        }
        this.syncComposterEvent(world, composterState, blockPos, blockState);
    }

    private void syncComposterEvent(ServerLevel world, BlockState oldState, BlockPos pos, BlockState newState) {
        world.levelEvent(LevelEvent.COMPOSTER_FILL, pos, newState != oldState ? 1 : 0);
    }

    private void craftAndDropBread(DwarfEntity entity) {
        SimpleContainer simpleInventory = entity.getInventory();
        if (simpleInventory.countItem(ItemsInit.BEER) > 12) {
            return;
        }
        int i = simpleInventory.countItem(Items.WHEAT);
        int j = 3;
        int k = 3;
        int l = Math.min(6, i / 6);
        if (l == 0) {
            return;
        }
        int m = l * 6;
        simpleInventory.removeItemType(Items.WHEAT, m);
        ItemStack itemStack = simpleInventory.addItem(new ItemStack(ItemsInit.BEER, l));
        if (!itemStack.isEmpty()) {
            entity.spawnAtLocation(itemStack, 0.5f);
        }
    }
}

