package warhammermod.utils.Registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.equine.Horse;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor.DwarfSecondaryPointsOfInterestSensor;
import warhammermod.Entities.Living.AImanager.Data.DwarfTasks.Sensor.LordLastSeenSensor;
import warhammermod.Entities.Living.AImanager.Data.EntityAttributes;
import warhammermod.Entities.Living.AImanager.sensor.DwarfHostilesSensor;
import warhammermod.Entities.Living.AImanager.sensor.dwarfBabiesSensor;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.firecomponent;
import warhammermod.utils.reference;
import warhammermod.world.EntitySpawning;

public class WHRegistry {

    public static final DataComponentType<Ammocomponent> AMMO = DataComponentType.<Ammocomponent>builder().persistent(Ammocomponent.CODEC).networkSynchronized(Ammocomponent.PACKET_CODEC).build();
    public static final DataComponentType<firecomponent> Fireorder = DataComponentType.<firecomponent>builder().persistent(firecomponent.CODEC).networkSynchronized(firecomponent.PACKET_CODEC).build();

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

    public static void registerparticles() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, reference.location("warp_particle"), WARP);
    }

    public static void registercomponents() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, reference.location("ammo"), AMMO);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, reference.location("fireorder"), Fireorder);
    }

    public static void registerattributes() {
        FabricDefaultAttributeRegistry.register(Entityinit.Pegasus, Horse.createBaseHorseAttributes());
        FabricDefaultAttributeRegistry.register(Entityinit.SKAVEN, SkavenEntity.createHostileAttributes());
        FabricDefaultAttributeRegistry.register(Entityinit.DWARF, EntityAttributes.registerDwarfTypesattributes());
    }

    public static SoundEvent ratambient = SoundEvent.createVariableRangeEvent(reference.location("entity.skaven.ambient"));
    public static SoundEvent ratdeath = SoundEvent.createVariableRangeEvent(reference.location("entity.skaven.death"));
    public static SoundEvent rathurt = SoundEvent.createVariableRangeEvent(reference.location("entity.skaven.hurt"));
    public static SoundEvent flame = SoundEvent.createVariableRangeEvent(reference.location("flamethrower"));

    public static void registersounds() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, reference.location("entity.skaven.ambient"), ratambient);
        Registry.register(BuiltInRegistries.SOUND_EVENT, reference.location("entity.skaven.death"), ratdeath);
        Registry.register(BuiltInRegistries.SOUND_EVENT, reference.location("entity.skaven.hurt"), rathurt);
        Registry.register(BuiltInRegistries.SOUND_EVENT, reference.location("flamethrower"), flame);
    }

    public static SensorType<DwarfSecondaryPointsOfInterestSensor> SECONDARY_POIS;
    public static SensorType<LordLastSeenSensor> Lord_LastSeen;
    public static SensorType<dwarfBabiesSensor> VISIBLE_VILLAGER_BABIES;
    public static SensorType<DwarfHostilesSensor> Hostiles;

    public static void registersensors() {
        Lord_LastSeen = Registry.register(BuiltInRegistries.SENSOR_TYPE, reference.location("lordlastseen"), new SensorType<>(LordLastSeenSensor::new));
        VISIBLE_VILLAGER_BABIES = Registry.register(BuiltInRegistries.SENSOR_TYPE, reference.location("visiblebabydwarf"), new SensorType<>(dwarfBabiesSensor::new));
        Hostiles = Registry.register(BuiltInRegistries.SENSOR_TYPE, reference.location("hostiles"), new SensorType<>(DwarfHostilesSensor::new));
        SECONDARY_POIS = Registry.register(BuiltInRegistries.SENSOR_TYPE, reference.location("poissecond"), new SensorType<>(DwarfSecondaryPointsOfInterestSensor::new));
    }

    public static final net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable> HERO_OF_THE_VILLAGE_LORD_GIFT_GAMEPLAY = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, reference.location("gameplay/hero_of_the_village/lord_gift"));
    public static final net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable> GIVE_TUTORIAL_BOOK = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, reference.location("gameplay/books"));

    public static final ResourceKey<Registry<DwarfProfessionRecord>> DwarfProfessionKey = ResourceKey.createRegistryKey(reference.location("dwarf_profession"));
    public static final Registry<DwarfProfessionRecord> DWARF_PROFESSIONS = FabricRegistryBuilder.createDefaulted(DwarfProfessionKey, reference.Dwarf_warrior).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static void registerprofessions() {
        DwarfProfessionRecord.initialize();
    }
}
