package warhammermod.Items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Ammocomponent(int ammocount, int startammo) {
    public static final Ammocomponent DEFAULT = new Ammocomponent(0, 0);

    /**
     * Clamp on construction: every path (codec decode, packet decode, setCharge)
     * goes through here, so the component can never hold a negative value - the
     * hashing/serialization path (HashedStack, player data) rejects negatives and
     * crashed the client with "Value must be within range [0;2147483647]".
     */
    public Ammocomponent {
        ammocount = Math.max(0, ammocount);
        startammo = Math.max(0, startammo);
    }

    public static final Codec<Ammocomponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.INT.fieldOf("ammo").forGetter(Ammocomponent::ammocount),
                            Codec.INT.fieldOf("magsize").forGetter(Ammocomponent::startammo)
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
