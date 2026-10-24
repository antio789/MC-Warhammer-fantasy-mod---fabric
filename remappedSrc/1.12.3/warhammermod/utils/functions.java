package warhammermod.utils;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import warhammermod.utils.Registry.ItemsInit;

import java.util.Random;

public class functions {
    public static Identifier location(String name)
    {
        return Identifier.of(reference.modid, name);
    }

    static int timer;
    public static void printer(Object object){
        if (timer==0){
            timer=11;
            System.out.println(object);
        }
        timer--;
    }
    static Object printermem;

    public static void printer_norepeat(Object object){
        if(!object.equals(printermem)){
            System.out.println(object+ " :: object printed for debugging");
            printermem=object;
        }
    }


    public static double getFallHeightFromSpeed(double y){
        return -22.8*Math.pow(y,5) +154*Math.pow(y,4) - 317*Math.pow(y,3) + 278*Math.pow(y,2) - 81.1*y;
    }

    private static final Item[] spears = {ItemsInit.wooden_spear,ItemsInit.wooden_spear,ItemsInit.stone_spear,ItemsInit.iron_spear,ItemsInit.diamond_spear};
    public static ItemStack getRandomspear(int maxlevel5){
        int random = new Random().nextInt(maxlevel5);
        return new ItemStack(spears[random]);
    }
    private static final Item[] sword = {Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD};
    public static ItemStack getRandomsword(int maxlevel5){
        int random = new Random().nextInt(maxlevel5);
        return new ItemStack(sword[random]);
    }
    private static final Item[] halberd =  {ItemsInit.stone_halberd,ItemsInit.iron_halberd,ItemsInit.iron_halberd,ItemsInit.diamond_halberd};
    public static ItemStack getRandomHalberd(int maxlevel4){
        int random = new Random().nextInt(maxlevel4);
        return new ItemStack(halberd[random]);
    }
    public static Item getRandomShield(){
        int shield = new Random().nextInt(4);
        switch(shield){
            case 0:return ItemsInit.Dwarf_shield;
            case 1:return ItemsInit.Imperial_shield;
            case 2:return ItemsInit.High_Elf_Shield;
            default:return ItemsInit.Dark_Elf_Shield;
        }
    }

    public static Item getRandomarmor(int part){
        int random = new Random().nextInt(2);
        if(random==0){if(part<1)return ItemsInit.DIAMOND_CHAINMAIL_LEGGINGS;else return ItemsInit.DIAMOND_CHAINMAIL_BOOTS;}
        else{if(part<1)return ItemsInit.DIAMOND_CHAINMAIL_HELMET;else return ItemsInit.DIAMOND_CHAINMAIL_CHESTPLATE;}
    }

    public static Item[] skulls = {Items.SKELETON_SKULL, Items.CREEPER_HEAD, Items.ZOMBIE_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD};

    public static Item getrandomskull(){
        int random = new Random().nextInt(skulls.length);
        return skulls[random];
    }
}
