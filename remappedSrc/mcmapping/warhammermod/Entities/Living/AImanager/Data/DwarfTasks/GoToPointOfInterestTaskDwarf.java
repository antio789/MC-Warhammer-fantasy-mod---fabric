/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.phys.Vec3;
import warhammermod.Entities.Living.DwarfEntity;

public class GoToPointOfInterestTaskDwarf {
    public static BehaviorControl<DwarfEntity> create(float speed, int completionRange) {
        return BehaviorBuilder.create(context -> context.group(context.absent(MemoryModuleType.WALK_TARGET)).apply(context, walkTarget -> (world, entity, time) -> {
            if (world.isVillage(entity.blockPosition())) {
                return false;
            }
            PoiManager pointOfInterestStorage = world.getPoiManager();
            int j = pointOfInterestStorage.sectionsToVillage(SectionPos.of(entity.blockPosition()));
            Vec3 vec3d = null;
            for (int k = 0; k < 5; ++k) {
                Vec3 vec3d2 = LandRandomPos.getPos(entity, 15, 7, pos -> -pointOfInterestStorage.sectionsToVillage(SectionPos.of(pos)));
                if (vec3d2 == null) continue;
                int l = pointOfInterestStorage.sectionsToVillage(SectionPos.of(BlockPos.containing(vec3d2)));
                if (l < j) {
                    vec3d = vec3d2;
                    break;
                }
                if (l != j) continue;
                vec3d = vec3d2;
            }
            if (vec3d != null) {
                walkTarget.set(new WalkTarget(vec3d, speed, completionRange));
            }
            return true;
        }));
    }
}

