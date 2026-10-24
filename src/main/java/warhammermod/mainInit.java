package warhammermod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import warhammermod.world.SkavenPatrolSpawner;
import warhammermod.Enchantements.ModEnchantements;
import warhammermod.Items.ItemsInit;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.WHRegistry;

public class mainInit implements ModInitializer {
    @Override
    public void onInitialize() {
        ItemsInit.registeritems();
        Entityinit.initializeEntities();
        WHRegistry.initialize();
        ModEnchantements.initialize();

        SkavenPatrolSpawner skavenPatrolSpawner = new SkavenPatrolSpawner();
        ServerTickEvents.START_LEVEL_TICK.register(level -> skavenPatrolSpawner.tick(level, level.isSpawningMonsters()));
    }
}
