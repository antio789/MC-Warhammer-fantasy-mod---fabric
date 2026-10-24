package warhammermod.world.dwarfvillage;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.registry.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import warhammermod.utils.reference;

import java.util.List;

public class DwarfVillagePools {
    //public static final StructurePool TOWN_CENTER = StructurePools.register(new StructurePool(Identifier.of("warhammermod:dwarf_village/town_centers"), Identifier.of("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("town_centers/dwarf_meeting_point_1").toString(), StructureProcessorLists.EMPTY), 1),Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("town_centers/tavern").toString(), StructureProcessorLists.EMPTY), 3)), StructurePool.Projection.RIGID));
    public static final ResourceKey<StructureTemplatePool> TOWN_CENTERS_KEY = Pools.parseKey("warhammermod:dwarf_village/town_centers");
    private static final ResourceKey<StructureTemplatePool> TERMINATORS_KEY = Pools.createKey("village/plains/terminators");

    public static void init() {

    }

    public static void bootstrap(HolderLookup.Provider poolRegisterable, FabricDynamicRegistryProvider.Entries entries, Holder<StructureProcessorList>  entry) {
        HolderGetter<StructureTemplatePool> registryEntryLookup3 = poolRegisterable.asGetterLookup().lookupOrThrow(Registries.TEMPLATE_POOL);
        Holder.Reference<StructureTemplatePool> Fallback = registryEntryLookup3.getOrThrow(Pools.EMPTY);
        Holder.Reference<StructureTemplatePool> registryEntry11 = registryEntryLookup3.getOrThrow(TERMINATORS_KEY);

        entries.add(TOWN_CENTERS_KEY, new StructureTemplatePool(Fallback, ImmutableList.of(
                Pair.of(StructurePoolElement.legacy("town_centers/dwarf_meeting_point_1"), 1),
                Pair.of(StructurePoolElement.legacy("town_centers/tavern"), 3)),
                StructureTemplatePool.Projection.RIGID));
        entries.add( Pools.EMPTY, new StructureTemplatePool(registryEntry11,
                ImmutableList.of(Pair.of(StructurePoolElement.legacy("warhammermod:dwarf_village/streets/corner_01"), 2),
                    Pair.of(StructurePoolElement.legacy("warhammermod:dwarf_village/streets/corner_02", entry), 2),
                        Pair.of(StructurePoolElement.legacy("warhammermod:dwarf_village/streets/corner_03", entry), 2),
                        Pair.of(StructurePoolElement.legacy("warhammermod:dwarf_village/streets/straight_01", entry), 2),
                        Pair.of(StructurePoolElement.legacy("warhammermod:dwarf_village/streets/straight_02", entry), 2),
                        Pair.of(StructurePoolElement.legacy("warhammermod:dwarf_village/streets/crossroad_01", entry), 2),
                        Pair.of(StructurePoolElement.legacy("warhammermod:dwarf_village/streets/crossroad_02", entry), 1),
                        Pair.of(StructurePoolElement.legacy("warhammermod:dwarf_village/streets/turn_01",entry), 3)),
                StructureTemplatePool.Projection.TERRAIN_MATCHING));

    }
    private static void register(BootstrapContext<StructureProcessorList> processorListRegisterable, ResourceKey<StructureProcessorList> key, List<StructureProcessor> processors) {
        processorListRegisterable.register(key, new StructureProcessorList(processors));
    }
/*
    static {

         StructurePools.register(new StructurePool(Identifier.of("warhammermod:dwarf_village/streets"), Identifier.of("warhammermod:dwarf_village/terminators"),
                 ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("warhammermod:dwarf_village/streets/corner_01"), 2),
                         Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/corner_02", pathchange), 2),
                         Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/corner_03", pathchange), 2),
                         Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/straight_01", pathchange), 2),
                         Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/straight_02", pathchange), 2),
                         Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/crossroad_01", pathchange), 2),
                         Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/crossroad_02", pathchange), 1),
                         Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/turn_01",pathchange), 3)),
                 StructurePool.Projection.TERRAIN_MATCHING));

         StructurePools.register(new StructurePool(Identifier.of("warhammermod:dwarf_village/houses"), Identifier.of("dwarf_village/terminators"),ImmutableList.of(
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("houses/dwarf_small_house_1").toString(), StructureProcessorLists.EMPTY), 3),
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("houses/dwarf_small_house_2").toString(), StructureProcessorLists.EMPTY), 3),
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("houses/dwarf_medium_house_1").toString(), StructureProcessorLists.EMPTY), 2),
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("houses/dwarf_builder_house_1").toString(), StructureProcessorLists.EMPTY), 2),
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("houses/dwarf_engineer_house_1").toString(), StructureProcessorLists.EMPTY), 1),
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("houses/dwarf_farmer_house_1").toString(), StructureProcessorLists.EMPTY), 2),
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("houses/dwarf_miner_house_1").toString(), StructureProcessorLists.EMPTY), 3),
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("houses/dwarf_slayer_house_1").toString(), StructureProcessorLists.EMPTY), 1),
                 Pair.of(StructurePoolElement.ofEmpty(), 4)), StructurePool.Projection.RIGID));

        StructurePools.register(new StructurePool(Identifier.of("warhammermod:dwarf_village/dwarf"), Identifier.of("empty"), ImmutableList.of(
                Pair.of(StructurePoolElement.ofLegacySingle("warhammermod:dwarf_village/dwarf/dwarf_villager"), 1)), StructurePool.Projection.RIGID));


        StructurePools.register(new StructurePool(Identifier.of("warhammermod:dwarf_village/terminators"), Identifier.of("empty"), ImmutableList.of(
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/terminators/terminator_01", StructureProcessorLists.EMPTY), 1),
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/terminators/terminator_02", StructureProcessorLists.EMPTY), 1),
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/terminators/terminator_03", StructureProcessorLists.EMPTY), 1),
                 Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/terminators/terminator_04", StructureProcessorLists.EMPTY), 1)), StructurePool.Projection.TERRAIN_MATCHING));

         }

*/
    public static ResourceLocation location(String name)
    {
        return ResourceLocation.fromNamespaceAndPath(reference.modid, "dwarf_village/"+name);
    }

}