package warhammermod.Datageneration;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.context.ContextType;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class modLootProvider extends SimpleFabricLootTableProvider {
    public modLootProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup, ContextType lootContextType) {
        super(output, registryLookup, lootContextType);
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        //lootTableBiConsumer.accept(LootTables.HERO_OF_THE_VILLAGE_ARMORER_GIFT_GAMEPLAY, LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with(ItemEntry.builder(ItemsInit.GHAL_MARAZ).apply(EnchantRandomlyLootFunction.create()))));
    }
}
