/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestType;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.WHRegistry;

import java.util.Optional;

public class GoToWorkTaskDwarf {
    public static Task<DwarfEntity> create() {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE), context.queryMemoryOptional(MemoryModuleType.JOB_SITE)).apply(context, (potentialJobSite, jobSite) -> (world, entity, time) -> {
            GlobalPos globalPos = (GlobalPos)context.getValue(potentialJobSite);
            if (!globalPos.pos().isWithinDistance(entity.getPos(), 2.0) && !entity.isNatural()) {
                return false;
            }
            potentialJobSite.forget();
            jobSite.remember(globalPos);
            world.sendEntityStatus(entity, EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES);
            if (entity.getProfession() != DwarfProfessionRecord.Warrior) {
                return true;
            }
            MinecraftServer minecraftServer = world.getServer();
            Optional.ofNullable(minecraftServer.getWorld(globalPos.dimension())).flatMap(jobSiteWorld -> jobSiteWorld.getPointOfInterestStorage().getType(globalPos.pos())).flatMap(poiType -> WHRegistry.DWARF_PROFESSIONS.stream().filter(profession -> profession.heldWorkstation().test((RegistryEntry<PointOfInterestType>)poiType)).findFirst()).ifPresent(profession -> {
                entity.setVillagerData(profession,entity.getProfessionLevel());
                entity.reinitializeBrain(world);
            });
            return true;
        }));
    }
}

