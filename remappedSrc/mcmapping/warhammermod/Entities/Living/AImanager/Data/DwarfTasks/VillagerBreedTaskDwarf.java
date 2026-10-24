/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.pathfinder.Path;

public class VillagerBreedTaskDwarf
extends Behavior<DwarfEntity> {
    private long breedEndTime;

    public VillagerBreedTaskDwarf() {
        super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT), 350, 350);
    }

    @Override
    protected boolean shouldRun(ServerLevel serverWorld, DwarfEntity dwarfEntity) {
        return this.isReadyToBreed(dwarfEntity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        return l <= this.breedEndTime && this.isReadyToBreed(dwarfEntity);
    }

    @Override
    protected void run(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        AgeableMob passiveEntity = dwarfEntity.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
        BehaviorUtils.lockGazeAndWalkToEachOther(dwarfEntity, passiveEntity, 0.5f, 2);
        serverWorld.broadcastEntityEvent(passiveEntity, EntityEvent.IN_LOVE_HEARTS);
        serverWorld.broadcastEntityEvent(dwarfEntity, EntityEvent.IN_LOVE_HEARTS);
        int i = 275 + dwarfEntity.getRandom().nextInt(50);
        this.breedEndTime = l + (long)i;
    }

    @Override
    protected void keepRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        DwarfEntity dwarfEntity2 = (DwarfEntity)dwarfEntity.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
        if (dwarfEntity.distanceToSqr(dwarfEntity2) > 5.0) {
            return;
        }
        BehaviorUtils.lockGazeAndWalkToEachOther(dwarfEntity, dwarfEntity2, 0.5f, 2);
        if (l >= this.breedEndTime) {
            dwarfEntity.eatForBreeding();
            dwarfEntity2.eatForBreeding();
            this.goHome(serverWorld, dwarfEntity, dwarfEntity2);
        } else if (dwarfEntity.getRandom().nextInt(35) == 0) {
            serverWorld.broadcastEntityEvent(dwarfEntity2, EntityEvent.LOVE_HEARTS);
            serverWorld.broadcastEntityEvent(dwarfEntity, EntityEvent.LOVE_HEARTS);
        }
    }

    private void goHome(ServerLevel world, DwarfEntity first, DwarfEntity second) {
        Optional<BlockPos> optional = this.getReachableHome(world, first);
        if (optional.isEmpty()) {
            world.broadcastEntityEvent(second, EntityEvent.VILLAGER_ANGRY);
            world.broadcastEntityEvent(first, EntityEvent.VILLAGER_ANGRY);
        } else {
            Optional<DwarfEntity> optional2 = this.createChild(world, first, second);
            if (optional2.isPresent()) {
                this.setChildHome(world, optional2.get(), optional.get());
            } else {
                world.getPoiManager().release(optional.get());
                DebugPackets.sendPoiTicketCountPacket(world, optional.get());
            }
        }
    }

    @Override
    protected void finishRunning(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
    }

    private boolean isReadyToBreed(DwarfEntity villager) {
        Brain<DwarfEntity> brain = villager.getBrain();
        Optional<AgeableMob> optional = brain.getMemory(MemoryModuleType.BREED_TARGET).filter(passiveEntity -> passiveEntity.getType() == Entityinit.DWARF);
        return optional.filter(passiveEntity -> BehaviorUtils.targetIsValid(brain, MemoryModuleType.BREED_TARGET, Entityinit.DWARF) && villager.canBreed() && passiveEntity.canBreed()).isPresent();
    }

    private Optional<BlockPos> getReachableHome(ServerLevel world, DwarfEntity villager) {
        return world.getPoiManager().take(poiType -> poiType.is(PoiTypes.HOME), (poiType, pos) -> this.canReachHome(villager, (BlockPos)pos, (Holder<PoiType>)poiType), villager.blockPosition(), 48);
    }

    private boolean canReachHome(DwarfEntity villager, BlockPos pos, Holder<PoiType> poiType) {
        Path path = villager.getNavigation().createPath(pos, poiType.value().validRange());
        return path != null && path.canReach();
    }

    private Optional<DwarfEntity> createChild(ServerLevel world, DwarfEntity parent, DwarfEntity partner) {
        DwarfEntity dwarfEntity = parent.getBreedOffspring(world, partner);
        if (dwarfEntity == null) {
            return Optional.empty();
        }
        parent.setAge(6000);
        partner.setAge(6000);
        dwarfEntity.setAge(-24000);
        dwarfEntity.moveTo(parent.getX(), parent.getY(), parent.getZ(), 0.0f, 0.0f);
        world.addFreshEntityWithPassengers(dwarfEntity);
        world.broadcastEntityEvent(dwarfEntity, EntityEvent.LOVE_HEARTS);
        return Optional.of(dwarfEntity);
    }

    private void setChildHome(ServerLevel world, DwarfEntity child, BlockPos pos) {
        GlobalPos globalPos = GlobalPos.of(world.dimension(), pos);
        child.getBrain().setMemory(MemoryModuleType.HOME, globalPos);
    }

}

