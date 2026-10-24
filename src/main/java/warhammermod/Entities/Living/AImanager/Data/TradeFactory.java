package warhammermod.Entities.Living.AImanager.Data;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.trading.MerchantOffer;

@FunctionalInterface
public interface TradeFactory {
    MerchantOffer create(RandomSource random);
}
