package warhammermod.Entities.Living.AImanager.Data;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;
import warhammermod.Datageneration.Jobsitetagprovider;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;

import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record DwarfProfessionRecord(String name, int ID, Predicate<Holder<PoiType>> heldWorkstation, Predicate<Holder<PoiType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound, Item mainhainditem, Item offhanditem) {
    public static void initialize(){

    }
    public static final Predicate<Holder<PoiType>> IS_ACQUIRABLE_JOB_SITE = (poiType) -> poiType.is(Jobsitetagprovider.ACQUIRABLE_JOB_SITE);
    public static final DwarfProfessionRecord Warrior = DwarfProfessionRecord.register(reference.Warrior,6,PoiType.NONE,IS_ACQUIRABLE_JOB_SITE,null,Items.IRON_AXE, ItemsInit.Dwarf_shield);
    public static final DwarfProfessionRecord Miner = DwarfProfessionRecord.register(reference.Miner,0,PoiTypes.ARMORER,null,Items.IRON_PICKAXE);
    public static final DwarfProfessionRecord Builder = DwarfProfessionRecord.register(reference.Builder,1,PoiTypes.MASON,SoundEvents.VILLAGER_WORK_MASON,ItemsInit.iron_warhammer);
    public static final DwarfProfessionRecord Engineer = DwarfProfessionRecord.register(reference.Engineer,2,PoiTypes.TOOLSMITH,SoundEvents.VILLAGER_WORK_TOOLSMITH,Items.IRON_AXE,ItemsInit.Dwarf_shield);
    public static final DwarfProfessionRecord FARMER = DwarfProfessionRecord.register(reference.Farmer,3,PoiTypes.FARMER,ImmutableSet.of(Items.WHEAT,Items.WHEAT_SEEDS,Items.BEETROOT_SEEDS,Items.BONE_MEAL),ImmutableSet.of(Blocks.FARMLAND),SoundEvents.VILLAGER_WORK_FARMER, Items.IRON_AXE,ItemStack.EMPTY.getItem());
    public static final DwarfProfessionRecord Slayer = DwarfProfessionRecord.register(reference.Slayer, 4, PoiTypes.BUTCHER, SoundEvents.VILLAGER_WORK_BUTCHER, Items.IRON_AXE, Items.IRON_AXE);
    public static final DwarfProfessionRecord Lord = DwarfProfessionRecord.register(reference.Lord,5,PoiType.NONE,PoiType.NONE,null,ItemsInit.GreatPick,ItemStack.EMPTY.getItem());


    private static DwarfProfessionRecord register(String id,int ID, ResourceKey<PoiType> heldWorkstation, @Nullable SoundEvent workSound,Item mainhainditem) {
        return DwarfProfessionRecord.register(id,ID, entry -> entry.is(heldWorkstation), entry -> entry.is(heldWorkstation), workSound,mainhainditem, ItemStack.EMPTY.getItem());
    }

    private static DwarfProfessionRecord register(String id,int ID, ResourceKey<PoiType> heldWorkstation, @Nullable SoundEvent workSound,Item mainhainditem,Item offhanditem) {
        return DwarfProfessionRecord.register(id,ID, entry -> entry.is(heldWorkstation), entry -> entry.is(heldWorkstation), workSound,mainhainditem,offhanditem);
    }

    private static DwarfProfessionRecord register(String id,int ID, Predicate<Holder<PoiType>> heldWorkstation, Predicate<Holder<PoiType>> acquirableWorkstation, @Nullable SoundEvent workSound,Item mainhainditem,Item offhanditem) {
        return DwarfProfessionRecord.register(id,ID, heldWorkstation, acquirableWorkstation, ImmutableSet.of(), ImmutableSet.of(), workSound,mainhainditem,offhanditem);
    }

    private static DwarfProfessionRecord register(String id,int ID, ResourceKey<PoiType> heldWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound,Item mainhainditem,Item offhanditem) {
        return DwarfProfessionRecord.register(id,ID, entry -> entry.is(heldWorkstation), entry -> entry.is(heldWorkstation), gatherableItems, secondaryJobSites, workSound,mainhainditem,offhanditem);
    }

    private static DwarfProfessionRecord register(String id,int ID, Predicate<Holder<PoiType>> heldWorkstation, Predicate<Holder<PoiType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound,Item itemmain,Item itemoff) {
        return Registry.register(WHRegistry.DWARF_PROFESSIONS, ResourceLocation.fromNamespaceAndPath(reference.modid,id), new DwarfProfessionRecord(id,ID, heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound,itemmain,itemoff));
    }
}
