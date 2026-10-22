package warhammermod.Entities.Projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import warhammermod.utils.Registry.Entityinit;

public class StoneEntity extends ProjectileBase implements net.minecraft.world.entity.projectile.ItemSupplier {
    public net.minecraft.world.item.ItemStack getItem() {
        return net.minecraft.world.item.Items.COBBLESTONE.getDefaultInstance();
    }
    public StoneEntity(EntityType<? extends StoneEntity> type, Level world) {
        super(type, world);
    }

    public StoneEntity(LivingEntity shooter, Level world, float damage) {
        super(Entityinit.STONEENTITY, shooter, world, damage);
    }
}
