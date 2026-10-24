package warhammermod.Entities.Projectile;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;

public class ShotEntity extends ProjectileBase {
    public BlockPos Playerpos;
    private int timer;
    public ShotEntity(World world) {
        super(Entityinit.Shotentity, world);
    }

    public ShotEntity(EntityType<? extends ShotEntity> p_i50148_1_, World p_i50148_2_) {
        super(p_i50148_1_, p_i50148_2_);
    }

    public ShotEntity(World worldin, LivingEntity shooter, float damageIn, EntityType<? extends ProjectileBase> type,ItemStack stack,ItemStack ammo) {
        super(type,worldin, shooter,stack,damageIn,ammo);
    }



    public ShotEntity(LivingEntity shooter, World world, float damage, BlockPos blockPos,ItemStack stack,ItemStack ammo) {
        this(world, shooter, damage, Entityinit.Shotentity,stack,ammo);
        Playerpos = blockPos;
    }



    protected void onEntityHit(EntityHitResult entityHitResult) {
        if(modifydamage(timer)) {
            super.onEntityHit(entityHitResult);
        }else this.remove(RemovalReason.DISCARDED);

    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ItemsInit.Shotshell);
    }

    public void tick()
    {
        super.tick();
        timer++;
    }

    public boolean modifydamage(double distance){
        double modifier = damagemodifier(distance);
        projectiledamage*=modifier;
        extradamage*=modifier;
        return !(modifier==0);
    }

    public double damagemodifier(double X){
        if(X==0)X=1;
        return Math.max( -0.3*X+1.3,0);
    }
}
