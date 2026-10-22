package warhammermod.utils.Registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import warhammermod.Entities.Projectile.Bullet;
import warhammermod.Entities.Projectile.FlameEntity;
import warhammermod.Entities.Projectile.GrenadeEntity;
import warhammermod.Entities.Projectile.ShotEntity;
import warhammermod.Entities.Projectile.StoneEntity;
import warhammermod.Entities.Projectile.WarpBulletEntity;
import warhammermod.utils.reference;

public class Entityinit {
    public static EntityType<Bullet> Bullet;
    public static EntityType<StoneEntity> STONEENTITY;
    public static EntityType<FlameEntity> Flame;
    public static EntityType<GrenadeEntity> Grenade;
    public static EntityType<ShotEntity> Shotentity;
    public static EntityType<WarpBulletEntity> WarpBullet;

    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, reference.location(name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    public static void initializeEntities() {
        Bullet = register("bullet", EntityType.Builder.<Bullet>of(Bullet::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(20));
        STONEENTITY = register("stoneentity", EntityType.Builder.<StoneEntity>of(StoneEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(20));
        Flame = register("flameentity", EntityType.Builder.<FlameEntity>of(FlameEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(20));
        Grenade = register("grenadeentity", EntityType.Builder.<GrenadeEntity>of(GrenadeEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(20));
        Shotentity = register("shotentity", EntityType.Builder.<ShotEntity>of(ShotEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(20));
        WarpBullet = register("warpbullet", EntityType.Builder.<WarpBulletEntity>of(WarpBulletEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(20));
    }
}
