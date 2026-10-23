package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.WHRegistry;

public class GoToWorkTaskDwarf {
    public static BehaviorControl<DwarfEntity> create() {
        return BehaviorBuilder.create(context -> context.group(
                        context.present(MemoryModuleType.POTENTIAL_JOB_SITE),
                        context.registered(MemoryModuleType.JOB_SITE))
                .apply(context, (potentialJobSite, jobSite) -> (world, entity, time) -> {
                    GlobalPos globalPos = context.get(potentialJobSite);
                    if (!globalPos.pos().closerThan(entity.blockPosition(), 2.0) && !entity.isNatural()) {
                        return false;
                    }
                    potentialJobSite.erase();
                    jobSite.set(globalPos);
                    world.broadcastEntityEvent(entity, (byte) 14);
                    if (entity.getProfession() != DwarfProfessionRecord.Warrior) {
                        return true;
                    }
                    MinecraftServer minecraftServer = world.getServer();
                    ServerLevel jobSiteWorld = minecraftServer.getLevel(globalPos.dimension());
                    if (jobSiteWorld != null) {
                        jobSiteWorld.getPoiManager().getType(globalPos.pos()).flatMap(poiType -> WHRegistry.DWARF_PROFESSIONS.stream().filter(profession -> profession.heldWorkstation().test((Holder<PoiType>) poiType)).findFirst()).ifPresent(profession -> {
                            entity.setVillagerData(profession, entity.getProfessionLevel());
                            entity.refreshBrain(world);
                        });
                    }
                    return true;
                }));
    }
}
