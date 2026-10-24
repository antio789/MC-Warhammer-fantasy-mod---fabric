package warhammermod.world.dwarfvillage;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import warhammermod.utils.Registry.WHRegistry;

import java.util.List;

public class modProcessorList {
    public static void bootstrap(Registerable<StructureProcessorList> processorListRegisterable){
        register(processorListRegisterable, WHRegistry.DWARF_STREETS,
                ImmutableList.of(
                new RuleStructureProcessor(ImmutableList.of(
                        new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.45F), AlwaysTrueRuleTest.INSTANCE, Blocks.ANDESITE.getDefaultState()),
                        new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GRAVEL,0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.ANDESITE.getDefaultState()))))
        );
    }

    private static void register(Registerable<StructureProcessorList> processorListRegisterable, RegistryKey<StructureProcessorList> key, List<StructureProcessor> processors) {
        processorListRegisterable.register(key, new StructureProcessorList(processors));
    }
}
