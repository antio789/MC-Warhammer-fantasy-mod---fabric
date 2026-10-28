package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.Nullable;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.List;

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
    protected boolean checkExtraStartConditions(ServerLevel serverWorld, DwarfEntity dwarf) {
        var brain = dwarf.getBrain();
        if (brain.getMemory(MemoryModuleType.INTERACTION_TARGET).isEmpty()) {
            return false;
        }
        LivingEntity livingEntity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        return livingEntity.getType() == EntityTypes.PLAYER && dwarf.isAlive() && livingEntity.isAlive() && !dwarf.isBaby() && dwarf.distanceToSqr(livingEntity) <= 17.0;
    }

    @Override
    protected boolean canStillUse(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        return this.checkExtraStartConditions(serverWorld, dwarf) && this.ticksLeft > 0 && dwarf.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    @Override
    protected void start(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        super.start(serverWorld, dwarf, l);
        this.findPotentialCustomer(dwarf);
        this.offerShownTicks = 0;
        this.offerIndex = 0;
        this.ticksLeft = 40;
    }

    @Override
    protected void tick(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
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
    protected void stop(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        super.stop(serverWorld, dwarf, l);
        dwarf.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        HoldTradeOffersTaskDwarf.holdNothing(dwarf);
        this.customerHeldStack = null;
    }

    private void setupOffers(LivingEntity customer, DwarfEntity dwarf) {
        ItemStack itemStack = customer.getMainHandItem();
        if (!ItemStack.isSameItemSameComponents(itemStack, this.customerHeldStack)) {
            this.customerHeldStack = itemStack.copy();
            this.offers.clear();
            if (!itemStack.isEmpty()) {
                this.loadPossibleOffers(dwarf);
                if (!this.offers.isEmpty()) {
                    this.ticksLeft = 900;
                    this.holdOffer(dwarf);
                }
            }
        }
    }

    private void holdOffer(DwarfEntity dwarf) {
        HoldTradeOffersTaskDwarf.holdOffer(dwarf, this.offers.get(0));
    }

    private void loadPossibleOffers(DwarfEntity dwarf) {
        for (MerchantOffer tradeOffer : dwarf.getOffers()) {
            if (tradeOffer.isOutOfStock() || !this.isPossible(tradeOffer)) continue;
            this.offers.add(tradeOffer.getResult());
        }
    }

    private boolean isPossible(MerchantOffer offer) {
        boolean matchesA = ItemStack.isSameItem(this.customerHeldStack, offer.getCostA());
        boolean matchesB = ItemStack.isSameItem(this.customerHeldStack, offer.getCostB());
        return matchesA || matchesB;
    }

    private static void holdNothing(DwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
    }

    private static void holdOffer(DwarfEntity dwarf, ItemStack stack) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, stack.copy());
    }

    private LivingEntity findPotentialCustomer(DwarfEntity dwarf) {
        var brain = dwarf.getBrain();
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
