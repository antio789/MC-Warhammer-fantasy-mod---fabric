/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;

public class LoseJobOnSiteLossTaskDwarf {
    static String norepeat="";
    public static BehaviorControl<DwarfEntity> create() {
        return BehaviorBuilder.create(context -> context.group(context.absent(MemoryModuleType.JOB_SITE)).apply(context, jobSite -> (world, entity, time) -> {
            if (entity.getProfession() != DwarfProfessionRecord.Warrior && entity.getProfession() != DwarfProfessionRecord.Lord && entity.getVillagerXp() == 0 && entity.getProfessionLevel() <= 1) {
                entity.setProfession(DwarfProfessionRecord.Warrior,"losejobonsiteloss");
                entity.reinitializeBrain(world);
                return true;
            }
            return false;
        }));
    }
}

