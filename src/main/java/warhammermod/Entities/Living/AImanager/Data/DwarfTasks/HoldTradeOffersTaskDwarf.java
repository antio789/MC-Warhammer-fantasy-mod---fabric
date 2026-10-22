/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import org.jetbrains.annotations.Nullable;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.List;

public class HoldTradeOffersTaskDwarf extends MultiTickTask<DwarfEntity> {
    private static final int RUN_INTERVAL = 900;
    private static final int OFFER_SHOWING_INTERVAL = 40;
    @Nullable
    private ItemStack customerHeldStack;
    private final List<ItemStack> offers = Lists.newArrayList();
    private int offerShownTicks;
    private int offerIndex;
    private int ticksLeft;

    public HoldTradeOffersTaskDwarf(int minRunTime, int maxRunTime) {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleState.VALUE_PRESENT), minRunTime, maxRunTime);
    }

    @Override
    public boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarf) {
        Brain<DwarfEntity> brain = dwarf.getBrain();
        if (brain.getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).isEmpty()) {
            return false;
        }
        LivingEntity livingEntity = brain.getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).get();
        return livingEntity.getType() == EntityType.PLAYER && dwarf.isAlive() && livingEntity.isAlive() && !dwarf.isBaby() && dwarf.squaredDistanceTo(livingEntity) <= 17.0;
    }

    @Override
    public boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarf, long l) {
        return this.shouldRun(serverWorld, dwarf) && this.ticksLeft > 0 && dwarf.getBrain().getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    @Override
    public void run(ServerWorld serverWorld, DwarfEntity dwarf, long l) {
        super.run(serverWorld, dwarf, l);
        this.findPotentialCustomer(dwarf);
        this.offerShownTicks = 0;
        this.offerIndex = 0;
        this.ticksLeft = 40;
    }

    @Override
    public void keepRunning(ServerWorld serverWorld, DwarfEntity dwarf, long l) {
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
    public void finishRunning(ServerWorld serverWorld, DwarfEntity dwarf, long l) {
        super.finishRunning(serverWorld, dwarf, l);
        dwarf.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
        HoldTradeOffersTaskDwarf.holdNothing(dwarf);
        this.customerHeldStack = null;
    }

    private void setupOffers(LivingEntity customer, DwarfEntity dwarf) {
        boolean bl = false;
        ItemStack itemStack = customer.getMainHandStack();
        if (this.customerHeldStack == null || !ItemStack.areItemsEqual(this.customerHeldStack, itemStack)) {
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
        for (TradeOffer tradeOffer : dwarf.getOffers()) {
            if (tradeOffer.isDisabled() || !this.isPossible(tradeOffer)) continue;
            this.offers.add(tradeOffer.copySellItem());
        }
    }

    private boolean isPossible(TradeOffer offer) {
        return ItemStack.areItemsEqual(this.customerHeldStack, offer.getDisplayedFirstBuyItem()) || ItemStack.areItemsEqual(this.customerHeldStack, offer.getDisplayedSecondBuyItem());
    }

    private static void holdNothing(DwarfEntity dwarf) {
            }

    private static void holdOffer(DwarfEntity dwarf, ItemStack stack) {
            }

    private LivingEntity findPotentialCustomer(DwarfEntity dwarf) {
        Brain<DwarfEntity> brain = dwarf.getBrain();
        LivingEntity livingEntity = brain.getOptionalRegisteredMemory(MemoryModuleType.INTERACTION_TARGET).get();
        brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(livingEntity, true));
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

