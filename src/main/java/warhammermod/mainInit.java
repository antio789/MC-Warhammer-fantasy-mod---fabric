package warhammermod;

import net.fabricmc.api.ModInitializer;
import warhammermod.Items.ItemsInit;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.WHRegistry;

public class mainInit implements ModInitializer {
    @Override
    public void onInitialize() {
        ItemsInit.registeritems();
        Entityinit.initializeEntities();
        WHRegistry.registersounds();
    }
}
