package warhammermod.Items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record firecomponent(int firecount) {//test adding component firing rotation that update on client
    public static final firecomponent DEFAULT = new firecomponent(0);
    public static final Codec<firecomponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("firing").forGetter(firecomponent::firecount)
                    )
                    .apply(instance, firecomponent::new)
    );

    public static final StreamCodec<ByteBuf, firecomponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            firecomponent::firecount,
            firecomponent::new
    );

}
