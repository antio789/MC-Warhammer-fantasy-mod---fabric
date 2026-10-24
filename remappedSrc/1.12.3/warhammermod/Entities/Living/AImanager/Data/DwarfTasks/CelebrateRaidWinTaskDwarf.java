/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.SeekSkyTask;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import org.jetbrains.annotations.Nullable;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.List;

public class CelebrateRaidWinTaskDwarf
extends MultiTickTask<DwarfEntity> {
    @Nullable
    private Raid raid;

    public CelebrateRaidWinTaskDwarf(int minRunTime, int maxRunTime) {
        super(ImmutableMap.of(), minRunTime, maxRunTime);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, DwarfEntity dwarf) {
        BlockPos blockPos = dwarf.getBlockPos();
        this.raid = serverWorld.getRaidAt(blockPos);
        return this.raid != null && this.raid.hasWon() && SeekSkyTask.isSkyVisible(serverWorld, dwarf, blockPos);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, DwarfEntity dwarf, long l) {
        return this.raid != null && !this.raid.hasStopped();
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, DwarfEntity dwarf, long l) {
        this.raid = null;
        dwarf.getBrain().refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, DwarfEntity dwarfEntity, long l) {
        Random random = dwarfEntity.getRandom();
        if (random.nextInt(100) == 0) {
            dwarfEntity.playCelebrateSound();
        }
        if (random.nextInt(200) == 0 && SeekSkyTask.isSkyVisible(serverWorld, dwarfEntity, dwarfEntity.getBlockPos())) {
            DyeColor dyeColor = Util.getRandom(DyeColor.values(), random);
            int i = random.nextInt(3);
            ItemStack itemStack = this.createFirework(dyeColor, i);
            FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(dwarfEntity.getWorld(), dwarfEntity, dwarfEntity.getX(), dwarfEntity.getEyeY(), dwarfEntity.getZ(), itemStack);
            dwarfEntity.getWorld().spawnEntity(fireworkRocketEntity);
        }
    }

    private ItemStack createFirework(DyeColor color, int flight) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET);
        itemStack.set(DataComponentTypes.FIREWORKS, new FireworksComponent((byte)flight, List.of(new FireworkExplosionComponent(FireworkExplosionComponent.Type.BURST, IntList.of(color.getFireworkColor()), IntList.of(), false, false))));
        return itemStack;
    }
}

