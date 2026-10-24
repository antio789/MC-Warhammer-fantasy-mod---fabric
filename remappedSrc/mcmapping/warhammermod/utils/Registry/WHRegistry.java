package warhammermod.utils.Registry;


import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.registry.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.storage.loot.LootTable;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor.DwarfSecondaryPointsOfInterestSensor;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor.LordLastSeenSensor;
import warhammermod.Entities.Living.AImanager.Data.EntityAttributes;
import warhammermod.Entities.Living.AImanager.sensor.DwarfHostilesSensor;
import warhammermod.Entities.Living.AImanager.sensor.dwarfBabiesSensor;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.firecomponent;
import warhammermod.mixin.accesssensor;
import warhammermod.utils.reference;
import warhammermod.world.EntitySpawning;

public class WHRegistry {

    public static final ResourceKey<LootTable> HERO_OF_THE_VILLAGE_LORD_GIFT_GAMEPLAY;
    public static final ResourceKey<LootTable> GIVE_TUTORIAL_BOOK;

    public static final DataComponentType<Ammocomponent> AMMO= DataComponentType.<Ammocomponent>builder().persistent(Ammocomponent.CODEC).networkSynchronized(Ammocomponent.PACKET_CODEC).build();
    public static final DataComponentType<firecomponent> Fireorder= DataComponentType.<firecomponent>builder().persistent(firecomponent.CODEC).networkSynchronized(firecomponent.PACKET_CODEC).build();

    public static final SimpleParticleType WARP = FabricParticleTypes.simple();

    public static void initialize() {
        registercomponents();
        registersounds();
        registersensors();
        registerattributes();
        registerprofessions();
        registerparticles();
        EntitySpawning.SpawnRestriction();
        EntitySpawning.addSpawn();
    }
    public static void registerparticles(){
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(reference.modid,"warp_particle"),WARP);
    }

    public static void registercomponents(){
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(reference.modid,"ammo"),AMMO);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(reference.modid,"fireorder"),Fireorder);
    }

    public static void registerattributes(){
        FabricDefaultAttributeRegistry.register(Entityinit.PEGASUS, Horse.createBaseHorseAttributes());
        FabricDefaultAttributeRegistry.register(Entityinit.SKAVEN, SkavenEntity.createMonsterAttributes());
        FabricDefaultAttributeRegistry.register(Entityinit.DWARF, EntityAttributes.registerDwarfTypesattributes());
    }



    public static final ResourceLocation ratambientID = ResourceLocation.fromNamespaceAndPath(reference.modid,"entity.skaven.ambient");
    public static SoundEvent ratambient = SoundEvent.createVariableRangeEvent(ratambientID);
    public static final ResourceLocation ratdeathID = ResourceLocation.fromNamespaceAndPath(reference.modid,"entity.skaven.death");
    public static SoundEvent ratdeath = SoundEvent.createVariableRangeEvent(ratdeathID);
    public static final ResourceLocation rathurtID = ResourceLocation.fromNamespaceAndPath(reference.modid,"entity.skaven.hurt");
    public static SoundEvent rathurt = SoundEvent.createVariableRangeEvent(rathurtID);
    public static final ResourceLocation flameID = ResourceLocation.fromNamespaceAndPath(reference.modid,"entity.flamethrower.flame");
    public static SoundEvent flame = SoundEvent.createVariableRangeEvent(flameID);

    public static void registersounds(){
        Registry.register(BuiltInRegistries.SOUND_EVENT,ratambientID,ratambient);
        Registry.register(BuiltInRegistries.SOUND_EVENT,ratdeathID,ratdeath);
        Registry.register(BuiltInRegistries.SOUND_EVENT,rathurtID,rathurt);
        Registry.register(BuiltInRegistries.SOUND_EVENT,flameID,flame);
    }

    public static SensorType<DwarfSecondaryPointsOfInterestSensor> SECONDARY_POIS;
    public static final ResourceLocation SECONDARY_POIS_id = ResourceLocation.fromNamespaceAndPath(reference.modid,"poissecond");

    public static SensorType<LordLastSeenSensor> Lord_LastSeen;
    public static final ResourceLocation Lord_LastSeenid = ResourceLocation.fromNamespaceAndPath(reference.modid,"lordlasrseen");
    public static SensorType<dwarfBabiesSensor> VISIBLE_VILLAGER_BABIES;
    public static final ResourceLocation VISIBLE_VILLAGER_BABIESid = ResourceLocation.fromNamespaceAndPath(reference.modid,"visiblebabydwarf");
    public static SensorType<DwarfHostilesSensor> Hostiles;
    public static final ResourceLocation Hostilesid = ResourceLocation.fromNamespaceAndPath(reference.modid,"hostiles");

    public static void registersensors(){
        Lord_LastSeen = Registry.register(BuiltInRegistries.SENSOR_TYPE,Lord_LastSeenid,accesssensor.init(LordLastSeenSensor::new));
        VISIBLE_VILLAGER_BABIES= Registry.register(BuiltInRegistries.SENSOR_TYPE,VISIBLE_VILLAGER_BABIESid,accesssensor.init(dwarfBabiesSensor::new));
        Hostiles= Registry.register(BuiltInRegistries.SENSOR_TYPE,Hostilesid,accesssensor.init(DwarfHostilesSensor::new));
    }

    public static final ResourceKey<StructureProcessorList> DWARF_STREETS = ResourceKey.create(Registries.PROCESSOR_LIST, ResourceLocation.fromNamespaceAndPath(reference.modid,"dwarf_path"));


    public static void registerProcessors() {
        //Registry.register(BuiltinRegistries.createWrapperLookup(), new Identifier(reference.modid, "pathschange"), DwarfVillagePools.pathchange);
        VanillaRegistries.createLookup().asGetterLookup();
    }


    public static void registerStructures() {

    }
    public static final ResourceKey<Registry<DwarfProfessionRecord>> DwarfProfessionKey = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(reference.modid,"dwarf_profession"));
    public static final Registry<DwarfProfessionRecord> DWARF_PROFESSIONS= FabricRegistryBuilder.createDefaulted(DwarfProfessionKey,reference.Dwarf_warrior).attribute(RegistryAttribute.SYNCED).buildAndRegister();


    public static void registerprofessions(){
        DwarfProfessionRecord.initialize();
    }
    static{

        SECONDARY_POIS= Registry.register(BuiltInRegistries.SENSOR_TYPE,SECONDARY_POIS_id, accesssensor.init(DwarfSecondaryPointsOfInterestSensor::new));

        HERO_OF_THE_VILLAGE_LORD_GIFT_GAMEPLAY= ResourceKey.create(Registries.LOOT_TABLE,ResourceLocation.fromNamespaceAndPath(reference.modid,"gameplay/hero_of_the_village/lord_gift"));

        GIVE_TUTORIAL_BOOK = ResourceKey.create(Registries.LOOT_TABLE,ResourceLocation.fromNamespaceAndPath(reference.modid,"gameplay/books2"));

    }


}
