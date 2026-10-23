package warhammermod.Entities.Living.AImanager;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRules;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.GoToPointOfInterestTaskDwarf;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.StopPanickingTaskDwarf;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.Entityinit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DwarfCombattasks {
    public static void updateActivity(DwarfEntity dwarfEntity) {
        var brain = dwarfEntity.getBrain();
        dwarfEntity.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createPanicTasks(float speed) {
        float f = speed * 1.2f;
        return ImmutableList.of(Pair.of(0, StopPanickingTaskDwarf.create()), Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_HOSTILE, f, 6, false)),
                Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, f, 6, false)),
                Pair.of(1, MeleeAttack.create(20)),
                Pair.of(3, GoToPointOfInterestTaskDwarf.create(f, 2)),
                createBusyFollowTask());
    }

    private static Pair<Integer, BehaviorControl<LivingEntity>> createBusyFollowTask() {
        return Pair.of(5, new RunOne<>(ImmutableList.of(
                Pair.of(SetEntityLookTarget.create(Entityinit.DWARF, 8.0f), 2),
                Pair.of(SetEntityLookTarget.create(EntityTypes.PLAYER, 8.0f), 2),
                Pair.of(new DoNothing(30, 60), 8))));
    }

    public static void onAttacked(ServerLevel server, DwarfEntity dwarf, LivingEntity attacker) {
        if (attacker instanceof DwarfEntity) {
            return;
        }
        var brain = dwarf.getBrain();
        if (dwarf.isBaby()) {
            brain.setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, attacker, 100L);
            if (Sensor.isEntityTargetable(server, dwarf, attacker)) {
                angerAtCloserTargets(server, dwarf, attacker);
            }
            return;
        }
        tryRevenge(server, dwarf, attacker);
    }

    protected static void angerAtCloserTargets(ServerLevel server, DwarfEntity dwarf, LivingEntity target) {
        getNearbyDwarfs(dwarf).forEach(nearbydwarf -> angerAtIfCloser(server, dwarf, target));
    }

    private static List<DwarfEntity> getNearbyDwarfs(DwarfEntity dwarf) {
        List<LivingEntity> livinglist = dwarf.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .map(NearestVisible -> List.copyOf(NearestVisible.nearbyEntities()))
                .orElse(ImmutableList.of()).stream().filter(e -> e.getType() == Entityinit.DWARF).toList();
        List<DwarfEntity> dwarflist = new ArrayList<>();
        livinglist.forEach(c -> {
            if (c instanceof DwarfEntity dwarf1) dwarflist.add(dwarf1);
        });
        return dwarflist;
    }

    private static void angerAtIfCloser(ServerLevel server, DwarfEntity dwarf, LivingEntity target) {
        Optional<LivingEntity> optional = getAngryAt(dwarf);
        LivingEntity livingEntity = getCloserEntity(dwarf, optional, target);
        if (optional.isPresent() && optional.get() == livingEntity) {
            return;
        }
        becomeAngryWith(server, dwarf, livingEntity);
    }

    private static Optional<LivingEntity> getAngryAt(DwarfEntity dwarf) {
        return dwarf.getBrain().getMemory(MemoryModuleType.ANGRY_AT).flatMap(uuid -> {
            var level = dwarf.level();
            var entity = ((ServerLevel) level).getEntity(uuid);
            return entity instanceof LivingEntity living ? Optional.of(living) : Optional.empty();
        });
    }

    private static LivingEntity getCloserEntity(LivingEntity dwarf, Optional<LivingEntity> first, LivingEntity target) {
        if (first.isEmpty()) {
            return target;
        }
        return dwarf.distanceToSqr(first.get()) <= dwarf.distanceToSqr(target) ? first.get() : target;
    }

    protected static void becomeAngryWith(ServerLevel server, DwarfEntity dwarf, LivingEntity target) {
        if (!Sensor.isEntityTargetable(server, dwarf, target)) {
            return;
        }
        dwarf.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        dwarf.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, target.getUUID(), 600L);

        if (target.getType() == EntityTypes.PLAYER && server.getGameRules().get(GameRules.UNIVERSAL_ANGER)) {
            dwarf.getBrain().setMemoryWithExpiry(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
        }
        dwarf.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
    }

    protected static void tryRevenge(ServerLevel server, DwarfEntity dwarf, LivingEntity target) {
        if (dwarf.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET)) {
            return;
        }
        if (!Sensor.isEntityTargetable(server, dwarf, target)) {
            return;
        }
        if (isNewTargetTooFar(dwarf, target, 4.0)) {
            return;
        }
        if (target.getType() == EntityTypes.PLAYER && server.getGameRules().get(GameRules.UNIVERSAL_ANGER)) {
            becomeAngryWithPlayer(server, dwarf, target);
            angerNearbyDwarfs(server, dwarf);
        } else {
            becomeAngryWith(server, dwarf, target);
            angerAtCloserTargets(server, dwarf, target);
        }
    }

    private static void becomeAngryWithPlayer(ServerLevel server, DwarfEntity dwarf, LivingEntity player) {
        Optional<Player> optional = getNearestDetectedPlayer(dwarf);
        if (optional.isPresent()) {
            becomeAngryWith(server, dwarf, optional.get());
        } else {
            becomeAngryWith(server, dwarf, player);
        }
    }

    public static Optional<Player> getNearestDetectedPlayer(DwarfEntity dwarf) {
        if (dwarf.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD)) {
            return dwarf.getBrain().getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD).map(p -> (Player) p);
        }
        return Optional.empty();
    }

    protected static void angerNearbyDwarfs(ServerLevel server, DwarfEntity dwarf) {
        getNearbyDwarfs(dwarf).forEach(nearbydwarf -> getNearestDetectedPlayer(nearbydwarf).ifPresent(player -> becomeAngryWith(server, nearbydwarf, player)));
    }

    private static Optional<LivingEntity> getPreferredTarget(ServerLevel server, DwarfEntity dwarf) {
        var brain = dwarf.getBrain();

        Optional<LivingEntity> optional = getAngryAt(dwarf);
        if (optional.isPresent() && Sensor.isEntityTargetable(server, dwarf, optional.get())) {
            return optional;
        }
        if (brain.hasMemoryValue(MemoryModuleType.UNIVERSAL_ANGER)) {
            var player = brain.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
            if (player.isPresent()) {
                return player.map(p -> (LivingEntity) p);
            }
        }
        var hostile = brain.getMemory(MemoryModuleType.NEAREST_HOSTILE);
        if (hostile.isPresent()) {
            return hostile;
        }
        return Optional.empty();
    }

    public static boolean isNewTargetTooFar(LivingEntity dwarf, LivingEntity target, double distance) {
        Optional<LivingEntity> optional = getAngryAt((DwarfEntity) dwarf);
        return optional.isEmpty() || dwarf.distanceToSqr(optional.get()) > dwarf.distanceToSqr(target) + distance * distance;
    }
}
