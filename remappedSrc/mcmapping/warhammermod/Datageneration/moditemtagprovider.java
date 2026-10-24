package warhammermod.Datageneration;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import warhammermod.Items.melee.specials.Great_pick;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.reference;

import java.util.concurrent.CompletableFuture;

public class moditemtagprovider extends FabricTagProvider.ItemTagProvider {

    /**
     * Constructs a new {@link FabricTagProvider} with the default computed path.
     *
     * <p>Common implementations of this class are provided.
     *
     * @param output           the {@link FabricDataOutput} instance
     *
     * @param registriesFuture the backing registry for the tag type
     */

    private static final TagKey<Item> GRENADE = TagKey.create(Registries.ITEM,ResourceLocation.fromNamespaceAndPath(reference.modid,"range"));

    public moditemtagprovider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        tag(ItemTags.SWORDS)
                .add(ItemsInit.iron_halberd).add(ItemsInit.netherite_halberd).add(ItemsInit.diamond_halberd).add(ItemsInit.gold_halberd).add(ItemsInit.stone_halberd).add(ItemsInit.wooden_halberd)
                .add(ItemsInit.stone_spear).add(ItemsInit.iron_spear).add(ItemsInit.wooden_spear).add(ItemsInit.gold_spear).add(ItemsInit.diamond_spear).add(ItemsInit.netherite_spear)
                .add(ItemsInit.iron_warhammer).add(ItemsInit.wooden_warhammer).add(ItemsInit.stone_warhammer).add(ItemsInit.diamond_warhammer).add(ItemsInit.gold_warhammer).add(ItemsInit.netherite_warhammer)
                .add(ItemsInit.wooden_dagger).add(ItemsInit.stone_dagger).add(ItemsInit.iron_dagger).add(ItemsInit.gold_dagger).add(ItemsInit.diamond_dagger).add(ItemsInit.netherite_dagger)
                .add(ItemsInit.diamond_gunsword).add(ItemsInit.iron_gunsword)
                .add(ItemsInit.GreatPick).add(ItemsInit.GHAL_MARAZ);

        tag(ItemTags.PICKAXES).add(ItemsInit.GreatPick);

        tag(ItemTags.BOW_ENCHANTABLE).add(ItemsInit.iron_gunsword).add(ItemsInit.diamond_gunsword)
                .add(ItemsInit.blunderbuss).add(ItemsInit.GrudgeRaker)
                .add(ItemsInit.musket).add(ItemsInit.pistol).add(ItemsInit.repeater_handgun).add(ItemsInit.RatlingGun).add(ItemsInit.Sling)
                .add(ItemsInit.thunderer_handgun).add(ItemsInit.Warplock_jezzail);
        tag(GRENADE).add(ItemsInit.grenade_launcher);

        tag(ItemTags.DURABILITY_ENCHANTABLE).add(ItemsInit.DrakeGun).add(ItemsInit.grenade_launcher)
                .add(ItemsInit.Dark_Elf_Shield).add(ItemsInit.High_Elf_Shield).add(ItemsInit.Imperial_shield).add(ItemsInit.Dwarf_shield).add(ItemsInit.Skaven_shield);

        tag(ItemTags.FOOT_ARMOR).add(ItemsInit.DIAMOND_CHAINMAIL_BOOTS);
        tag(ItemTags.LEG_ARMOR).add(ItemsInit.DIAMOND_CHAINMAIL_LEGGINGS);
        tag(ItemTags.CHEST_ARMOR).add(ItemsInit.DIAMOND_CHAINMAIL_CHESTPLATE);
        tag(ItemTags.HEAD_ARMOR).add(ItemsInit.DIAMOND_CHAINMAIL_HELMET);


    }


}
