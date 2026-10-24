package warhammermod.Items.ranged;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import warhammermod.Entities.Projectile.FlameEntity;
import warhammermod.Items.AutogunBase;
import warhammermod.utils.Registry.WHRegistry;


public class DrakeGunTemplate extends AutogunBase { //need unbreaking and mending enchantement

    public DrakeGunTemplate(net.minecraft.item.Item.Settings properties, int MagSize, int time) {
        //super(properties,MagSize,1,time, Items.BLAZE_ROD.getName().toString(),5);
        super(properties, Items.BLAZE_ROD, time, MagSize,1,4);
    }

    public void fire(PlayerEntity player, World world, ItemStack stack){

        if(!world.isClient()){
            world.playSound(null, player.getX(), player.getY(), player.getZ(), WHRegistry.flame, player.getSoundCategory(), 0.5F, 1.05F+(rand.nextFloat()+rand.nextFloat())*0.1F);
            Vec3d vec3d = new Vec3d(player.getRotationVector().x*5 + rand.nextGaussian() * 0.1, player.getRotationVector().y*5, player.getRotationVector().z*5 + rand.nextGaussian() * 0.1);

            FlameEntity smallfireballentity = new FlameEntity(player,vec3d, world);
            smallfireballentity.setPosition(player.getX(), (player.getEyeY()+ -0.55F), player.getZ());
            world.spawnEntity(smallfireballentity);
        }
    }
}
