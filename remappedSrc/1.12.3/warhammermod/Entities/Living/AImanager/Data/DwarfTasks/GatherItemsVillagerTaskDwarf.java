/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.TargetUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.Set;
import java.util.stream.Collectors;

public class GatherItemsVillagerTaskDwarf
extends MultiTickTask<DwarfEntity> {
    private Set<Item> items = ImmutableSet.of();

    public GatherItemsVillagerTaskDwarf() {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
    }

    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        return TargetUtil.canSee(dwarfEntity.getBrain(), MemoryModuleType.INTERACTION_TARGET, Entityinit.DWARF);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        return this.shouldRun(serverWorld, dwarfEntity);
    }

    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        DwarfEntity dwarf2 = (DwarfEntity)dwarfEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).get();
        TargetUtil.lookAtAndWalkTowardsEachOther(dwarfEntity, dwarf2, 0.5f, 2);
        this.items = GatherItemsVillagerTaskDwarf.getGatherableItems(dwarfEntity, dwarf2);
    }

    protected void keepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        DwarfEntity dwarf2 = (DwarfEntity)dwarfEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).get();
        if (dwarfEntity.squaredDistanceTo(dwarf2) > 5.0) {
            return;
        }
        TargetUtil.lookAtAndWalkTowardsEachOther(dwarfEntity, dwarf2, 0.5f, 2);
        dwarfEntity.talkWithVillager(serverWorld, dwarf2, l);
        if (dwarfEntity.wantsToStartBreeding() && (dwarfEntity.getProfession() == DwarfProfessionRecord.FARMER || dwarf2.canBreed())) {
            GatherItemsVillagerTaskDwarf.giveHalfOfStack(dwarfEntity, DwarfEntity.ITEM_FOOD_VALUES.keySet(), dwarf2);
        }
        if (dwarf2.getProfession() == DwarfProfessionRecord.FARMER && dwarfEntity.getInventory().count(Items.WHEAT) > Items.WHEAT.getMaxCount() / 2) {
            GatherItemsVillagerTaskDwarf.giveHalfOfStack(dwarfEntity, ImmutableSet.of(Items.WHEAT), dwarf2);
        }
        if (!this.items.isEmpty() && dwarfEntity.getInventory().containsAny(this.items)) {
            GatherItemsVillagerTaskDwarf.giveHalfOfStack(dwarfEntity, this.items, dwarf2);
        }
    }

    protected void finishRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
    }

    private static Set<Item> getGatherableItems(DwarfEntity entity, DwarfEntity target) {
        ImmutableSet<Item> immutableSet = target.getProfession().gatherableItems();
        ImmutableSet<Item> immutableSet2 = entity.getProfession().gatherableItems();
        return immutableSet.stream().filter(item -> !immutableSet2.contains(item)).collect(Collectors.toSet());
    }

    private static void giveHalfOfStack(DwarfEntity dwarf, Set<Item> validItems, LivingEntity target) {
        SimpleInventory simpleInventory = dwarf.getInventory();
        ItemStack itemStack = ItemStack.EMPTY;
        for (int i = 0; i < simpleInventory.size(); ++i) {
            int j;
            Item item;
            ItemStack itemStack2 = simpleInventory.getStack(i);
            if (itemStack2.isEmpty() || !validItems.contains(item = itemStack2.getItem())) continue;
            if (itemStack2.getCount() > itemStack2.getMaxCount() / 2) {
                j = itemStack2.getCount() / 2;
            } else {
                if (itemStack2.getCount() <= 24) continue;
                j = itemStack2.getCount() - 24;
            }
            itemStack2.decrement(j);
            itemStack = new ItemStack(item, j);
            break;
        }
        if (!itemStack.isEmpty()) {
            TargetUtil.give(dwarf, itemStack, target.getPos());
        }
    }


}

