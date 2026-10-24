package warhammermod.mixin;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import warhammermod.world.SkavenPatrolSpawner;

import javax.print.attribute.standard.Sides;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;


@Mixin(ServerWorld.class)
public abstract class Serverworld_injection {

    @ModifyVariable(at = @At("CTOR_HEAD"),method = "<init>")
    public List<SpecialSpawner> replacelist(List<SpecialSpawner> spawners){
        List<SpecialSpawner> templist = new ArrayList<>(spawners);
        templist.add(new SkavenPatrolSpawner());
        return templist;
    }

    /*
    * @Shadow @Final
    @Mutable
    private List<SpecialSpawner> spawners;
    @ModifyVariable(at = @At("HEAD"),method = "<init>",ordinal =1)
    private void init(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<SpecialSpawner> Spawners, boolean shouldTickTime, @Nullable RandomSequencesState randomSequencesState, CallbackInfo callbackInfo){
        List<SpecialSpawner> templist = new ArrayList<>(spawners);
        templist.add(new SkavenPatrolSpawner());
        spawners= templist;
    }
    * */
}
