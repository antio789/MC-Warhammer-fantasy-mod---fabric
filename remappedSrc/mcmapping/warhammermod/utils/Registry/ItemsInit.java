package warhammermod.utils.Registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import warhammermod.Items.melee.*;
import warhammermod.Items.melee.specials.Ghal_Maraz;
import warhammermod.Items.melee.specials.Great_pick;
import warhammermod.Items.ranged.*;
import warhammermod.utils.reference;

public class ItemsInit {
    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CUSTOM_ITEM_GROUP_KEY, Modgroup);
        ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY).register(itemgroup -> {
            itemgroup.accept(BEER);

            itemgroup.accept(Cartridge);
            itemgroup.accept(Warpstone);
            itemgroup.accept(Shotshell);
            itemgroup.accept(Grenade);

            itemgroup.accept(netherite_warhammer);
            itemgroup.accept(diamond_warhammer);
            itemgroup.accept(gold_warhammer);
            itemgroup.accept(iron_warhammer);
            itemgroup.accept(stone_warhammer);
            itemgroup.accept(wooden_warhammer);

            itemgroup.accept(netherite_dagger);
            itemgroup.accept(diamond_dagger);
            itemgroup.accept(gold_dagger);
            itemgroup.accept(iron_dagger);
            itemgroup.accept(stone_dagger);
            itemgroup.accept(wooden_dagger);

            itemgroup.accept(netherite_spear);
            itemgroup.accept(diamond_spear);
            itemgroup.accept(gold_spear);
            itemgroup.accept(iron_spear);
            itemgroup.accept(stone_spear);
            itemgroup.accept(wooden_spear);

            itemgroup.accept(netherite_halberd);
            itemgroup.accept(diamond_halberd);
            itemgroup.accept(gold_halberd);
            itemgroup.accept(iron_halberd);
            itemgroup.accept(stone_halberd);
            itemgroup.accept(wooden_halberd);

            itemgroup.accept(GreatPick);
            itemgroup.accept(GHAL_MARAZ);

            itemgroup.accept(DIAMOND_CHAINMAIL_BOOTS);
            itemgroup.accept(DIAMOND_CHAINMAIL_HELMET);
            itemgroup.accept(DIAMOND_CHAINMAIL_CHESTPLATE);
            itemgroup.accept(DIAMOND_CHAINMAIL_LEGGINGS);

            itemgroup.accept(musket);
            itemgroup.accept(pistol);
            itemgroup.accept(repeater_handgun);
            itemgroup.accept(thunderer_handgun);
            itemgroup.accept(blunderbuss);
            itemgroup.accept(GrudgeRaker);

            itemgroup.accept(grenade_launcher);
            itemgroup.accept(DrakeGun);
            itemgroup.accept(RatlingGun);

            itemgroup.accept(Warplock_jezzail);
            itemgroup.accept(Sling);

            itemgroup.accept(High_Elf_Shield);
            itemgroup.accept(Dark_Elf_Shield);
            itemgroup.accept(Skaven_shield);
            itemgroup.accept(Skaven_shield);
            itemgroup.accept(Dwarf_shield);
            itemgroup.accept(Imperial_shield);

            itemgroup.accept(Entityinit.DWARF_SPAWN_EGG);
            itemgroup.accept(Entityinit.SKAVEN_SPAWN_EGG);
            itemgroup.accept(Entityinit.Pegasus_SPAWN_EGG);
        });
    }
    //FOOD
    public static FoodProperties BEER_EFFECT= new FoodProperties.Builder().nutrition(4).saturationModifier(0.4F).effect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1), 0.8F).effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 120, 0), 0.6F).effect(new MobEffectInstance(MobEffects.CONFUSION, 220, 1), 0.18F).alwaysEdible().build();
    public static Item BEER = register(new Item(new Item.Properties().rarity(Rarity.RARE).food(BEER_EFFECT).stacksTo(16)),"beer");
//basic items
    public static Item Cartridge = register(new Item(new Item.Properties()),"cartridge");
    public static Item Warpstone = register(new Item(new Item.Properties()),"warpstone");
    public static Item Shotshell = register(new Item(new Item.Properties()),"shotshell");
    public static Item Grenade= register(new Item(new Item.Properties()),"grenade");
//melee weapons
    public static final Item netherite_warhammer = register(new HammerTemplate(Tiers.NETHERITE,new Item.Properties()),"netherite_warhammer");
    public static final Item diamond_warhammer = register(new HammerTemplate(Tiers.DIAMOND,new Item.Properties()),"diamond_warhammer");
    public static final Item wooden_warhammer = register(new HammerTemplate(Tiers.WOOD,new Item.Properties()),"wooden_warhammer");
    public static final Item stone_warhammer = register(new HammerTemplate(Tiers.STONE,new Item.Properties()),"stone_warhammer");
    public static final Item iron_warhammer = register(new HammerTemplate(Tiers.IRON,new Item.Properties()),"iron_warhammer");
    public static final Item gold_warhammer = register(new HammerTemplate(Tiers.GOLD,new Item.Properties()),"gold_warhammer");

    public static final Item netherite_dagger = register(new DaggerTemplate(Tiers.NETHERITE,new Item.Properties().attributes(DaggerTemplate.createAttributeModifiers(Tiers.NETHERITE,0.5F,5))),"netherite_dagger");
    public static final Item diamond_dagger = register(new DaggerTemplate(Tiers.DIAMOND,new Item.Properties().attributes(DaggerTemplate.createAttributeModifiers(Tiers.DIAMOND,0.5F,5))),"diamond_dagger");
    public static final Item wooden_dagger = register(new DaggerTemplate(Tiers.WOOD,new Item.Properties().attributes(DaggerTemplate.createAttributeModifiers(Tiers.WOOD,0.5F,5))),"wooden_dagger");
    public static final Item stone_dagger = register(new DaggerTemplate(Tiers.STONE,new Item.Properties().attributes(DaggerTemplate.createAttributeModifiers(Tiers.STONE,0.5F,5))),"stone_dagger");
    public static final Item iron_dagger = register(new DaggerTemplate(Tiers.IRON,new Item.Properties().attributes(DaggerTemplate.createAttributeModifiers(Tiers.IRON,0.5F,5))),"iron_dagger");
    public static final Item gold_dagger = register(new DaggerTemplate(Tiers.GOLD,new Item.Properties().attributes(DaggerTemplate.createAttributeModifiers(Tiers.GOLD,0.5F,5))),"gold_dagger");

    public static final Item netherite_halberd = register(new HalberdTemplate(Tiers.NETHERITE,new Item.Properties()),"netherite_halberd");
    public static final Item diamond_halberd = register(new HalberdTemplate(Tiers.DIAMOND,new Item.Properties()),"diamond_halberd");
    public static final Item wooden_halberd = register(new HalberdTemplate(Tiers.WOOD,new Item.Properties()),"wooden_halberd");
    public static final Item stone_halberd = register(new HalberdTemplate(Tiers.STONE,new Item.Properties()),"stone_halberd");
    public static final Item iron_halberd = register(new HalberdTemplate(Tiers.IRON,new Item.Properties()),"iron_halberd");
    public static final Item gold_halberd = register(new HalberdTemplate(Tiers.GOLD,new Item.Properties()),"gold_halberd");

    public static final Item netherite_spear = register(new SpearTemplate(Tiers.NETHERITE,new Item.Properties()),"netherite_spear");
    public static final Item diamond_spear = register(new SpearTemplate(Tiers.DIAMOND,new Item.Properties()),"diamond_spear");
    public static final Item wooden_spear = register(new SpearTemplate(Tiers.WOOD,new Item.Properties()),"wooden_spear");
    public static final Item stone_spear = register(new SpearTemplate(Tiers.STONE,new Item.Properties()),"stone_spear");
    public static final Item iron_spear = register(new SpearTemplate(Tiers.IRON,new Item.Properties()),"iron_spear");
    public static final Item gold_spear = register(new SpearTemplate(Tiers.GOLD,new Item.Properties()),"gold_spear");

    public static final Item GreatPick = register(new Great_pick(new Item.Properties()),"Great_Pick");
    public static final Item GHAL_MARAZ = register(new Ghal_Maraz(new Item.Properties().rarity(Rarity.EPIC).durability((int)(Tiers.NETHERITE.getUses()*1.3F))),"GHAL_MARAZ");


    public static final Item DIAMOND_CHAINMAIL_HELMET =  register(new ArmorItem(ModArmorMaterial.DIAMOND_CHAINMAIL, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(31))),"DIAMOND_CHAINMAIL_HELMET");
    public static final Item DIAMOND_CHAINMAIL_CHESTPLATE =  register(new ArmorItem(ModArmorMaterial.DIAMOND_CHAINMAIL, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(31))),"DIAMOND_CHAINMAIL_CHESTPLATE");
    public static final Item DIAMOND_CHAINMAIL_LEGGINGS = register(new ArmorItem(ModArmorMaterial.DIAMOND_CHAINMAIL, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(31))),"DIAMOND_CHAINMAIL_LEGGINGS");
    public static final Item DIAMOND_CHAINMAIL_BOOTS =  register(new ArmorItem(ModArmorMaterial.DIAMOND_CHAINMAIL, ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(31))),"DIAMOND_CHAINMAIL_BOOTS");

    public static Item musket = register(new GunTemplate(new Item.Properties().durability(420), Cartridge,40,1,13),"musket");
    public static Item pistol = register(new GunTemplate(new Item.Properties().durability(384), Cartridge,25,1,8),"pistol");
    public static Item repeater_handgun = register(new Gun3DTemplate(new Item.Properties().durability(500), Cartridge,92,6,13),"nuln_repeater_handgun");
    public static Item thunderer_handgun = register(new GunTemplate(new Item.Properties().durability(540), Cartridge,35,1,16),"thunderer_handgun");

    public static Item blunderbuss = register(new ShotgunTemplate(new Item.Properties().durability(390), Shotshell,40,1,19),"blunderbuss");
    public static Item GrudgeRaker = register(new ShotgunTemplate(new Item.Properties().durability(450), Shotshell,50,2,19),"GrudgeRaker");

    public static Item grenade_launcher = register(new GrenadeTemplate(new Item.Properties().durability(384), Grenade,75,1),"grenade_launcher");

    public static Item DrakeGun = register(new DrakeGunTemplate(new Item.Properties().durability(210),32,40),"DrakeGun");
    public static Item RatlingGun = register(new RatlingGun(new Item.Properties().durability(210),64,80),"Ratling_Gun");

    public static Item Warplock_jezzail = register(new WarpgunTemplate(new Item.Properties().durability(384), Warpstone,50,1,14),"Warplock_jezzail");

    public static Item Sling = register(new SlingTemplate(new Item.Properties().durability(180)),"Sling");

    public static Item High_Elf_Shield= register(new ShieldTemplate(new Item.Properties()),"High_Elf_Shield");
    public static Item Dark_Elf_Shield = register(new ShieldTemplate(new Item.Properties()),"Dark_Elf_Shield");

    public static Item Dwarf_shield= register(new SmallShieldTemplate(new Item.Properties()),"Dwarf_shield");
    public static Item Imperial_shield= register(new SmallShieldTemplate(new Item.Properties()),"Imperial_shield");
    public static Item Skaven_shield= register(new SmallShieldTemplate(new Item.Properties()),"Skaven_shield");



    //public static final Item Great_pick = new Great_pick(new Item.Properties().setNoRepair().tab(reference.warhammer));
    //public static final Item Ghal_Maraz = new Ghal_Maraz(new Item.Properties().setNoRepair().durability((int)(Tiers.NETHERITE.getUses()*1.3)));

    public static Item iron_gunsword = register(new GunSwordTemplate(Tiers.IRON,25,1,8),"iron_gunsword");
    public static Item diamond_gunsword = register(new GunSwordTemplate(Tiers.DIAMOND,25,1,8),"diamond_gunsword");

    public static Item register(Item item, String id) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(reference.modid, id.toLowerCase()), item);
    }


    public static final ResourceKey<CreativeModeTab> CUSTOM_ITEM_GROUP_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(reference.modid, "item_group"));
    private static final CreativeModeTab Modgroup = FabricItemGroup.builder()
            .icon(() -> new ItemStack(GHAL_MARAZ))
            .title(Component.translatable("itemGroup.warhammermod.warhammer"))
            .build();
}
