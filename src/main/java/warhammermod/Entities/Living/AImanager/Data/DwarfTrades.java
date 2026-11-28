package warhammermod.Entities.Living.AImanager.Data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import warhammermod.Items.ItemsInit;

import java.util.Map;

public class DwarfTrades {
    public static final Map<DwarfProfessionRecord, Int2ObjectMap<TradeFactory[]>> TRADES = make(Maps.newHashMap(), (professiontrades) -> {
        professiontrades.put(DwarfProfessionRecord.FARMER, copyToFastUtilMap(ImmutableMap.of(1, new TradeFactory[]{
                buy(Items.WHEAT, 20, 16, 2), buy(Items.POTATO, 26, 16, 2), buy(Items.CARROT, 22, 16, 2), sell(ItemsInit.BEER, 8, 1, 2)
        }, 2, new TradeFactory[]{
                buy(Blocks.PUMPKIN, 6, 12, 10), sell(Items.PUMPKIN_PIE, 1, 4, 5), sell(Items.APPLE, 1, 4, 16, 5)
        }, 3, new TradeFactory[]{
                sell(Items.COOKIE, 3, 18, 10), buy(Blocks.MELON, 4, 12, 20)
        }, 4, new TradeFactory[]{
                sell(Blocks.CAKE, 1, 1, 12, 15)
        }, 5, new TradeFactory[]{
                sell(Items.GOLDEN_CARROT, 3, 3, 30), sell(Items.GLISTERING_MELON_SLICE, 4, 3, 30)
        })));
        professiontrades.put(DwarfProfessionRecord.Miner, copyToFastUtilMap(ImmutableMap.of(1, new TradeFactory[]{
                buy(Items.COAL, 15, 16, 2), buy(Items.REDSTONE, 32, 14, 1), buy(Items.GOLD_INGOT, 3, 12, 5), buy(Items.OBSIDIAN, 3, 12, 15), buy(Items.IRON_INGOT, 8, 10, 3)
        }, 2, new TradeFactory[]{
                sell(Items.MINECART, 3, 1, 15), sell(Items.LANTERN, 1, 2, 10), buy(ItemsInit.BEER, 3, 30, 8)
        }, 3, new TradeFactory[]{
                buy(Items.TNT, 8, 10, 30), sell(Items.QUARTZ, 35, 4, 5, 30), sell(Items.RAIL, 1, 2, 5)
        }, 4, new TradeFactory[]{
                sell(Items.POWERED_RAIL, 3, 18, 20)
        }, 5, new TradeFactory[]{
                sell(Items.QUARTZ, 20, 1, 30)
        })));
        professiontrades.put(DwarfProfessionRecord.Builder, copyToFastUtilMap(ImmutableMap.of(1, new TradeFactory[]{
                sell(Items.CLAY_BALL, 10, 1, 2), sell(Items.BRICK, 20, 1, 16, 10), sell(Items.SANDSTONE, 8, 2, 20, 5), sell(Items.RED_SANDSTONE, 12, 2, 20, 5)
        }, 2, new TradeFactory[]{
                buy(Blocks.STONE, 20, 16, 10), sell(Blocks.CHISELED_STONE_BRICKS, 1, 4, 16, 5)
        }, 3, new TradeFactory[]{
                buy(Blocks.GRANITE, 16, 16, 20), buy(Blocks.ANDESITE, 16, 16, 20), buy(Blocks.DIORITE, 16, 16, 20),
                sell(Blocks.POLISHED_ANDESITE, 1, 4, 16, 10), sell(Blocks.POLISHED_DIORITE, 1, 4, 16, 10), sell(Blocks.POLISHED_GRANITE, 1, 4, 16, 10)
        }, 4, concreteTrades(15))));
        professiontrades.put(DwarfProfessionRecord.Slayer, copyToFastUtilMap(ImmutableMap.of(1, new TradeFactory[]{
                buy(Items.COOKED_BEEF, 12, 15, 5), buy(Items.COOKED_CHICKEN, 12, 15, 5), buy(Items.COOKED_COD, 12, 15, 5),
                buy(Items.COOKED_MUTTON, 12, 15, 5), buy(Items.COOKED_PORKCHOP, 12, 15, 5), buy(Items.COOKED_RABBIT, 12, 15, 5), buy(Items.COOKED_SALMON, 12, 15, 5)
        }, 2, new TradeFactory[]{
                buy(Items.RABBIT_STEW, 12, 15, 10), buy(ItemsInit.BEER, 3, 30, 15)
        }, 3, new TradeFactory[]{
                sell(Items.BONE, 20, 1, 30, 10)
        }, 4, new TradeFactory[]{
                buy(Items.PHANTOM_MEMBRANE, 40, 3, 30)
        })));
        professiontrades.put(DwarfProfessionRecord.Engineer, copyToFastUtilMap(ImmutableMap.of(1, new TradeFactory[]{
                sell(ItemsInit.Cartridge, 5, 1, 5), sell(ItemsInit.shotShell, 6, 1, 5)
        }, 2, new TradeFactory[]{
                buy(ItemsInit.BEER, 2, 30, 8), sell(Items.PISTON, 8, 1, 30, 10), sell(Items.DISPENSER, 16, 1, 38, 5)
        }, 3, new TradeFactory[]{
                sell(ItemsInit.thunderer_handgun, 15, 1, 30)
        }, 4, new TradeFactory[]{
                sell(ItemsInit.GrudgeRaker, 16, 1, 20)
        }, 5, new TradeFactory[]{
                sell(ItemsInit.DrakeGun, 20, 1, 30)
        })));
    });

    private static Int2ObjectMap<TradeFactory[]> copyToFastUtilMap(ImmutableMap<Integer, TradeFactory[]> map) {
        return new Int2ObjectOpenHashMap<>(map);
    }

    private static <T> T make(T object, java.util.function.Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }

    private static TradeFactory[] concreteTrades(int xp) {
        return Items.CONCRETE.asList().stream()
                .map(item -> (TradeFactory) (random) -> new MerchantOffer(new ItemCost(Items.EMERALD, 2), new ItemStack(item, 1), 12, xp, 0.05F))
                .toArray(TradeFactory[]::new);
    }

    private static TradeFactory buy(ItemLike item, int count, int maxUses, int xp) {
        return (random) -> new MerchantOffer(new ItemCost(item, count), new ItemStack(Items.EMERALD), maxUses, xp, 0.05F);
    }

    private static TradeFactory sell(ItemLike item, int emeralds, int count, int maxUses, int xp) {
        return (random) -> new MerchantOffer(new ItemCost(Items.EMERALD, emeralds), new ItemStack(item, count), maxUses, xp, 0.05F);
    }

    private static TradeFactory sell(ItemLike item, int emeralds, int count, int xp) {
        return (random) -> new MerchantOffer(new ItemCost(Items.EMERALD, emeralds), new ItemStack(item, count), 12, xp, 0.05F);
    }
}
