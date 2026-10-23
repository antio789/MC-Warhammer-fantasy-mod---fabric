package warhammermod.Entities.Living.AImanager.Data.DwarfTasks;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.MoveToSkySeeingSpot;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.entity.raid.Raid;
import org.jetbrains.annotations.Nullable;
import warhammermod.Entities.Living.DwarfEntity;

import java.util.List;

public class CelebrateRaidWinTaskDwarf extends Behavior<DwarfEntity> {
    @Nullable
    private Raid raid;

    public CelebrateRaidWinTaskDwarf(int minRunTime, int maxRunTime) {
        super(ImmutableMap.of(), minRunTime, maxRunTime);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverWorld, DwarfEntity dwarf) {
        BlockPos blockPos = dwarf.blockPosition();
        this.raid = serverWorld.getRaidAt(blockPos);
        return this.raid != null && this.raid.isVictory() && MoveToSkySeeingSpot.hasNoBlocksAbove(serverWorld, dwarf, blockPos);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        return this.raid != null && !this.raid.isStopped();
    }

    @Override
    protected void stop(ServerLevel serverWorld, DwarfEntity dwarf, long l) {
        this.raid = null;
    }

    @Override
    protected void tick(ServerLevel serverWorld, DwarfEntity dwarfEntity, long l) {
        RandomSource random = dwarfEntity.getRandom();
        if (random.nextInt(100) == 0) {
            dwarfEntity.playCelebrateSound();
        }
        if (random.nextInt(200) == 0 && MoveToSkySeeingSpot.hasNoBlocksAbove(serverWorld, dwarfEntity, dwarfEntity.blockPosition())) {
            DyeColor dyeColor = DyeColor.RED;
            int i = random.nextInt(3);
            ItemStack itemStack = this.createFirework(dyeColor, i);
            FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(dwarfEntity.level(), dwarfEntity, dwarfEntity.getX(), dwarfEntity.getEyeY(), dwarfEntity.getZ(), itemStack);
            dwarfEntity.level().addFreshEntity(fireworkRocketEntity);
        }
    }

    private ItemStack createFirework(DyeColor color, int flight) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET);
        itemStack.set(DataComponents.FIREWORKS, new Fireworks((byte) flight, List.of(new FireworkExplosion(FireworkExplosion.Shape.BURST, IntList.of(color.getFireworkColor()), IntList.of(), false, false))));
        return itemStack;
    }
}
