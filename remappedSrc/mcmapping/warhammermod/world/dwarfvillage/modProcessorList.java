package warhammermod.world.dwarfvillage;

import com.google.common.collect.ImmutableList;
import warhammermod.utils.Registry.WHRegistry;

import java.util.List;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class modProcessorList {
    public static void bootstrap(BootstrapContext<StructureProcessorList> processorListRegisterable){
        register(processorListRegisterable, WHRegistry.DWARF_STREETS,
                ImmutableList.of(
                new RuleProcessor(ImmutableList.of(
                        new ProcessorRule(new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.45F), AlwaysTrueTest.INSTANCE, Blocks.ANDESITE.defaultBlockState()),
                        new ProcessorRule(new RandomBlockMatchTest(Blocks.GRAVEL,0.3F), AlwaysTrueTest.INSTANCE, Blocks.ANDESITE.defaultBlockState()))))
        );
    }

    private static void register(BootstrapContext<StructureProcessorList> processorListRegisterable, ResourceKey<StructureProcessorList> key, List<StructureProcessor> processors) {
        processorListRegisterable.register(key, new StructureProcessorList(processors));
    }
}
