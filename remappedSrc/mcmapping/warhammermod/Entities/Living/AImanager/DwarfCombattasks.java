package warhammermod.Entities.Living.AImanager;


import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.mob.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.VillageBoundRandomStroll;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.GameRules;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.StopPanickingTaskDwarf;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.Registry.Entityinit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class DwarfCombattasks {
    public static void updateActivity(DwarfEntity dwarfEntity) {
        Brain<DwarfEntity> brain = dwarfEntity.getBrain();
        dwarfEntity.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

/*
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getAttackPackage(DwarfProfessionRecord p_220636_0_, float p_220636_1_) {
        float f = p_220636_1_ * 1.5F;
        return ImmutableList.of(Pair.of(0, StopPanickingTask.create()),Pair.of(2,MeleeAttackTask.create(20)));
    }
*/
    public static ImmutableList<Pair<Integer,?extends BehaviorControl<? super Mob>>> getAttackpackage(DwarfProfessionRecord profession,DwarfEntity dwarf, float speed){
        return ImmutableList.of(Pair.of(0,StopAttackingIfTargetInvalid.create(target -> isPreferredAttackTarget(dwarf,target))), Pair.of(1,MeleeAttack.create((int)20)));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super DwarfEntity>>> createPanicTasks( float speed) {
        float f = speed * 1.2f;
        return ImmutableList.of(Pair.of(0, StopPanickingTaskDwarf.create()),Pair.of(1,SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(f)),Pair.of(1,MeleeAttack.create(20)),
                Pair.of(3, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_HOSTILE, (float)f, (int)6, (boolean)false)),
                Pair.of(3, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, (float)f, (int)6, (boolean)false)),
                Pair.of(4, VillageBoundRandomStroll.create((float)f, (int)2, (int)2)),
                createBusyFollowTask()
        );

    }

    private static Pair<Integer, BehaviorControl<LivingEntity>> createBusyFollowTask() {
        return Pair.of(5, new RunOne(ImmutableList.of(Pair.of(SetEntityLookTarget.create(Entityinit.DWARF, 8.0f), 2), Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0f), 2), Pair.of(new DoNothing(30, 60), 8))));
    }



    public static void onAttacked(DwarfEntity dwarf, LivingEntity attacker) {
        if (attacker instanceof DwarfEntity) {
            return;
        }
        Brain<DwarfEntity> brain = dwarf.getBrain();
        if (dwarf.isBaby()) {
            brain.setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, attacker, 100L);
            if (Sensor.isEntityAttackableIgnoringLineOfSight((LivingEntity)dwarf, (LivingEntity)attacker)) {
                angerAtCloserTargets(dwarf, attacker);
            }
            return;
        }
        tryRevenge(dwarf, attacker);
    }

    protected static void angerAtCloserTargets(DwarfEntity dwarf, LivingEntity target) {
        getNearbyDwarfs(dwarf).forEach(nearbydwarf -> {
            angerAtIfCloser(dwarf, target);
        });
    }

    private static List<DwarfEntity> getNearbyDwarfs(DwarfEntity dwarf) {
        List<LivingEntity> livinglist = dwarf.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of()).stream().filter(e-> e.getType()==Entityinit.DWARF).toList();
        List<DwarfEntity> dwarflist= new ArrayList<DwarfEntity>();
        livinglist.forEach(c -> {if (c instanceof DwarfEntity dwarf1) dwarflist.add(dwarf1);
        });
        return dwarflist;
    }

    private static void angerAtIfCloser(DwarfEntity piglin, LivingEntity target) {
        Optional<LivingEntity> optional = getAngryAt(piglin);
        LivingEntity livingEntity = BehaviorUtils.getNearestTarget((LivingEntity)piglin, optional, (LivingEntity)target);
        if (optional.isPresent() && optional.get() == livingEntity) {
            return;
        }
        becomeAngryWith(piglin, livingEntity);
    }

    private static Optional<LivingEntity> getAngryAt(DwarfEntity dwarf) {
        return BehaviorUtils.getLivingEntityFromUUIDMemory(dwarf, MemoryModuleType.ANGRY_AT);
    }

    protected static void becomeAngryWith(DwarfEntity dwarf, LivingEntity target) {
        if (!Sensor.isEntityAttackableIgnoringLineOfSight((LivingEntity)dwarf, (LivingEntity)target)) {
            return;
        }
        dwarf.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        dwarf.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, target.getUUID(), 600L);

        if (target.getType() == EntityType.PLAYER && dwarf.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
            dwarf.getBrain().setMemoryWithExpiry(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
        }
    }

    protected static void tryRevenge(DwarfEntity dwarf, LivingEntity target) {
        if (dwarf.getBrain().isActive(Activity.AVOID)) {
            return;
        }
        if (!Sensor.isEntityAttackableIgnoringLineOfSight((LivingEntity)dwarf, (LivingEntity)target)) {
            return;
        }
        if (BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget((LivingEntity)dwarf, (LivingEntity)target, (double)4.0)) {
            return;
        }
        if (target.getType() == EntityType.PLAYER && dwarf.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
            becomeAngryWithPlayer(dwarf, target);
            angerNearbyDwarfs(dwarf);
        } else {
            becomeAngryWith(dwarf, target);
            angerAtCloserTargets(dwarf, target);
        }
    }

    private static void becomeAngryWithPlayer(DwarfEntity piglin, LivingEntity player) {
        Optional<Player> optional = getNearestDetectedPlayer(piglin);
        if (optional.isPresent()) {
            becomeAngryWith(piglin, optional.get());
        } else {
            becomeAngryWith(piglin, player);
        }
    }

    public static Optional<Player> getNearestDetectedPlayer(DwarfEntity dwarf) {
        if (dwarf.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)) {
            return dwarf.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
        }
        return Optional.empty();
    }

    protected static void angerNearbyDwarfs(DwarfEntity dwarf) {
        getNearbyDwarfs(dwarf).forEach(nearbydwarf -> getNearestDetectedPlayer(nearbydwarf).ifPresent(player -> becomeAngryWith(nearbydwarf, (LivingEntity)player)));
    }

    private static boolean isPreferredAttackTarget(DwarfEntity dwarf, LivingEntity target) {
        return getPreferredTarget(dwarf).filter(preferredTarget -> preferredTarget == target).isPresent();
    }


    private static Optional<? extends LivingEntity> getPreferredTarget(DwarfEntity dwarf) {
        Optional optional2;
        Brain brain = dwarf.getBrain();

        Optional optional = BehaviorUtils.getLivingEntityFromUUIDMemory(dwarf, MemoryModuleType.ANGRY_AT);
        if (optional.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight((LivingEntity)dwarf, (LivingEntity)((LivingEntity)optional.get()))) {
            return optional;
        }
        if (brain.hasMemoryValue(MemoryModuleType.UNIVERSAL_ANGER) && (optional2 = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)).isPresent()) {
            return optional2;
        }
        optional2 = brain.getMemory(MemoryModuleType.NEAREST_HOSTILE);
        if (optional2.isPresent()) {
            return optional2;
        }
        return Optional.empty();
    }







// ,Pair.of(1, new TargetTask(MemoryModuleType.NEAREST_HOSTILE)), Pair.of(3, new MoveToTargetTask(f)), Pair.of(3, new AttackTargetTask(20))




}
