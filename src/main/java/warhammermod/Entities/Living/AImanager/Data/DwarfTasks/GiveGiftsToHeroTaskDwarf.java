package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.WHRegistry;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GiveGiftsToHeroTaskDwarf extends Behavior<DwarfEntity> {
    private static final int MAX_DISTANCE = 5;
    private static final int DEFAULT_DURATION = 600;
    private static final int RUN_TIME = 20;

    private static final Map<DwarfProfessionRecord, ResourceKey<LootTable>> GIFTS = Util.make(Maps.newHashMap(), gifts -> {
        gifts.put(DwarfProfessionRecord.Lord, WHRegistry.HERO_OF_THE_VILLAGE_LORD_GIFT_GAMEPLAY);
        gifts.put(DwarfProfessionRecord.Miner, BuiltInLootTables.BUTCHER_GIFT);
        gifts.put(DwarfProfessionRecord.FARMER, BuiltInLootTables.FARMER_GIFT);
        gifts.put(DwarfProfessionRecord.Slayer, BuiltInLootTables.BUTCHER_GIFT);
        gifts.put(DwarfProfessionRecord.Engineer, BuiltInLootTables.TOOLSMITH_GIFT);
        gifts.put(DwarfProfessionRecord.Builder, BuiltInLootTables.MASON_GIFT);
    });

    private static final float WALK_SPEED = 0.5f;
    private int ticksLeft = 600;
    private boolean done;
    private long startTime;

    public GiveGiftsToHeroTaskDwarf(int delay) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT), delay);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverWorld, DwarfEntity dwarf) {
        if (!this.isNearestPlayerHero(dwarf)) {
            return false;
        }
        if (this.ticksLeft > 0) {
            --this.ticksLeft;
            return false;
        }
        return true;
    }

    @Override
    protected void start(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        this.done = false;
        this.startTime = l;
        Player playerEntity = this.getNearestPlayerIfHero(dwarf).get();
        dwarf.getBrain().setMemory(MemoryModuleType.INTERACTION_TARGET, playerEntity);
        BehaviorUtils.lookAtEntity(dwarf, playerEntity);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        return this.isNearestPlayerHero(dwarf) && !this.done;
    }

    @Override
    protected void tick(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        Player playerEntity = this.getNearestPlayerIfHero(dwarf).get();
        BehaviorUtils.lookAtEntity(dwarf, playerEntity);
        if (this.isCloseEnough(dwarf, playerEntity)) {
            if (l - this.startTime > 20L) {
                this.giveGifts(dwarf, playerEntity);
                this.done = true;
            }
        } else {
            BehaviorUtils.setWalkAndLookTargetMemories(dwarf, playerEntity, 0.5f, 5);
        }
    }

    @Override
    protected void stop(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        this.ticksLeft = GiveGiftsToHeroTaskDwarf.getNextGiftDelay(serverWorld);
        dwarf.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        dwarf.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        dwarf.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
    }

    private void giveGifts(DwarfEntity dwarf, LivingEntity recipient) {
        List<ItemStack> list = this.getGifts(dwarf);
        for (ItemStack itemStack : list) {
            BehaviorUtils.throwItem(dwarf, itemStack, recipient.position());
        }
    }

    private List<ItemStack> getGifts(DwarfEntity dwarf) {
        if (dwarf.isBaby()) {
            return ImmutableList.of(new ItemStack(Items.POPPY));
        }
        DwarfProfessionRecord villagerProfession = dwarf.getProfession();
        if (GIFTS.containsKey(villagerProfession)) {
            LootTable lootTable = dwarf.level().getServer().reloadableRegistries().getLootTable(GIFTS.get(villagerProfession));
            LootParams lootParams = new LootParams.Builder((ServerLevel) dwarf.level())
                    .withParameter(LootContextParams.ORIGIN, dwarf.position())
                    .withParameter(LootContextParams.THIS_ENTITY, dwarf)
                    .create(LootContextParamSets.GIFT);
            return lootTable.getRandomItems(lootParams, dwarf.getRandom());
        }
        return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
    }

    private boolean isNearestPlayerHero(DwarfEntity dwarf) {
        return this.getNearestPlayerIfHero(dwarf).isPresent();
    }

    private Optional<Player> getNearestPlayerIfHero(DwarfEntity dwarf) {
        return dwarf.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
    }

    private boolean isHero(Player player) {
        return player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE);
    }

    private boolean isCloseEnough(DwarfEntity dwarf, Player player) {
        BlockPos blockPos = player.blockPosition();
        BlockPos blockPos2 = dwarf.blockPosition();
        return blockPos2.closerThan(blockPos, 5.0);
    }

    private static int getNextGiftDelay(ServerLevel world) {
        return 600 + world.getRandom().nextInt(6001);
    }
}
