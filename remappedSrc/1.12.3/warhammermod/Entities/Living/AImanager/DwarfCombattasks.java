package warhammermod.Entities.Living.AImanager;


import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
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
        dwarfEntity.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

/*
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getAttackPackage(DwarfProfessionRecord p_220636_0_, float p_220636_1_) {
        float f = p_220636_1_ * 1.5F;
        return ImmutableList.of(Pair.of(0, StopPanickingTask.create()),Pair.of(2,MeleeAttackTask.create(20)));
    }
*/
    public static ImmutableList<Pair<Integer,?extends Task<? super MobEntity>>> getAttackpackage(DwarfProfessionRecord profession,DwarfEntity dwarf, float speed){
        return ImmutableList.of(Pair.of(0,ForgetAttackTargetTask.create(target -> isPreferredAttackTarget(dwarf,target))), Pair.of(1,MeleeAttackTask.create((int)20)));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super DwarfEntity>>> createPanicTasks( float speed) {
        float f = speed * 1.2f;
        return ImmutableList.of(Pair.of(0, StopPanickingTaskDwarf.create()),Pair.of(1,RangedApproachTask.create(f)),Pair.of(1,MeleeAttackTask.create(20)),
                Pair.of(3, GoToRememberedPositionTask.createEntityBased(MemoryModuleType.NEAREST_HOSTILE, (float)f, (int)6, (boolean)false)),
                Pair.of(3, GoToRememberedPositionTask.createEntityBased(MemoryModuleType.HURT_BY_ENTITY, (float)f, (int)6, (boolean)false)),
                Pair.of(4, GoToPointOfInterestTask.create((float)f, (int)2, (int)2)),
                createBusyFollowTask()
        );

    }

    private static Pair<Integer, Task<LivingEntity>> createBusyFollowTask() {
        return Pair.of(5, new RandomTask(ImmutableList.of(Pair.of(LookAtMobTask.create(Entityinit.DWARF, 8.0f), 2), Pair.of(LookAtMobTask.create(EntityType.PLAYER, 8.0f), 2), Pair.of(new WaitTask(30, 60), 8))));
    }



    public static void onAttacked(DwarfEntity dwarf, LivingEntity attacker) {
        if (attacker instanceof DwarfEntity) {
            return;
        }
        Brain<DwarfEntity> brain = dwarf.getBrain();
        if (dwarf.isBaby()) {
            brain.remember(MemoryModuleType.AVOID_TARGET, attacker, 100L);
            if (Sensor.testAttackableTargetPredicateIgnoreVisibility((LivingEntity)dwarf, (LivingEntity)attacker)) {
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
        List<LivingEntity> livinglist = dwarf.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS).orElse(ImmutableList.of()).stream().filter(e-> e.getType()==Entityinit.DWARF).toList();
        List<DwarfEntity> dwarflist= new ArrayList<DwarfEntity>();
        livinglist.forEach(c -> {if (c instanceof DwarfEntity dwarf1) dwarflist.add(dwarf1);
        });
        return dwarflist;
    }

    private static void angerAtIfCloser(DwarfEntity piglin, LivingEntity target) {
        Optional<LivingEntity> optional = getAngryAt(piglin);
        LivingEntity livingEntity = TargetUtil.getCloserEntity((LivingEntity)piglin, optional, (LivingEntity)target);
        if (optional.isPresent() && optional.get() == livingEntity) {
            return;
        }
        becomeAngryWith(piglin, livingEntity);
    }

    private static Optional<LivingEntity> getAngryAt(DwarfEntity dwarf) {
        return TargetUtil.getEntity(dwarf, MemoryModuleType.ANGRY_AT);
    }

    protected static void becomeAngryWith(DwarfEntity dwarf, LivingEntity target) {
        if (!Sensor.testAttackableTargetPredicateIgnoreVisibility((LivingEntity)dwarf, (LivingEntity)target)) {
            return;
        }
        dwarf.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        dwarf.getBrain().remember(MemoryModuleType.ANGRY_AT, target.getUuid(), 600L);

        if (target.getType() == EntityType.PLAYER && dwarf.getWorld().getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
            dwarf.getBrain().remember(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
        }
    }

    protected static void tryRevenge(DwarfEntity dwarf, LivingEntity target) {
        if (dwarf.getBrain().hasActivity(Activity.AVOID)) {
            return;
        }
        if (!Sensor.testAttackableTargetPredicateIgnoreVisibility((LivingEntity)dwarf, (LivingEntity)target)) {
            return;
        }
        if (TargetUtil.isNewTargetTooFar((LivingEntity)dwarf, (LivingEntity)target, (double)4.0)) {
            return;
        }
        if (target.getType() == EntityType.PLAYER && dwarf.getWorld().getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
            becomeAngryWithPlayer(dwarf, target);
            angerNearbyDwarfs(dwarf);
        } else {
            becomeAngryWith(dwarf, target);
            angerAtCloserTargets(dwarf, target);
        }
    }

    private static void becomeAngryWithPlayer(DwarfEntity piglin, LivingEntity player) {
        Optional<PlayerEntity> optional = getNearestDetectedPlayer(piglin);
        if (optional.isPresent()) {
            becomeAngryWith(piglin, optional.get());
        } else {
            becomeAngryWith(piglin, player);
        }
    }

    public static Optional<PlayerEntity> getNearestDetectedPlayer(DwarfEntity dwarf) {
        if (dwarf.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)) {
            return dwarf.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
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

        Optional optional = TargetUtil.getEntity(dwarf, MemoryModuleType.ANGRY_AT);
        if (optional.isPresent() && Sensor.testAttackableTargetPredicateIgnoreVisibility((LivingEntity)dwarf, (LivingEntity)((LivingEntity)optional.get()))) {
            return optional;
        }
        if (brain.hasMemoryModule(MemoryModuleType.UNIVERSAL_ANGER) && (optional2 = brain.getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)).isPresent()) {
            return optional2;
        }
        optional2 = brain.getOptionalRegisteredMemory(MemoryModuleType.NEAREST_HOSTILE);
        if (optional2.isPresent()) {
            return optional2;
        }
        return Optional.empty();
    }







// ,Pair.of(1, new TargetTask(MemoryModuleType.NEAREST_HOSTILE)), Pair.of(3, new MoveToTargetTask(f)), Pair.of(3, new AttackTargetTask(20))




}
