package warhammermod.Items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record firecomponent(int firecount) {
    public static final firecomponent DEFAULT = new firecomponent(0);
    public static final Codec<firecomponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ExtraCodecs.POSITIVE_INT.fieldOf("firing").forGetter(firecomponent::firecount)
                    )
                    .apply(instance, firecomponent::new)
    );

    public static final StreamCodec<io.netty.buffer.ByteBuf, firecomponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            firecomponent::firecount,
            firecomponent::new
    );
}
