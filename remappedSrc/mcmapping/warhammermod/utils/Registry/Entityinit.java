package warhammermod.utils.Registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.Entities.Living.PegasusEntity;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.Entities.Projectile.*;
import warhammermod.utils.reference;


public class Entityinit {
    public static EntityType<HalberdEntity> halberdthrust;
    public static EntityType<Bullet> Bullet;
    public static EntityType<SpearEntity> SpearProjectile;
    public static EntityType<StoneEntity> STONEENTITY;
    public static EntityType<FlameEntity> Flame;
    public static EntityType<GrenadeEntity> Grenade;
    public static EntityType<ShotEntity> Shotentity;
    public static EntityType<WarpBulletEntity> WarpBullet;


    public static EntityType<PegasusEntity> PEGASUS;
    public static Item Pegasus_SPAWN_EGG;

    public static EntityType<SkavenEntity> SKAVEN;
    public static Item SKAVEN_SPAWN_EGG;

    public static EntityType<DwarfEntity> DWARF;
    public static Item DWARF_SPAWN_EGG;

    public static void initializeEntities(){
        PEGASUS = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(reference.modid,"pegasus"),
                EntityType.Builder.of(PegasusEntity::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10).build()
        );
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(reference.modid, "pegasus_egg"), Pegasus_SPAWN_EGG = new SpawnEggItem(PEGASUS, 15528173,15395562,new Item.Properties()));
        SKAVEN = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(reference.modid,"skaven"),
                EntityType.Builder.of(SkavenEntity::new, MobCategory.MONSTER).sized(0.6F, 1.6F).canSpawnFarFromPlayer().clientTrackingRange(8).build()
        );
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(reference.modid, "skaven_egg"), SKAVEN_SPAWN_EGG = new SpawnEggItem(SKAVEN, 13698049,894731, new Item.Properties()));

        DWARF = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(reference.modid,"dwarf"),
                EntityType.Builder.<DwarfEntity>of(DwarfEntity::new, MobCategory.MISC).sized(0.6F, 1.7F).clientTrackingRange(10).build()
        );
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(reference.modid, "dwarf_egg"), DWARF_SPAWN_EGG = new SpawnEggItem(DWARF, 1599971,15721509, new Item.Properties()));



/* deprecated left in case of issues
        Pegasus = Registry.register(Registries.ENTITY_TYPE, Identifier.of(reference.modid,"pegasus"), FabricEntityTypeBuilder.<PegasusEntity>create(
                SpawnGroup.CREATURE,PegasusEntity::new).trackRangeBlocks(10).dimensions(EntityDimensions.changing(1.3964844F, 1.6F)).build());
        Registry.register(Registries.ITEM, Identifier.of(reference.modid, "pegasus_egg"), Pegasus_SPAWN_EGG = new SpawnEggItem(Pegasus, 15528173,15395562,new Item.Settings()));
        SKAVEN = Registry.register(Registries.ENTITY_TYPE, Identifier.of(reference.modid,"skaven"), FabricEntityTypeBuilder.<SkavenEntity>create(
                SpawnGroup.MONSTER,SkavenEntity::new).trackRangeBlocks(10).dimensions(EntityDimensions.changing(0.6F, 1.6F)).build());
        Registry.register(Registries.ITEM, Identifier.of(reference.modid, "skaven_egg"), SKAVEN_SPAWN_EGG = new SpawnEggItem(SKAVEN, 13698049,894731, new Item.Settings()));

        DWARF = Registry.register(Registries.ENTITY_TYPE, Identifier.of(reference.modid,"dwarf"), FabricEntityTypeBuilder.<DwarfEntity>create(
                SpawnGroup.MISC,DwarfEntity::new).trackRangeBlocks(10).dimensions(EntityDimensions.changing(0.6F, 1.7F)).build());
        Registry.register(Registries.ITEM, Identifier.of(reference.modid, "dwarf_egg"), DWARF_SPAWN_EGG = new SpawnEggItem(DWARF, 1599971,15721509, new Item.Settings()));

*/


        halberdthrust = Registry.register(BuiltInRegistries.ENTITY_TYPE,ResourceLocation.fromNamespaceAndPath(reference.modid,"halberdentity"), EntityType.Builder.<HalberdEntity>of(HalberdEntity::new,MobCategory.MISC)
                .sized(0.25F,0.25F).clientTrackingRange(4).updateInterval(10).build()
        );
        Bullet = Registry.register(BuiltInRegistries.ENTITY_TYPE,ResourceLocation.fromNamespaceAndPath(reference.modid,"bullet"), EntityType.Builder.<Bullet>of(Bullet::new,MobCategory.MISC)
                .sized(0.25F,0.25F).clientTrackingRange(8).updateInterval(20).build()
        );
        SpearProjectile = Registry.register(BuiltInRegistries.ENTITY_TYPE,ResourceLocation.fromNamespaceAndPath(reference.modid,"spearentity"), EntityType.Builder.<SpearEntity>of(SpearEntity::new,MobCategory.MISC)
                .sized(0.25F,0.25F).clientTrackingRange(4).updateInterval(20).build()
        );
        STONEENTITY= Registry.register(BuiltInRegistries.ENTITY_TYPE,ResourceLocation.fromNamespaceAndPath(reference.modid,"stoneentity"), EntityType.Builder.<StoneEntity>of(StoneEntity::new,MobCategory.MISC)
                .sized(0.25F,0.25F).clientTrackingRange(8).updateInterval(20).build()
        );
        Flame= Registry.register(BuiltInRegistries.ENTITY_TYPE,ResourceLocation.fromNamespaceAndPath(reference.modid,"flameentity"), EntityType.Builder.<FlameEntity>of(FlameEntity::new,MobCategory.MISC)
                .sized(0.25F,0.25F).clientTrackingRange(4).updateInterval(20).build()
        );
        Grenade= Registry.register(BuiltInRegistries.ENTITY_TYPE,ResourceLocation.fromNamespaceAndPath(reference.modid,"grenadeentity"), EntityType.Builder.<GrenadeEntity>of(GrenadeEntity::new,MobCategory.MISC)
                .sized(0.25F,0.25F).clientTrackingRange(4).updateInterval(20).build()
        );
        Shotentity = Registry.register(BuiltInRegistries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(reference.modid,"shotentity"), EntityType.Builder.<ShotEntity>of(ShotEntity::new,MobCategory.MISC)
                .sized(0.25F,0.25F).clientTrackingRange(4).updateInterval(20).build()
        );
        WarpBullet = Registry.register(BuiltInRegistries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(reference.modid,"warpbullet"), EntityType.Builder.<WarpBulletEntity>of(WarpBulletEntity::new,MobCategory.MISC)
                .sized(0.25F,0.25F).clientTrackingRange(8).updateInterval(20).build()
        );
    }

}
