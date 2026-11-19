package warhammermod.utils.Registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.Entities.Living.PegasusEntity;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.Entities.Projectile.Bullet;
import warhammermod.Entities.Projectile.FlameEntity;
import warhammermod.Entities.Projectile.GrenadeEntity;
import warhammermod.Entities.Projectile.ShotEntity;
import warhammermod.Entities.Projectile.StoneEntity;
import warhammermod.Entities.Projectile.WarpBulletEntity;
import warhammermod.Items.ItemsInit;
import warhammermod.utils.reference;

public class Entityinit {
    public static EntityType<Bullet> Bullet;
    public static EntityType<StoneEntity> STONEENTITY;
    public static EntityType<FlameEntity> Flame;
    public static EntityType<GrenadeEntity> Grenade;
    public static EntityType<ShotEntity> Shotentity;
    public static EntityType<WarpBulletEntity> WarpBullet;

    public static EntityType<PegasusEntity> Pegasus;
    public static Item Pegasus_SPAWN_EGG;
    public static EntityType<SkavenEntity> SKAVEN;
    public static Item SKAVEN_SPAWN_EGG;
    public static EntityType<DwarfEntity> DWARF;
    public static Item DWARF_SPAWN_EGG;

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

        SKAVEN = register("skaven", EntityType.Builder.<SkavenEntity>of(SkavenEntity::new, MobCategory.MONSTER).sized(0.6F, 1.6F).clientTrackingRange(10));
        Pegasus = register("pegasus", EntityType.Builder.<PegasusEntity>of(PegasusEntity::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
        DWARF = register("dwarf", EntityType.Builder.<DwarfEntity>of(DwarfEntity::new, MobCategory.CREATURE).sized(0.6F, 1.7F).clientTrackingRange(10));

        FabricDefaultAttributeRegistry.register(SKAVEN, SkavenEntity.createMonsterAttributes());
        FabricDefaultAttributeRegistry.register(Pegasus, PegasusEntity.createBaseHorseAttributes());

        SKAVEN_SPAWN_EGG = ItemsInit.register("skaven_egg", properties -> new SpawnEggItem(properties.spawnEgg(SKAVEN)));
        Pegasus_SPAWN_EGG = ItemsInit.register("pegasus_egg", properties -> new SpawnEggItem(properties.spawnEgg(Pegasus)));
        DWARF_SPAWN_EGG = ItemsInit.register("dwarf_egg", properties -> new SpawnEggItem(properties.spawnEgg(DWARF)));
    }
}
