package warhammermod.Datageneration;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import warhammermod.utils.reference;

import java.util.concurrent.CompletableFuture;

public class Jobsitetagprovider extends FabricTagProvider<PointOfInterestType> {
    public static final TagKey<PointOfInterestType> ACQUIRABLE_JOB_SITE = TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.of(reference.modid,"poi"));

    public Jobsitetagprovider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, RegistryKeys.POINT_OF_INTEREST_TYPE,completableFuture);
    }


    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ACQUIRABLE_JOB_SITE).add(PointOfInterestTypes.ARMORER, PointOfInterestTypes.BUTCHER, PointOfInterestTypes.FARMER, PointOfInterestTypes.MASON, PointOfInterestTypes.TOOLSMITH);
    }
}
