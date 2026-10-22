package warhammermod.Items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record Ammocomponent(int ammocount, int startammo) {
    public static final Ammocomponent DEFAULT = new Ammocomponent(0, 0);
    public static final Codec<Ammocomponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ExtraCodecs.POSITIVE_INT.fieldOf("ammo").forGetter(Ammocomponent::ammocount),
                            ExtraCodecs.POSITIVE_INT.fieldOf("magsize").forGetter(Ammocomponent::startammo)
                    )
                    .apply(instance, Ammocomponent::new)
    );

    public static final StreamCodec<io.netty.buffer.ByteBuf, Ammocomponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            Ammocomponent::ammocount,
            ByteBufCodecs.VAR_INT,
            Ammocomponent::startammo,
            Ammocomponent::new
    );
}
