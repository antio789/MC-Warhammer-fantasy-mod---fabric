package warhammermod.Items.ranged;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import warhammermod.Client.Render.Item.RenderRatlingGun;
import warhammermod.Client.Render.Item.RenderRepeater;
import warhammermod.Items.firecomponent;
import warhammermod.utils.ItemFiringPayload;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.WHRegistry;


public class Gun3DTemplate extends GunTemplate implements ItemLike {
    public Gun3DTemplate(Properties properties, Item ammotype, int time, int magsize, float damagein){
        super(properties, ammotype, time, magsize,damagein);
    }






}
