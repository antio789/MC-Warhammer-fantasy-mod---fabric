/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public class HoldTradeOffersTaskDwarf extends Behavior<DwarfEntity> {
    private static final int RUN_INTERVAL = 900;
    private static final int OFFER_SHOWING_INTERVAL = 40;
    @Nullable
    private ItemStack customerHeldStack;
    private final List<ItemStack> offers = Lists.newArrayList();
    private int offerShownTicks;
    private int offerIndex;
    private int ticksLeft;

    public HoldTradeOffersTaskDwarf(int minRunTime, int maxRunTime) {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT), minRunTime, maxRunTime);
    }

    @Override
    public boolean shouldRun(ServerLevel serverWorld, DwarfEntity dwarf) {
        Brain<DwarfEntity> brain = dwarf.getBrain();
        if (brain.getMemory(MemoryModuleType.INTERACTION_TARGET).isEmpty()) {
            return false;
        }
        LivingEntity livingEntity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        return livingEntity.getType() == EntityType.PLAYER && dwarf.isAlive() && livingEntity.isAlive() && !dwarf.isBaby() && dwarf.distanceToSqr(livingEntity) <= 17.0;
    }

    @Override
    public boolean shouldKeepRunning(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        return this.shouldRun(serverWorld, dwarf) && this.ticksLeft > 0 && dwarf.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    @Override
    public void run(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        super.start(serverWorld, dwarf, l);
        this.findPotentialCustomer(dwarf);
        this.offerShownTicks = 0;
        this.offerIndex = 0;
        this.ticksLeft = 40;
    }

    @Override
    public void keepRunning(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        LivingEntity livingEntity = this.findPotentialCustomer(dwarf);
        this.setupOffers(livingEntity, dwarf);
        if (!this.offers.isEmpty()) {
            this.refreshShownOffer(dwarf);
        } else {
            HoldTradeOffersTaskDwarf.holdNothing(dwarf);
            this.ticksLeft = Math.min(this.ticksLeft, 40);
        }
        --this.ticksLeft;
    }

    @Override
    public void finishRunning(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        super.stop(serverWorld, dwarf, l);
        dwarf.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        HoldTradeOffersTaskDwarf.holdNothing(dwarf);
        this.customerHeldStack = null;
    }

    private void setupOffers(LivingEntity customer, DwarfEntity dwarf) {
        boolean bl = false;
        ItemStack itemStack = customer.getMainHandItem();
        if (this.customerHeldStack == null || !ItemStack.isSameItem(this.customerHeldStack, itemStack)) {
            this.customerHeldStack = itemStack;
            bl = true;
            this.offers.clear();
        }
        if (bl && !this.customerHeldStack.isEmpty()) {
            this.loadPossibleOffers(dwarf);
            if (!this.offers.isEmpty()) {
                this.ticksLeft = 900;
                this.holdOffer(dwarf);
            }
        }
    }

    private void holdOffer(DwarfEntity dwarf) {
        HoldTradeOffersTaskDwarf.holdOffer(dwarf, this.offers.get(0));
    }

    private void loadPossibleOffers(DwarfEntity dwarf) {
        for (MerchantOffer tradeOffer : dwarf.getOffers()) {
            if (tradeOffer.isOutOfStock() || !this.isPossible(tradeOffer)) continue;
            this.offers.add(tradeOffer.assemble());
        }
    }

    private boolean isPossible(MerchantOffer offer) {
        return ItemStack.isSameItem(this.customerHeldStack, offer.getCostA()) || ItemStack.isSameItem(this.customerHeldStack, offer.getCostB());
    }

    private static void holdNothing(DwarfEntity dwarf) {
            }

    private static void holdOffer(DwarfEntity dwarf, ItemStack stack) {
            }

    private LivingEntity findPotentialCustomer(DwarfEntity dwarf) {
        Brain<DwarfEntity> brain = dwarf.getBrain();
        LivingEntity livingEntity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(livingEntity, true));
        return livingEntity;
    }

    private void refreshShownOffer(DwarfEntity dwarf) {
        if (this.offers.size() >= 2 && ++this.offerShownTicks >= 40) {
            ++this.offerIndex;
            this.offerShownTicks = 0;
            if (this.offerIndex > this.offers.size() - 1) {
                this.offerIndex = 0;
            }
            HoldTradeOffersTaskDwarf.holdOffer(dwarf, this.offers.get(this.offerIndex));
        }
    }

}

