package warhammermod.Items.ranged;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import warhammermod.Client.Render.Item.RenderRatlingGun;
import warhammermod.Client.Render.Item.RenderRepeater;
import warhammermod.Items.firecomponent;
import warhammermod.utils.ItemFiringPayload;
import warhammermod.utils.ModEnchantmentHelper;
import warhammermod.utils.Registry.WHRegistry;


public class Gun3DTemplate extends GunTemplate implements ItemConvertible {
    public Gun3DTemplate(net.minecraft.item.Item.Settings properties, Item ammotype, int time, int magsize, float damagein){
        super(properties, ammotype, time, magsize,damagein);
    }






}
