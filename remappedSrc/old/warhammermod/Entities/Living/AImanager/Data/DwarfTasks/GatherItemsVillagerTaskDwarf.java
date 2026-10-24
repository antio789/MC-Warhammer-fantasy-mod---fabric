/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GatherItemsVillagerTaskDwarf
extends Behavior<DwarfEntity> {
    private Set<Item> items = ImmutableSet.of();

    public GatherItemsVillagerTaskDwarf() {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
    }

    protected boolean shouldRun(ServerLevel serverWorld, DwarfEntity dwarfEntity) {
        return BehaviorUtils.targetIsValid(dwarfEntity.getBrain(), MemoryModuleType.INTERACTION_TARGET, Entityinit.DWARF);
    }

    protected boolean shouldKeepRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        return this.shouldRun(serverWorld, dwarfEntity);
    }

    protected void run(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        DwarfEntity dwarf2 = (DwarfEntity)dwarfEntity.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        BehaviorUtils.lockGazeAndWalkToEachOther(dwarfEntity, dwarf2, 0.5f, 2);
        this.items = GatherItemsVillagerTaskDwarf.getGatherableItems(dwarfEntity, dwarf2);
    }

    protected void keepRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        DwarfEntity dwarf2 = (DwarfEntity)dwarfEntity.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        if (dwarfEntity.distanceToSqr(dwarf2) > 5.0) {
            return;
        }
        BehaviorUtils.lockGazeAndWalkToEachOther(dwarfEntity, dwarf2, 0.5f, 2);
        dwarfEntity.talkWithVillager(serverWorld, dwarf2, l);
        if (dwarfEntity.wantsToStartBreeding() && (dwarfEntity.getProfession() == DwarfProfessionRecord.FARMER || dwarf2.canBreed())) {
            GatherItemsVillagerTaskDwarf.giveHalfOfStack(dwarfEntity, DwarfEntity.ITEM_FOOD_VALUES.keySet(), dwarf2);
        }
        if (dwarf2.getProfession() == DwarfProfessionRecord.FARMER && dwarfEntity.getInventory().countItem(Items.WHEAT) > Items.WHEAT.getDefaultMaxStackSize() / 2) {
            GatherItemsVillagerTaskDwarf.giveHalfOfStack(dwarfEntity, ImmutableSet.of(Items.WHEAT), dwarf2);
        }
        if (!this.items.isEmpty() && dwarfEntity.getInventory().hasAnyOf(this.items)) {
            GatherItemsVillagerTaskDwarf.giveHalfOfStack(dwarfEntity, this.items, dwarf2);
        }
    }

    protected void finishRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
    }

    private static Set<Item> getGatherableItems(DwarfEntity entity, DwarfEntity target) {
        ImmutableSet<Item> immutableSet = target.getProfession().gatherableItems();
        ImmutableSet<Item> immutableSet2 = entity.getProfession().gatherableItems();
        return immutableSet.stream().filter(item -> !immutableSet2.contains(item)).collect(Collectors.toSet());
    }

    private static void giveHalfOfStack(DwarfEntity dwarf, Set<Item> validItems, LivingEntity target) {
        SimpleContainer simpleInventory = dwarf.getInventory();
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < simpleInventory.getContainerSize(); ++i) {
            int j;
            Item item;
            ItemStack itemStack2 = simpleInventory.getItem(i);
            if (itemStack2.isEmpty() || !validItems.contains(item = itemStack2.getItem())) continue;
            if (itemStack2.getCount() > itemStack2.getMaxStackSize() / 2) {
                j = itemStack2.getCount() / 2;
            } else {
                if (itemStack2.getCount() <= 24) continue;
                j = itemStack2.getCount() - 24;
            }
            itemStack2.shrink(j);
            itemStack = new ItemStack(item, j);
            break;
        }
        if (!itemStack.isEmpty()) {
            BehaviorUtils.throwItem(dwarf, itemStack, target.position());
        }
    }


}

