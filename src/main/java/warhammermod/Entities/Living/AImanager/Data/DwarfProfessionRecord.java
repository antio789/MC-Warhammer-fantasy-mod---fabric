package warhammermod.Entities.Living.AImanager.Data;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;

import java.util.function.Predicate;

public record DwarfProfessionRecord(String name, int ID, Predicate<Holder<PoiType>> heldWorkstation, Predicate<Holder<PoiType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, SoundEvent workSound, Item mainhainditem, Item offhanditem) {
    public static final TagKey<PoiType> ACQUIRABLE_JOB_SITE = TagKey.create(Registries.POINT_OF_INTEREST_TYPE, reference.location("poi"));

    public static final DwarfProfessionRecord Warrior = register(reference.Warrior, 6, poiType -> false, holder -> holder.is(ACQUIRABLE_JOB_SITE), null, Items.IRON_AXE, ItemsInit.Dwarf_shield);
    public static final DwarfProfessionRecord Miner = register(reference.Miner, 0, PoiTypes.ARMORER, SoundEvents.VILLAGER_WORK_ARMORER, Items.IRON_PICKAXE);
    public static final DwarfProfessionRecord Builder = register(reference.Builder, 1, PoiTypes.MASON, SoundEvents.VILLAGER_WORK_MASON, ItemsInit.iron_warhammer);
    public static final DwarfProfessionRecord Engineer = register(reference.Engineer, 2, PoiTypes.TOOLSMITH, SoundEvents.VILLAGER_WORK_TOOLSMITH, Items.IRON_AXE, ItemsInit.Dwarf_shield);
    public static final DwarfProfessionRecord FARMER = register(reference.Farmer, 3, PoiTypes.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(net.minecraft.world.level.block.Blocks.FARMLAND), SoundEvents.VILLAGER_WORK_FARMER, Items.IRON_AXE, ItemStack.EMPTY.getItem());
    public static final DwarfProfessionRecord Slayer = register(reference.Slayer, 4, PoiTypes.BUTCHER, SoundEvents.VILLAGER_WORK_BUTCHER, Items.IRON_AXE, Items.IRON_AXE);
    public static final DwarfProfessionRecord Lord = register(reference.Lord, 5, poiType -> false, poiType -> false, null, ItemsInit.GreatPick, ItemStack.EMPTY.getItem());

    private static DwarfProfessionRecord register(String id, int ID, ResourceKey<PoiType> heldWorkstation, SoundEvent workSound, Item mainhainditem) {
        return register(id, ID, entry -> entry.is(heldWorkstation), entry -> entry.is(heldWorkstation), workSound, mainhainditem, ItemStack.EMPTY.getItem());
    }

    private static DwarfProfessionRecord register(String id, int ID, ResourceKey<PoiType> heldWorkstation, SoundEvent workSound, Item mainhainditem, Item offhanditem) {
        return register(id, ID, entry -> entry.is(heldWorkstation), entry -> entry.is(heldWorkstation), workSound, mainhainditem, offhanditem);
    }

    private static DwarfProfessionRecord register(String id, int ID, Predicate<Holder<PoiType>> heldWorkstation, Predicate<Holder<PoiType>> acquirableWorkstation, SoundEvent workSound, Item mainhainditem, Item offhanditem) {
        return register(id, ID, heldWorkstation, acquirableWorkstation, ImmutableSet.of(), ImmutableSet.of(), workSound, mainhainditem, offhanditem);
    }

    private static DwarfProfessionRecord register(String id, int ID, ResourceKey<PoiType> heldWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, SoundEvent workSound, Item mainhainditem, Item offhanditem) {
        return register(id, ID, entry -> entry.is(heldWorkstation), entry -> entry.is(heldWorkstation), gatherableItems, secondaryJobSites, workSound, mainhainditem, offhanditem);
    }

    private static DwarfProfessionRecord register(String id, int ID, Predicate<Holder<PoiType>> heldWorkstation, Predicate<Holder<PoiType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, SoundEvent workSound, Item itemmain, Item itemoff) {
        return Registry.register(WHRegistry.DWARF_PROFESSIONS, reference.location(id), new DwarfProfessionRecord(id, ID, heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound, itemmain, itemoff));
    }

    public static void initialize() {
    }
}
