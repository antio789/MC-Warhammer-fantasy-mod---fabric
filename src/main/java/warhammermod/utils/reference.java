package warhammermod.utils;

import net.minecraft.resources.Identifier;

public class reference {
    public static final String modid = "warhammermod";
    public static final String cartridge = "warhammermod:cartridge";
    public static final String shotgun_shells = "warhammermod:shotgun shells";
    public static final String grenade = "warhammermod:grenade";
    public static final String warpstone = "warhammermod:warpstone";
    public static final String Cartridge = "cartridge";
    public static final String Shotgun_Shells = "shotshell";
    public static final String Grenade = "grenade";
    public static final String Warpstone = "warpstone";
    public static final String slave = "slave";
    public static final String clanrat = "clanrat";
    public static final String stormvermin = "stormvermin";
    public static final String gutter_runner = "gutter runner";
    public static final String globadier = "globadier";
    public static final String ratling_gunner = "ratling gunner";
    public static final Identifier Dwarf_warrior = location("warrior");
    public static final Identifier Dwarf_slayer = location("slayer");
    public static final Identifier Dwarf_engineer = location("engineer");
    public static final Identifier Dwarf_miner = location("miner");
    public static final Identifier Dwarf_lord = location("lord");
    public static final Identifier Dwarf_builder = location("builder");
    public static final Identifier Dwarf_farmer = location("farmer");
    public static final String Warrior = "warrior";
    public static final String Farmer = "farmer";
    public static final String Engineer = "engineer";
    public static final String Miner = "miner";
    public static final String Lord = "lord";
    public static final String Builder = "builder";
    public static final String Slayer = "slayer";

    public static Identifier location(String name) {
        return Identifier.fromNamespaceAndPath(modid, name);
    }
}
