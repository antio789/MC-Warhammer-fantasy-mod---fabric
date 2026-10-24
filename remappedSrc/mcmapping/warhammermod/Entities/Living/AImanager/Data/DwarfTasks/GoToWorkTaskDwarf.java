/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.WHRegistry;

import java.util.Optional;

public class GoToWorkTaskDwarf {
    public static BehaviorControl<DwarfEntity> create() {
        return BehaviorBuilder.create(context -> context.group(context.present(MemoryModuleType.POTENTIAL_JOB_SITE), context.registered(MemoryModuleType.JOB_SITE)).apply(context, (potentialJobSite, jobSite) -> (world, entity, time) -> {
            GlobalPos globalPos = (GlobalPos)context.get(potentialJobSite);
            if (!globalPos.pos().closerToCenterThan(entity.position(), 2.0) && !entity.isNatural()) {
                return false;
            }
            potentialJobSite.erase();
            jobSite.set(globalPos);
            world.broadcastEntityEvent(entity, EntityEvent.VILLAGER_HAPPY);
            if (entity.getProfession() != DwarfProfessionRecord.Warrior) {
                return true;
            }
            MinecraftServer minecraftServer = world.getServer();
            Optional.ofNullable(minecraftServer.getLevel(globalPos.dimension())).flatMap(jobSiteWorld -> jobSiteWorld.getPoiManager().getType(globalPos.pos())).flatMap(poiType -> WHRegistry.DWARF_PROFESSIONS.stream().filter(profession -> profession.heldWorkstation().test((Holder<PoiType>)poiType)).findFirst()).ifPresent(profession -> {
                entity.setVillagerData(profession,entity.getProfessionLevel());
                entity.reinitializeBrain(world);
            });
            return true;
        }));
    }
}

