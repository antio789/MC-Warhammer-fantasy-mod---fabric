package warhammermod.utils.Registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
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
    /*
    public static <T extends Entity> EntityType<T> registerEntity(String name, EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup) {
        Identifier id = Identifier.of(reference.modid, name);
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);

        // Create the entity type with the provided factory
        EntityType<T> type = EntityType.Builder.create(factory, SpawnGroup.CREATURE).build(key);
        Registry.register(Registries.ENTITY_TYPE, key, type);
        return type;
    }
    */
    public static RegistryKey<EntityType<?>> keybuild (String name){
        Identifier id = Identifier.of(reference.modid, name);
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);
    }

    public static void initializeEntities(){
        PEGASUS = Registry.register(Registries.ENTITY_TYPE,
                Identifier.of(reference.modid,"pegasus"),
                EntityType.Builder.create(PegasusEntity::new, SpawnGroup.CREATURE).dimensions(1.3964844F, 1.6F).maxTrackingRange(10).build(keybuild("pegasus"))
        );
        Registry.register(Registries.ITEM, Identifier.of(reference.modid, "pegasus_egg"), Pegasus_SPAWN_EGG = new SpawnEggItem(PEGASUS, 15528173,15395562,new Item.Settings()));

        SKAVEN = Registry.register(Registries.ENTITY_TYPE,
                Identifier.of(reference.modid,"skaven"),
                EntityType.Builder.create(SkavenEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.6F).spawnableFarFromPlayer().maxTrackingRange(8).build(keybuild("skaven"))
        );
        Registry.register(Registries.ITEM, Identifier.of(reference.modid, "skaven_egg"), SKAVEN_SPAWN_EGG = new SpawnEggItem(SKAVEN, 13698049,894731, new Item.Settings()));

        DWARF = Registry.register(Registries.ENTITY_TYPE,
                Identifier.of(reference.modid,"dwarf"),
                EntityType.Builder.<DwarfEntity>create(DwarfEntity::new, SpawnGroup.MISC).dimensions(0.6F, 1.7F).maxTrackingRange(10).build(keybuild("dwarf"))
        );
        Registry.register(Registries.ITEM, Identifier.of(reference.modid, "dwarf_egg"), DWARF_SPAWN_EGG = new SpawnEggItem(DWARF, 1599971,15721509, new Item.Settings()));



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


        halberdthrust = Registry.register(Registries.ENTITY_TYPE,Identifier.of(reference.modid,"halberdentity"), EntityType.Builder.<HalberdEntity>create(HalberdEntity::new,SpawnGroup.MISC)
                .dimensions(0.25F,0.25F).maxTrackingRange(4).trackingTickInterval(10).build(keybuild("halberdentity"))
        );
        Bullet = Registry.register(Registries.ENTITY_TYPE,Identifier.of(reference.modid,"bullet"), EntityType.Builder.<Bullet>create(Bullet::new,SpawnGroup.MISC)
                .dimensions(0.25F,0.25F).maxTrackingRange(8).trackingTickInterval(20).build(keybuild("bullet"))
        );
        SpearProjectile = Registry.register(Registries.ENTITY_TYPE,Identifier.of(reference.modid,"spearentity"), EntityType.Builder.<SpearEntity>create(SpearEntity::new,SpawnGroup.MISC)
                .dimensions(0.25F,0.25F).maxTrackingRange(4).trackingTickInterval(20).build(keybuild("spearentity"))
        );
        STONEENTITY= Registry.register(Registries.ENTITY_TYPE,Identifier.of(reference.modid,"stoneentity"), EntityType.Builder.<StoneEntity>create(StoneEntity::new,SpawnGroup.MISC)
                .dimensions(0.25F,0.25F).maxTrackingRange(8).trackingTickInterval(20).build(keybuild("stoneentity"))
        );
        Flame= Registry.register(Registries.ENTITY_TYPE,Identifier.of(reference.modid,"flameentity"), EntityType.Builder.<FlameEntity>create(FlameEntity::new,SpawnGroup.MISC)
                .dimensions(0.25F,0.25F).maxTrackingRange(4).trackingTickInterval(20).build(keybuild("flameentity"))
        );
        Grenade= Registry.register(Registries.ENTITY_TYPE,Identifier.of(reference.modid,"grenadeentity"), EntityType.Builder.<GrenadeEntity>create(GrenadeEntity::new,SpawnGroup.MISC)
                .dimensions(0.25F,0.25F).maxTrackingRange(4).trackingTickInterval(20).build(keybuild("grenadeentity"))
        );
        Shotentity = Registry.register(Registries.ENTITY_TYPE, Identifier.of(reference.modid,"shotentity"), EntityType.Builder.<ShotEntity>create(ShotEntity::new,SpawnGroup.MISC)
                .dimensions(0.25F,0.25F).maxTrackingRange(4).trackingTickInterval(20).build(keybuild("shotentity"))
        );
        WarpBullet = Registry.register(Registries.ENTITY_TYPE, Identifier.of(reference.modid,"warpbullet"), EntityType.Builder.<WarpBulletEntity>create(WarpBulletEntity::new,SpawnGroup.MISC)
                .dimensions(0.25F,0.25F).maxTrackingRange(8).trackingTickInterval(20).build(keybuild("warpbullet"))
        );
    }

}
