package warhammermod.Items;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import warhammermod.Items.melee.DaggerTemplate;
import warhammermod.Items.melee.HalberdTemplate;
import warhammermod.Items.melee.HammerTemplate;
import warhammermod.Items.melee.ShieldTemplate;
import warhammermod.Items.melee.SmallShieldTemplate;
import warhammermod.Items.melee.specials.Ghal_Maraz;
import warhammermod.Items.melee.specials.Great_pick;
import warhammermod.Items.ranged.DrakeGunTemplate;
import warhammermod.Items.ranged.Gun3DTemplate;
import warhammermod.Items.ranged.GrenadeTemplate;
import warhammermod.Items.ranged.GunSwordTemplate;
import warhammermod.Items.ranged.GunTemplate;
import warhammermod.Items.ranged.RatlingGun;
import warhammermod.Items.ranged.ShotgunTemplate;
import warhammermod.Items.ranged.SlingTemplate;
import warhammermod.Items.ranged.WarpgunTemplate;
import warhammermod.utils.reference;

import java.util.List;
import java.util.function.Function;

public class ItemsInit {
    public static final ResourceKey<CreativeModeTab> WARHAMMER_TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, reference.location("warhammer"));

    //food
    public static final Item BEER = register("beer", properties -> new Item(properties.stacksTo(16).rarity(Rarity.UNCOMMON)
            .food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.7F).alwaysEdible().build(),
                    Consumable.builder()
                            .consumeSeconds(1.6F)
                            .onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
                                    new MobEffectInstance(MobEffects.REGENERATION, 400, 1),
                                    new MobEffectInstance(MobEffects.STRENGTH, 120, 0),
                                    new MobEffectInstance(MobEffects.NAUSEA, 220, 1))))
                            .build())));

    //basic items
    public static final Item Cartridge = register("cartridge", Item::new);
    public static final Item Warpstone = register("warpstone", properties -> new Item(properties.rarity(Rarity.UNCOMMON)));
    public static final Item shotShell = register("shotshell", Item::new);
    public static final Item Grenade = register("grenade", Item::new);

    //melee weapons
    public static final Item netherite_warhammer = register("netherite_warhammer", properties -> new HammerTemplate(ToolMaterial.NETHERITE, properties, 9.5F, -2.9F));
    public static final Item diamond_warhammer = register("diamond_warhammer", properties -> new HammerTemplate(ToolMaterial.DIAMOND, properties, 8.5F, -2.9F));
    public static final Item wooden_warhammer = register("wooden_warhammer", properties -> new HammerTemplate(ToolMaterial.WOOD, properties, 4.5F, -2.9F));
    public static final Item stone_warhammer = register("stone_warhammer", properties -> new HammerTemplate(ToolMaterial.STONE, properties, 5.5F, -2.9F));
    public static final Item iron_warhammer = register("iron_warhammer", properties -> new HammerTemplate(ToolMaterial.IRON, properties, 7.5F, -2.9F));
    public static final Item gold_warhammer = register("gold_warhammer", properties -> new HammerTemplate(ToolMaterial.GOLD, properties, 6.5F, -2.9F));

    public static final Item netherite_dagger = register("netherite_dagger", properties -> new DaggerTemplate(ToolMaterial.NETHERITE, properties, 3.5F, 5.0F));
    public static final Item diamond_dagger = register("diamond_dagger", properties -> new DaggerTemplate(ToolMaterial.DIAMOND, properties, 3.0F, 5.0F));
    public static final Item wooden_dagger = register("wooden_dagger", properties -> new DaggerTemplate(ToolMaterial.WOOD, properties, 1.0F, 5.0F));
    public static final Item stone_dagger = register("stone_dagger", properties -> new DaggerTemplate(ToolMaterial.STONE, properties, 1.5F, 5.0F));
    public static final Item iron_dagger = register("iron_dagger", properties -> new DaggerTemplate(ToolMaterial.IRON, properties, 2.5F, 5.0F));
    public static final Item gold_dagger = register("gold_dagger", properties -> new DaggerTemplate(ToolMaterial.GOLD, properties, 2.0F, 5.0F));

    public static final Item netherite_halberd = register("netherite_halberd", properties -> new HalberdTemplate(ToolMaterial.NETHERITE, properties, 7.5F, -2.8F));
    public static final Item diamond_halberd = register("diamond_halberd", properties -> new HalberdTemplate(ToolMaterial.DIAMOND, properties, 6.5F, -2.8F));
    public static final Item wooden_halberd = register("wooden_halberd", properties -> new HalberdTemplate(ToolMaterial.WOOD, properties, 2.5F, -2.8F));
    public static final Item stone_halberd = register("stone_halberd", properties -> new HalberdTemplate(ToolMaterial.STONE, properties, 3.5F, -2.8F));
    public static final Item iron_halberd = register("iron_halberd", properties -> new HalberdTemplate(ToolMaterial.IRON, properties, 5.5F, -2.8F));
    public static final Item gold_halberd = register("gold_halberd", properties -> new HalberdTemplate(ToolMaterial.GOLD, properties, 4.5F, -2.8F));

    public static final Item GreatPick = register("war_pick", properties -> new Great_pick(ToolMaterial.DIAMOND, properties, 5.5F, -2.6F));
    public static final Item GHAL_MARAZ = register("ghal_maraz", properties -> new Ghal_Maraz(ToolMaterial.NETHERITE, properties.rarity(Rarity.EPIC).durability((int) (ToolMaterial.NETHERITE.durability() * 1.3F)), 10.0F, -2.4F));

    //ranged weapons
    public static final Item musket = register("musket", properties -> new GunTemplate(properties.durability(420), Cartridge, 40, 1, 13));
    public static final Item pistol = register("pistol", properties -> new GunTemplate(properties.durability(384), Cartridge, 25, 1, 8));
    public static final Item repeater_handgun = register("nuln_repeater_handgun", properties -> new Gun3DTemplate(properties.durability(500), Cartridge, 92, 6, 13));
    public static final Item thunderer_handgun = register("thunderer_handgun", properties -> new GunTemplate(properties.durability(540), Cartridge, 35, 1, 16));

    public static final Item blunderbuss = register("blunderbuss", properties -> new ShotgunTemplate(properties.durability(390), shotShell, 40, 1, 19));
    public static final Item GrudgeRaker = register("grudgeraker", properties -> new ShotgunTemplate(properties.durability(450), shotShell, 40, 2, 19));

    public static final Item grenade_launcher = register("grenade_launcher", properties -> new GrenadeTemplate(properties.durability(384), Grenade, 75, 1));

    public static final Item DrakeGun = register("drakegun", properties -> new DrakeGunTemplate(properties.durability(210), 64, 40));
    public static final Item RatlingGun = register("ratling_gun", properties -> new RatlingGun(properties.durability(210), 64, 80));

    public static final Item Warplock_jezzail = register("warplock_jezzail", properties -> new WarpgunTemplate(properties.durability(384), Warpstone, 50, 1, 14));

    public static final Item Sling = register("sling", properties -> new SlingTemplate(properties.durability(180)));

    public static final Item iron_gunsword = register("iron_gunsword", properties -> new GunSwordTemplate(ToolMaterial.IRON, properties, 5.5F, -2.4F, 25, 1, 8));
    public static final Item diamond_gunsword = register("diamond_gunsword", properties -> new GunSwordTemplate(ToolMaterial.DIAMOND, properties, 6.5F, -2.4F, 25, 1, 8));

    //armor
    public static final ArmorMaterial DIAMOND_CHAIN = new ArmorMaterial(
            33,
            java.util.Map.of(ArmorType.HELMET, 3, ArmorType.CHESTPLATE, 6, ArmorType.LEGGINGS, 7, ArmorType.BOOTS, 2),
            10,
            net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_CHAIN,
            1.0F,
            0.0F,
            net.minecraft.tags.ItemTags.DIAMOND_TOOL_MATERIALS,
            EquipmentAssets.CHAINMAIL);

    public static final Item DIAMOND_CHAINMAIL_HELMET = register("diamond_chainmail_helmet", properties -> new Item(properties.humanoidArmor(DIAMOND_CHAIN, ArmorType.HELMET)));
    public static final Item DIAMOND_CHAINMAIL_CHESTPLATE = register("diamond_chainmail_chestplate", properties -> new Item(properties.humanoidArmor(DIAMOND_CHAIN, ArmorType.CHESTPLATE)));
    public static final Item DIAMOND_CHAINMAIL_LEGGINGS = register("diamond_chainmail_leggings", properties -> new Item(properties.humanoidArmor(DIAMOND_CHAIN, ArmorType.LEGGINGS)));
    public static final Item DIAMOND_CHAINMAIL_BOOTS = register("diamond_chainmail_boots", properties -> new Item(properties.humanoidArmor(DIAMOND_CHAIN, ArmorType.BOOTS)));

    //shields
    public static final Item High_Elf_Shield = register("high_elf_shield", ShieldTemplate::new);
    public static final Item Dark_Elf_Shield = register("dark_elf_shield", ShieldTemplate::new);
    public static final Item Dwarf_shield = register("dwarf_shield", SmallShieldTemplate::new);
    public static final Item Imperial_shield = register("imperial_shield", SmallShieldTemplate::new);
    public static final Item Skaven_shield = register("skaven_shield", SmallShieldTemplate::new);

    public static void registeritems() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, WARHAMMER_TAB,
                FabricCreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.warhammermod.warhammer"))
                        .icon(() -> new ItemStack(GHAL_MARAZ))
                        .build());
        CreativeModeTabEvents.modifyOutputEvent(WARHAMMER_TAB).register(output -> {
            output.accept(new ItemStack(BEER));
            output.accept(new ItemStack(Cartridge));
            output.accept(new ItemStack(Warpstone));
            output.accept(new ItemStack(shotShell));
            output.accept(new ItemStack(Grenade));

            output.accept(new ItemStack(netherite_warhammer));
            output.accept(new ItemStack(diamond_warhammer));
            output.accept(new ItemStack(gold_warhammer));
            output.accept(new ItemStack(iron_warhammer));
            output.accept(new ItemStack(stone_warhammer));
            output.accept(new ItemStack(wooden_warhammer));

            output.accept(new ItemStack(netherite_dagger));
            output.accept(new ItemStack(diamond_dagger));
            output.accept(new ItemStack(gold_dagger));
            output.accept(new ItemStack(iron_dagger));
            output.accept(new ItemStack(stone_dagger));
            output.accept(new ItemStack(wooden_dagger));

            output.accept(new ItemStack(netherite_halberd));
            output.accept(new ItemStack(diamond_halberd));
            output.accept(new ItemStack(gold_halberd));
            output.accept(new ItemStack(iron_halberd));
            output.accept(new ItemStack(stone_halberd));
            output.accept(new ItemStack(wooden_halberd));

            output.accept(new ItemStack(GreatPick));
            output.accept(new ItemStack(GHAL_MARAZ));

            output.accept(new ItemStack(DIAMOND_CHAINMAIL_HELMET));
            output.accept(new ItemStack(DIAMOND_CHAINMAIL_CHESTPLATE));
            output.accept(new ItemStack(DIAMOND_CHAINMAIL_LEGGINGS));
            output.accept(new ItemStack(DIAMOND_CHAINMAIL_BOOTS));

            output.accept(new ItemStack(musket));
            output.accept(new ItemStack(pistol));
            output.accept(new ItemStack(repeater_handgun));
            output.accept(new ItemStack(thunderer_handgun));
            output.accept(new ItemStack(blunderbuss));
            output.accept(new ItemStack(GrudgeRaker));
            output.accept(new ItemStack(grenade_launcher));
            output.accept(new ItemStack(DrakeGun));
            output.accept(new ItemStack(RatlingGun));
            output.accept(new ItemStack(Warplock_jezzail));
            output.accept(new ItemStack(Sling));

            output.accept(new ItemStack(iron_gunsword));
            output.accept(new ItemStack(diamond_gunsword));

            output.accept(new ItemStack(High_Elf_Shield));
            output.accept(new ItemStack(Dark_Elf_Shield));
            output.accept(new ItemStack(Dwarf_shield));
            output.accept(new ItemStack(Imperial_shield));
            output.accept(new ItemStack(Skaven_shield));

            output.accept(new ItemStack(warhammermod.utils.Registry.Entityinit.SKAVEN_SPAWN_EGG));
            output.accept(new ItemStack(warhammermod.utils.Registry.Entityinit.Pegasus_SPAWN_EGG));
            output.accept(new ItemStack(warhammermod.utils.Registry.Entityinit.DWARF_SPAWN_EGG));
        });
    }

    public static Item register(String id, Function<Item.Properties, Item> factory) {
        Identifier identifier = reference.location(id.toLowerCase());
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, identifier);
        Item item = factory.apply(new Item.Properties().setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }
}
