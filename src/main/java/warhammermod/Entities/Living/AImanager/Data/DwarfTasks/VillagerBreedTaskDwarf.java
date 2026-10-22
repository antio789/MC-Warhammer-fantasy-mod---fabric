/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.TargetUtil;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.Optional;

public class VillagerBreedTaskDwarf
extends MultiTickTask<DwarfEntity> {
    private long breedEndTime;

    public VillagerBreedTaskDwarf() {
        super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT), 350, 350);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarfEntity) {
        return this.isReadyToBreed(dwarfEntity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        return l <= this.breedEndTime && this.isReadyToBreed(dwarfEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        PassiveEntity passiveEntity = dwarfEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.BREED_TARGET).get();
        TargetUtil.lookAtAndWalkTowardsEachOther(dwarfEntity, passiveEntity, 0.5f, 2);
        serverWorld.sendEntityStatus(passiveEntity, EntityStatuses.ADD_BREEDING_PARTICLES);
        serverWorld.sendEntityStatus(dwarfEntity, EntityStatuses.ADD_BREEDING_PARTICLES);
        int i = 275 + dwarfEntity.getRandom().nextInt(50);
        this.breedEndTime = l + (long)i;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        DwarfEntity dwarfEntity2 = (DwarfEntity)dwarfEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.BREED_TARGET).get();
        if (dwarfEntity.squaredDistanceTo(dwarfEntity2) > 5.0) {
            return;
        }
        TargetUtil.lookAtAndWalkTowardsEachOther(dwarfEntity, dwarfEntity2, 0.5f, 2);
        if (l >= this.breedEndTime) {
            dwarfEntity.eatForBreeding();
            dwarfEntity2.eatForBreeding();
            this.goHome(serverWorld, dwarfEntity, dwarfEntity2);
        } else if (dwarfEntity.getRandom().nextInt(35) == 0) {
            serverWorld.sendEntityStatus(dwarfEntity2, EntityStatuses.ADD_VILLAGER_HEART_PARTICLES);
            serverWorld.sendEntityStatus(dwarfEntity, EntityStatuses.ADD_VILLAGER_HEART_PARTICLES);
        }
    }

    private void goHome(ServerWorld world, DwarfEntity first, DwarfEntity second) {
        Optional<BlockPos> optional = this.getReachableHome(world, first);
        if (optional.isEmpty()) {
            world.sendEntityStatus(second, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES);
            world.sendEntityStatus(first, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES);
        } else {
            Optional<DwarfEntity> optional2 = this.createChild(world, first, second);
            if (optional2.isPresent()) {
                this.setChildHome(world, optional2.get(), optional.get());
            } else {
                world.getPointOfInterestStorage().releaseTicket(optional.get());
                DebugInfoSender.sendPointOfInterest(world, optional.get());
            }
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        dwarfEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
    }

    private boolean isReadyToBreed(DwarfEntity villager) {
        Brain<DwarfEntity> brain = villager.getBrain();
        Optional<PassiveEntity> optional = brain.getOptionalRegisteredMemory(MemoryModuleType.BREED_TARGET).filter(passiveEntity -> passiveEntity.getType() == Entityinit.DWARF);
        return optional.filter(passiveEntity -> TargetUtil.canSee(brain, MemoryModuleType.BREED_TARGET, Entityinit.DWARF) && villager.isReadyToBreed() && passiveEntity.isReadyToBreed()).isPresent();
    }

    private Optional<BlockPos> getReachableHome(ServerWorld world, DwarfEntity villager) {
        return world.getPointOfInterestStorage().getPosition(poiType -> poiType.matchesKey(PointOfInterestTypes.HOME), (poiType, pos) -> this.canReachHome(villager, (BlockPos)pos, (RegistryEntry<PointOfInterestType>)poiType), villager.getBlockPos(), 48);
    }

    private boolean canReachHome(DwarfEntity villager, BlockPos pos, RegistryEntry<PointOfInterestType> poiType) {
        Path path = villager.getNavigation().findPathTo(pos, poiType.value().searchDistance());
        return path != null && path.reachesTarget();
    }

    private Optional<DwarfEntity> createChild(ServerWorld world, DwarfEntity parent, DwarfEntity partner) {
        DwarfEntity dwarfEntity = parent.createChild(world, partner);
        if (dwarfEntity == null) {
            return Optional.empty();
        }
        parent.setBreedingAge(6000);
        partner.setBreedingAge(6000);
        dwarfEntity.setBreedingAge(-24000);
        dwarfEntity.refreshPositionAndAngles(parent.getX(), parent.getY(), parent.getZ(), 0.0f, 0.0f);
        world.spawnEntityAndPassengers(dwarfEntity);
        world.sendEntityStatus(dwarfEntity, EntityStatuses.ADD_VILLAGER_HEART_PARTICLES);
        return Optional.of(dwarfEntity);
    }

    private void setChildHome(ServerWorld world, DwarfEntity child, BlockPos pos) {
        GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), pos);
        child.getBrain().remember(MemoryModuleType.HOME, globalPos);
    }

}

