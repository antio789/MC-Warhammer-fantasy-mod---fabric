package warhammermod.Items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record Ammocomponent(int ammocount,int startammo) {//test adding component firing rotation that update on client
    public static final Ammocomponent DEFAULT = new Ammocomponent(0,0);
    public static final Codec<Ammocomponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codecs.POSITIVE_INT.fieldOf("ammo").forGetter(Ammocomponent::ammocount),
                            Codecs.POSITIVE_INT.fieldOf("magsize").forGetter(Ammocomponent::startammo)
                    )
                    .apply(instance, Ammocomponent::new)
    );

    public static final PacketCodec<ByteBuf, Ammocomponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            Ammocomponent::ammocount,
            PacketCodecs.VAR_INT,
            Ammocomponent::startammo,
            Ammocomponent::new
    );

}
