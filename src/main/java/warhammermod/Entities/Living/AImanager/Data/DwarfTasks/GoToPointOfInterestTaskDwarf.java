package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.phys.Vec3;
import warhammermod.Entities.Living.DwarfEntity;

public class GoToPointOfInterestTaskDwarf {
    public static BehaviorControl<DwarfEntity> create(float speed, int completionRange) {
        return BehaviorBuilder.create(context -> context.group(
                        context.absent(MemoryModuleType.WALK_TARGET))
                .apply(context, walkTarget -> (world, entity, time) -> {
                    if (world.isCloseToVillage(entity.blockPosition(), 4)) {
                        return false;
                    }
                    PoiManager pointOfInterestStorage = world.getPoiManager();
                    int j = pointOfInterestStorage.sectionsToVillage(SectionPos.of(entity.blockPosition()));
                    Vec3 vec3d = null;
                    for (int k = 0; k < 5; ++k) {
                        Vec3 vec3d2 = DefaultRandomPos.getPosAway(entity, 15, 7, Vec3.atBottomCenterOf(entity.blockPosition()));
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
