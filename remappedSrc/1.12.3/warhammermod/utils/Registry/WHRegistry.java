package warhammermod.utils.Registry;


import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
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

    public static final RegistryKey<LootTable> HERO_OF_THE_VILLAGE_LORD_GIFT_GAMEPLAY;
    public static final RegistryKey<LootTable> GIVE_TUTORIAL_BOOK;

    public static final ComponentType<Ammocomponent> AMMO= ComponentType.<Ammocomponent>builder().codec(Ammocomponent.CODEC).packetCodec(Ammocomponent.PACKET_CODEC).build();
    public static final ComponentType<firecomponent> Fireorder= ComponentType.<firecomponent>builder().codec(firecomponent.CODEC).packetCodec(firecomponent.PACKET_CODEC).build();

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
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(reference.modid,"warp_particle"),WARP);
    }

    public static void registercomponents(){
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(reference.modid,"ammo"),AMMO);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(reference.modid,"fireorder"),Fireorder);
    }

    public static void registerattributes(){
        FabricDefaultAttributeRegistry.register(Entityinit.PEGASUS, HorseEntity.createBaseHorseAttributes());
        FabricDefaultAttributeRegistry.register(Entityinit.SKAVEN, SkavenEntity.createHostileAttributes());
        FabricDefaultAttributeRegistry.register(Entityinit.DWARF, EntityAttributes.registerDwarfTypesattributes());
    }



    public static final Identifier ratambientID = Identifier.of(reference.modid,"entity.skaven.ambient");
    public static SoundEvent ratambient = SoundEvent.of(ratambientID);
    public static final Identifier ratdeathID = Identifier.of(reference.modid,"entity.skaven.death");
    public static SoundEvent ratdeath = SoundEvent.of(ratdeathID);
    public static final Identifier rathurtID = Identifier.of(reference.modid,"entity.skaven.hurt");
    public static SoundEvent rathurt = SoundEvent.of(rathurtID);
    public static final Identifier flameID = Identifier.of(reference.modid,"entity.flamethrower.flame");
    public static SoundEvent flame = SoundEvent.of(flameID);

    public static void registersounds(){
        Registry.register(Registries.SOUND_EVENT,ratambientID,ratambient);
        Registry.register(Registries.SOUND_EVENT,ratdeathID,ratdeath);
        Registry.register(Registries.SOUND_EVENT,rathurtID,rathurt);
        Registry.register(Registries.SOUND_EVENT,flameID,flame);
    }

    public static SensorType<DwarfSecondaryPointsOfInterestSensor> SECONDARY_POIS;
    public static final Identifier SECONDARY_POIS_id = Identifier.of(reference.modid,"poissecond");

    public static SensorType<LordLastSeenSensor> Lord_LastSeen;
    public static final Identifier Lord_LastSeenid = Identifier.of(reference.modid,"lordlasrseen");
    public static SensorType<dwarfBabiesSensor> VISIBLE_VILLAGER_BABIES;
    public static final Identifier VISIBLE_VILLAGER_BABIESid = Identifier.of(reference.modid,"visiblebabydwarf");
    public static SensorType<DwarfHostilesSensor> Hostiles;
    public static final Identifier Hostilesid = Identifier.of(reference.modid,"hostiles");

    public static void registersensors(){
        Lord_LastSeen = Registry.register(Registries.SENSOR_TYPE,Lord_LastSeenid,accesssensor.init(LordLastSeenSensor::new));
        VISIBLE_VILLAGER_BABIES= Registry.register(Registries.SENSOR_TYPE,VISIBLE_VILLAGER_BABIESid,accesssensor.init(dwarfBabiesSensor::new));
        Hostiles= Registry.register(Registries.SENSOR_TYPE,Hostilesid,accesssensor.init(DwarfHostilesSensor::new));
    }

    public static final RegistryKey<StructureProcessorList> DWARF_STREETS = RegistryKey.of(RegistryKeys.PROCESSOR_LIST, Identifier.of(reference.modid,"dwarf_path"));


    public static void registerProcessors() {
        //Registry.register(BuiltinRegistries.createWrapperLookup(), new Identifier(reference.modid, "pathschange"), DwarfVillagePools.pathchange);
        BuiltinRegistries.createWrapperLookup().createRegistryLookup();
    }


    public static void registerStructures() {

    }
    public static final RegistryKey<Registry<DwarfProfessionRecord>> DwarfProfessionKey = RegistryKey.ofRegistry(Identifier.of(reference.modid,"dwarf_profession"));
    public static final Registry<DwarfProfessionRecord> DWARF_PROFESSIONS= FabricRegistryBuilder.createDefaulted(DwarfProfessionKey,reference.Dwarf_warrior).attribute(RegistryAttribute.SYNCED).buildAndRegister();


    public static void registerprofessions(){
        DwarfProfessionRecord.initialize();
    }
    static{

        SECONDARY_POIS= Registry.register(Registries.SENSOR_TYPE,SECONDARY_POIS_id, accesssensor.init(DwarfSecondaryPointsOfInterestSensor::new));

        HERO_OF_THE_VILLAGE_LORD_GIFT_GAMEPLAY= RegistryKey.of(RegistryKeys.LOOT_TABLE,Identifier.of(reference.modid,"gameplay/hero_of_the_village/lord_gift"));

        GIVE_TUTORIAL_BOOK = RegistryKey.of(RegistryKeys.LOOT_TABLE,Identifier.of(reference.modid,"gameplay/books2"));

    }


}
