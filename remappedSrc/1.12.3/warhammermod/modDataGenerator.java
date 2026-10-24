package warhammermod;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import warhammermod.Datageneration.Jobsitetagprovider;
import warhammermod.Datageneration.moditemtagprovider;

public class modDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(moditemtagprovider::new);
        pack.addProvider(Jobsitetagprovider::new);
        //pack.addProvider(modLootProvider::new);
    }
}
