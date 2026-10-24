package warhammermod.Datageneration;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import warhammermod.utils.reference;

import java.util.concurrent.CompletableFuture;

public class Jobsitetagprovider extends FabricTagProvider<PoiType> {
    public static final TagKey<PoiType> ACQUIRABLE_JOB_SITE = TagKey.create(Registries.POINT_OF_INTEREST_TYPE, ResourceLocation.fromNamespaceAndPath(reference.modid,"poi"));

    public Jobsitetagprovider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Registries.POINT_OF_INTEREST_TYPE,completableFuture);
    }


    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        tag(ACQUIRABLE_JOB_SITE).add(PoiTypes.ARMORER, PoiTypes.BUTCHER, PoiTypes.FARMER, PoiTypes.MASON, PoiTypes.TOOLSMITH);
    }
}
