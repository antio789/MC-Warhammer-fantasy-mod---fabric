package warhammermod.utils.Registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import warhammermod.Items.melee.*;
import warhammermod.Items.melee.specials.Ghal_Maraz;
import warhammermod.Items.melee.specials.Great_pick;
import warhammermod.Items.ranged.*;
import warhammermod.utils.reference;

import static net.minecraft.component.type.ConsumableComponents.food;

public class ItemsInit {
    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, Modgroup);
        ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY).register(itemgroup -> {
            itemgroup.add(BEER);

            itemgroup.add(Cartridge);
            itemgroup.add(Warpstone);
            itemgroup.add(Shotshell);
            itemgroup.add(Grenade);

            itemgroup.add(netherite_warhammer);
            itemgroup.add(diamond_warhammer);
            itemgroup.add(gold_warhammer);
            itemgroup.add(iron_warhammer);
            itemgroup.add(stone_warhammer);
            itemgroup.add(wooden_warhammer);

            itemgroup.add(netherite_dagger);
            itemgroup.add(diamond_dagger);
            itemgroup.add(gold_dagger);
            itemgroup.add(iron_dagger);
            itemgroup.add(stone_dagger);
            itemgroup.add(wooden_dagger);

            itemgroup.add(netherite_spear);
            itemgroup.add(diamond_spear);
            itemgroup.add(gold_spear);
            itemgroup.add(iron_spear);
            itemgroup.add(stone_spear);
            itemgroup.add(wooden_spear);

            itemgroup.add(netherite_halberd);
            itemgroup.add(diamond_halberd);
            itemgroup.add(gold_halberd);
            itemgroup.add(iron_halberd);
            itemgroup.add(stone_halberd);
            itemgroup.add(wooden_halberd);

            itemgroup.add(GreatPick);
            itemgroup.add(GHAL_MARAZ);

            itemgroup.add(DIAMOND_CHAINMAIL_BOOTS);
            itemgroup.add(DIAMOND_CHAINMAIL_HELMET);
            itemgroup.add(DIAMOND_CHAINMAIL_CHESTPLATE);
            itemgroup.add(DIAMOND_CHAINMAIL_LEGGINGS);

            itemgroup.add(musket);
            itemgroup.add(pistol);
            itemgroup.add(repeater_handgun);
            itemgroup.add(thunderer_handgun);
            itemgroup.add(blunderbuss);
            itemgroup.add(GrudgeRaker);

            itemgroup.add(grenade_launcher);
            itemgroup.add(DrakeGun);
            itemgroup.add(RatlingGun);

            itemgroup.add(Warplock_jezzail);
            itemgroup.add(Sling);

            itemgroup.add(High_Elf_Shield);
            itemgroup.add(Dark_Elf_Shield);
            itemgroup.add(Skaven_shield);
            itemgroup.add(Skaven_shield);
            itemgroup.add(Dwarf_shield);
            itemgroup.add(Imperial_shield);

            itemgroup.add(Entityinit.DWARF_SPAWN_EGG);
            itemgroup.add(Entityinit.SKAVEN_SPAWN_EGG);
            itemgroup.add(Entityinit.Pegasus_SPAWN_EGG);
        });
    }
    //FOOD
    public static final ConsumableComponent BEER_ConsumableEffect = food()
            .consumeEffect(
                    new ApplyEffectsConsumeEffect(
                            new StatusEffectInstance(StatusEffects.REGENERATION, 250, 1), 0.8F))
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.STRENGTH,120,0),0.6F))
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.NAUSEA,220,1),0.18F))
            .build();
    public static FoodComponent Beer_FoodComponent = new FoodComponent.Builder().nutrition(4).saturationModifier(0.4F).alwaysEdible().build();
    public static Item BEER = register(new Item(new Item.Settings().rarity(Rarity.UNCOMMON).food(Beer_FoodComponent, BEER_ConsumableEffect).maxCount(16)),"beer");
//basic items
    public static Item Cartridge = register(new Item(new Item.Settings()),"cartridge");
    public static Item Warpstone = register(new Item(new Item.Settings().rarity(Rarity.UNCOMMON)),"warpstone");
    public static Item Shotshell = register(new Item(new Item.Settings()),"shotshell");
    public static Item Grenade= register(new Item(new Item.Settings()),"grenade");
//melee weapons
    public static final Item netherite_warhammer = register(new HammerTemplate(ToolMaterial.NETHERITE,new Item.Settings()),"netherite_warhammer");
    public static final Item diamond_warhammer = register(new HammerTemplate(ToolMaterial.DIAMOND,new Item.Settings()),"diamond_warhammer");
    public static final Item wooden_warhammer = register(new HammerTemplate(ToolMaterial.WOOD,new Item.Settings()),"wooden_warhammer");
    public static final Item stone_warhammer = register(new HammerTemplate(ToolMaterial.STONE,new Item.Settings()),"stone_warhammer");
    public static final Item iron_warhammer = register(new HammerTemplate(ToolMaterial.IRON,new Item.Settings()),"iron_warhammer");
    public static final Item gold_warhammer = register(new HammerTemplate(ToolMaterial.GOLD,new Item.Settings()),"gold_warhammer");

    public static final Item netherite_dagger = register(new SwordItem(ToolMaterial.NETHERITE,0.5f,5,new Item.Settings()),"netherite_dagger");
    public static final Item diamond_dagger = register(new SwordItem(ToolMaterial.DIAMOND,0.5f,5,new Item.Settings()),"diamond_dagger");
    public static final Item wooden_dagger = register(new SwordItem(ToolMaterial.WOOD,0.5f,5,new Item.Settings()),"wooden_dagger");
    public static final Item stone_dagger = register(new SwordItem(ToolMaterial.STONE,0.5f,5,new Item.Settings()),"stone_dagger");
    public static final Item iron_dagger = register(new SwordItem(ToolMaterial.IRON,0.5f,5,new Item.Settings()),"iron_dagger");
    public static final Item gold_dagger = register(new SwordItem(ToolMaterial.GOLD,0.5f,5,new Item.Settings()),"gold_dagger");

    public static final Item netherite_halberd = register(new HalberdTemplate(ToolMaterial.NETHERITE,new Item.Settings()),"netherite_halberd");
    public static final Item diamond_halberd = register(new HalberdTemplate(ToolMaterial.DIAMOND,new Item.Settings()),"diamond_halberd");
    public static final Item wooden_halberd = register(new HalberdTemplate(ToolMaterial.WOOD,new Item.Settings()),"wooden_halberd");
    public static final Item stone_halberd = register(new HalberdTemplate(ToolMaterial.STONE,new Item.Settings()),"stone_halberd");
    public static final Item iron_halberd = register(new HalberdTemplate(ToolMaterial.IRON,new Item.Settings()),"iron_halberd");
    public static final Item gold_halberd = register(new HalberdTemplate(ToolMaterial.GOLD,new Item.Settings()),"gold_halberd");

    public static final Item netherite_spear = register(new SpearTemplate(ToolMaterial.NETHERITE,new Item.Settings()),"netherite_spear");
    public static final Item diamond_spear = register(new SpearTemplate(ToolMaterial.DIAMOND,new Item.Settings()),"diamond_spear");
    public static final Item wooden_spear = register(new SpearTemplate(ToolMaterial.WOOD,new Item.Settings()),"wooden_spear");
    public static final Item stone_spear = register(new SpearTemplate(ToolMaterial.STONE,new Item.Settings()),"stone_spear");
    public static final Item iron_spear = register(new SpearTemplate(ToolMaterial.IRON,new Item.Settings()),"iron_spear");
    public static final Item gold_spear = register(new SpearTemplate(ToolMaterial.GOLD,new Item.Settings()),"gold_spear");

    public static final Item GreatPick = register(new Great_pick(new Item.Settings()),"Great_Pick");
    public static final Item GHAL_MARAZ = register(new Ghal_Maraz(new Item.Settings().rarity(Rarity.EPIC).maxDamage((int)(ToolMaterial.NETHERITE.durability()*1.3F))),"GHAL_MARAZ");


    public static final Item DIAMOND_CHAINMAIL_HELMET =  register(new ArmorItem(ModArmorMaterials.DIAMOND_CHAIN, EquipmentType.HELMET, new Item.Settings()),"DIAMOND_CHAINMAIL_HELMET");
    public static final Item DIAMOND_CHAINMAIL_CHESTPLATE =  register(new ArmorItem(ModArmorMaterials.DIAMOND_CHAIN, EquipmentType.CHESTPLATE, new Item.Settings()),"DIAMOND_CHAINMAIL_CHESTPLATE");
    public static final Item DIAMOND_CHAINMAIL_LEGGINGS = register(new ArmorItem(ModArmorMaterials.DIAMOND_CHAIN, EquipmentType.LEGGINGS, new Item.Settings()),"DIAMOND_CHAINMAIL_LEGGINGS");
    public static final Item DIAMOND_CHAINMAIL_BOOTS =  register(new ArmorItem(ModArmorMaterials.DIAMOND_CHAIN, EquipmentType.BOOTS, new Item.Settings()),"DIAMOND_CHAINMAIL_BOOTS");

    public static Item musket = register(new GunTemplate(new Item.Settings().maxDamage(420), Cartridge,40,1,13),"musket");
    public static Item pistol = register(new GunTemplate(new Item.Settings().maxDamage(384), Cartridge,25,1,8),"pistol");
    public static Item repeater_handgun = register(new Gun3DTemplate(new Item.Settings().maxDamage(500), Cartridge,92,6,13),"nuln_repeater_handgun");
    public static Item thunderer_handgun = register(new GunTemplate(new Item.Settings().maxDamage(540), Cartridge,35,1,16),"thunderer_handgun");

    public static Item blunderbuss = register(new ShotgunTemplate(new Item.Settings().maxDamage(390), Shotshell,40,1,19),"blunderbuss");
    public static Item GrudgeRaker = register(new ShotgunTemplate(new Item.Settings().maxDamage(450), Shotshell,50,2,19),"GrudgeRaker");

    public static Item grenade_launcher = register(new GrenadeTemplate(new Item.Settings().maxDamage(384), Grenade,75,1),"grenade_launcher");

    public static Item DrakeGun = register(new DrakeGunTemplate(new Item.Settings().maxDamage(210),32,40),"DrakeGun");
    public static Item RatlingGun = register(new RatlingGun(new Item.Settings().rarity(Rarity.RARE).maxDamage(210),64,80),"Ratling_Gun");

    public static Item Warplock_jezzail = register(new WarpgunTemplate(new Item.Settings().maxDamage(384), Warpstone,50,1,14),"Warplock_jezzail");

    public static Item Sling = register(new SlingTemplate(new Item.Settings().maxDamage(180)),"Sling");

    public static Item High_Elf_Shield= register(new ShieldTemplate(new Item.Settings()),"High_Elf_Shield");
    public static Item Dark_Elf_Shield = register(new ShieldTemplate(new Item.Settings()),"Dark_Elf_Shield");

    public static Item Dwarf_shield= register(new SmallShieldTemplate(new Item.Settings()),"Dwarf_shield");
    public static Item Imperial_shield= register(new SmallShieldTemplate(new Item.Settings()),"Imperial_shield");
    public static Item Skaven_shield= register(new SmallShieldTemplate(new Item.Settings()),"Skaven_shield");



    //public static final Item Great_pick = new Great_pick(new Item.Properties().setNoRepair().tab(reference.warhammer));
    //public static final Item Ghal_Maraz = new Ghal_Maraz(new Item.Properties().setNoRepair().durability((int)(Tiers.NETHERITE.getUses()*1.3)));

    public static Item iron_gunsword = register(new GunSwordTemplate(ToolMaterial.IRON,25,1,8),"iron_gunsword");
    public static Item diamond_gunsword = register(new GunSwordTemplate(ToolMaterial.DIAMOND,25,1,8),"diamond_gunsword");
/*
    public static Item register(Item item, String id) {
        return Registry.register(Registries.ITEM, Identifier.of(reference.modid, id.toLowerCase()), item);
    }
*/
    public static Item register(Item item, String id) {
        // Create the registry key for the item
        Identifier identifier = Identifier.of(reference.modid, id.toLowerCase());
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, identifier);


        // Set the registry key for the item
        Item.Settings settings = new Item.Settings().registryKey(key);

        // Register the item with its registry key
        return Registry.register(Registries.ITEM, key, item);
    }



    public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(reference.modid, "item_group"));
    private static final ItemGroup Modgroup = FabricItemGroup.builder()
            .icon(() -> new ItemStack(GHAL_MARAZ))
            .displayName(Text.translatable("itemGroup.warhammermod.warhammer"))
            .build();
}
