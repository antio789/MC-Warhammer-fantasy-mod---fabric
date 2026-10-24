package warhammermod.Items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record Ammocomponent(int ammocount,int startammo) {//test adding component firing rotation that update on client
    public static final Ammocomponent DEFAULT = new Ammocomponent(0,0);
    public static final Codec<Ammocomponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ammo").forGetter(Ammocomponent::ammocount),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("magsize").forGetter(Ammocomponent::startammo)
                    )
                    .apply(instance, Ammocomponent::new)
    );

    public static final StreamCodec<ByteBuf, Ammocomponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            Ammocomponent::ammocount,
            ByteBufCodecs.VAR_INT,
            Ammocomponent::startammo,
            Ammocomponent::new
    );

}
