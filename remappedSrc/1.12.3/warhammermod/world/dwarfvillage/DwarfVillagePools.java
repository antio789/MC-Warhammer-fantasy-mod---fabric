package warhammermod.world.dwarfvillage;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import warhammermod.utils.reference;

import java.util.List;

public class DwarfVillagePools {
    //public static final StructurePool TOWN_CENTER = StructurePools.register(new StructurePool(Identifier.of("warhammermod:dwarf_village/town_centers"), Identifier.of("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("town_centers/dwarf_meeting_point_1").toString(), StructureProcessorLists.EMPTY), 1),Pair.of(StructurePoolElement.ofProcessedLegacySingle(location("town_centers/tavern").toString(), StructureProcessorLists.EMPTY), 3)), StructurePool.Projection.RIGID));
    public static final RegistryKey<StructurePool> TOWN_CENTERS_KEY = StructurePools.of("warhammermod:dwarf_village/town_centers");
    private static final RegistryKey<StructurePool> TERMINATORS_KEY = StructurePools.ofVanilla("village/plains/terminators");

    public static void init() {

    }

    public static void bootstrap(RegistryWrapper.WrapperLookup poolRegisterable, FabricDynamicRegistryProvider.Entries entries, RegistryEntry<StructureProcessorList>  entry) {
        RegistryEntryLookup<StructurePool> registryEntryLookup3 = poolRegisterable.createRegistryLookup().getOrThrow(RegistryKeys.TEMPLATE_POOL);
        RegistryEntry.Reference<StructurePool> Fallback = registryEntryLookup3.getOrThrow(StructurePools.EMPTY);
        RegistryEntry.Reference<StructurePool> registryEntry11 = registryEntryLookup3.getOrThrow(TERMINATORS_KEY);

        entries.add(TOWN_CENTERS_KEY, new StructurePool(Fallback, ImmutableList.of(
                Pair.of(StructurePoolElement.ofLegacySingle("town_centers/dwarf_meeting_point_1"), 1),
                Pair.of(StructurePoolElement.ofLegacySingle("town_centers/tavern"), 3)),
                StructurePool.Projection.RIGID));
        entries.add( StructurePools.EMPTY, new StructurePool(registryEntry11,
                ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle("warhammermod:dwarf_village/streets/corner_01"), 2),
                    Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/corner_02", entry), 2),
                        Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/corner_03", entry), 2),
                        Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/straight_01", entry), 2),
                        Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/straight_02", entry), 2),
                        Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/crossroad_01", entry), 2),
                        Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/crossroad_02", entry), 1),
                        Pair.of(StructurePoolElement.ofProcessedLegacySingle("warhammermod:dwarf_village/streets/turn_01",entry), 3)),
                StructurePool.Projection.TERRAIN_MATCHING));

    }
    private static void register(Registerable<StructureProcessorList> processorListRegisterable, RegistryKey<StructureProcessorList> key, List<StructureProcessor> processors) {
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
    public static Identifier location(String name)
    {
        return Identifier.of(reference.modid, "dwarf_village/"+name);
    }

}