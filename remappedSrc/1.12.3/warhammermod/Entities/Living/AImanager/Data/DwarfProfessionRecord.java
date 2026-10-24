package warhammermod.Entities.Living.AImanager.Data;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;
import warhammermod.Datageneration.Jobsitetagprovider;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;

import java.util.function.Predicate;

public record DwarfProfessionRecord(String name, int ID, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound, Item mainhainditem, Item offhanditem) {
    public static void initialize(){

    }
    public static final Predicate<RegistryEntry<PointOfInterestType>> IS_ACQUIRABLE_JOB_SITE = (poiType) -> poiType.isIn(Jobsitetagprovider.ACQUIRABLE_JOB_SITE);
    public static final DwarfProfessionRecord Warrior = DwarfProfessionRecord.register(reference.Warrior,6,PointOfInterestType.NONE,IS_ACQUIRABLE_JOB_SITE,null,Items.IRON_AXE, ItemsInit.Dwarf_shield);
    public static final DwarfProfessionRecord Miner = DwarfProfessionRecord.register(reference.Miner,0,PointOfInterestTypes.ARMORER,null,Items.IRON_PICKAXE);
    public static final DwarfProfessionRecord Builder = DwarfProfessionRecord.register(reference.Builder,1,PointOfInterestTypes.MASON,SoundEvents.ENTITY_VILLAGER_WORK_MASON,ItemsInit.iron_warhammer);
    public static final DwarfProfessionRecord Engineer = DwarfProfessionRecord.register(reference.Engineer,2,PointOfInterestTypes.TOOLSMITH,SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH,Items.IRON_AXE,ItemsInit.Dwarf_shield);
    public static final DwarfProfessionRecord FARMER = DwarfProfessionRecord.register(reference.Farmer,3,PointOfInterestTypes.FARMER,ImmutableSet.of(Items.WHEAT,Items.WHEAT_SEEDS,Items.BEETROOT_SEEDS,Items.BONE_MEAL),ImmutableSet.of(Blocks.FARMLAND),SoundEvents.ENTITY_VILLAGER_WORK_FARMER, Items.IRON_AXE,ItemStack.EMPTY.getItem());
    public static final DwarfProfessionRecord Slayer = DwarfProfessionRecord.register(reference.Slayer, 4, PointOfInterestTypes.BUTCHER, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER, Items.IRON_AXE, Items.IRON_AXE);
    public static final DwarfProfessionRecord Lord = DwarfProfessionRecord.register(reference.Lord,5,PointOfInterestType.NONE,PointOfInterestType.NONE,null,ItemsInit.GreatPick,ItemStack.EMPTY.getItem());


    private static DwarfProfessionRecord register(String id,int ID, RegistryKey<PointOfInterestType> heldWorkstation, @Nullable SoundEvent workSound,Item mainhainditem) {
        return DwarfProfessionRecord.register(id,ID, entry -> entry.matchesKey(heldWorkstation), entry -> entry.matchesKey(heldWorkstation), workSound,mainhainditem, ItemStack.EMPTY.getItem());
    }

    private static DwarfProfessionRecord register(String id,int ID, RegistryKey<PointOfInterestType> heldWorkstation, @Nullable SoundEvent workSound,Item mainhainditem,Item offhanditem) {
        return DwarfProfessionRecord.register(id,ID, entry -> entry.matchesKey(heldWorkstation), entry -> entry.matchesKey(heldWorkstation), workSound,mainhainditem,offhanditem);
    }

    private static DwarfProfessionRecord register(String id,int ID, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, @Nullable SoundEvent workSound,Item mainhainditem,Item offhanditem) {
        return DwarfProfessionRecord.register(id,ID, heldWorkstation, acquirableWorkstation, ImmutableSet.of(), ImmutableSet.of(), workSound,mainhainditem,offhanditem);
    }

    private static DwarfProfessionRecord register(String id,int ID, RegistryKey<PointOfInterestType> heldWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound,Item mainhainditem,Item offhanditem) {
        return DwarfProfessionRecord.register(id,ID, entry -> entry.matchesKey(heldWorkstation), entry -> entry.matchesKey(heldWorkstation), gatherableItems, secondaryJobSites, workSound,mainhainditem,offhanditem);
    }

    private static DwarfProfessionRecord register(String id,int ID, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound,Item itemmain,Item itemoff) {
        return Registry.register(WHRegistry.DWARF_PROFESSIONS, Identifier.of(reference.modid,id), new DwarfProfessionRecord(id,ID, heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound,itemmain,itemoff));
    }
}
