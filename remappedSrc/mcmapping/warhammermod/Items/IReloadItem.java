package warhammermod.Items;


import net.minecraft.world.item.ItemStack;

public interface IReloadItem {
    int getTimetoreload();
    boolean isReadytoFire(ItemStack stack);
}
