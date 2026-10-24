package warhammermod.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.SpecialSpawner;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import warhammermod.world.SkavenPatrolSpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


@Mixin(ServerWorld.class)
public abstract class MCserver_injection {

    @ModifyVariable(at = @At("CTOR_HEAD"),method = "<init>",target = @Desc("list"))
    private List<SpecialSpawner> test(List<SpecialSpawner> spawner){
        List<SpecialSpawner> templist = new ArrayList<>(spawner);
        templist.add(new SkavenPatrolSpawner());
        return templist;
    }
}
