package warhammermod.utils;

import static warhammermod.mainInit.HIGHLIGHT_PACKET_ID;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record emptyload() implements CustomPacketPayload {



    public static final Type<emptyload> ID = new Type<>(HIGHLIGHT_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, emptyload> CODEC = StreamCodec.unit(new emptyload());
    // should you need to send more data, add the appropriate record parameters and change your codec:
    // public static final PacketCodec<RegistryByteBuf, BlockHighlightPayload> CODEC = PacketCodec.tuple(
    //         BlockPos.PACKET_CODEC, BlockHighlightPayload::blockPos,
    //         PacketCodecs.INTEGER, BlockHighlightPayload::myInt,
    //         Uuids.PACKET_CODEC, BlockHighlightPayload::myUuid,
    //         BlockHighlightPayload::new
    // );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
