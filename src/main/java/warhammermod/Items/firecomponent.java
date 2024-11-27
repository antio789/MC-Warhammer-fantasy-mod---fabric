package warhammermod.Items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record firecomponent(int firecount) {//test adding component firing rotation that update on client
    public static final firecomponent DEFAULT = new firecomponent(0);
    public static final Codec<firecomponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codecs.POSITIVE_INT.fieldOf("firing").forGetter(firecomponent::firecount)
                    )
                    .apply(instance, firecomponent::new)
    );

    public static final PacketCodec<ByteBuf, firecomponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            firecomponent::firecount,
            firecomponent::new
    );

}
