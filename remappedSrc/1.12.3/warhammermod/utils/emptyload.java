package warhammermod.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import static warhammermod.mainInit.HIGHLIGHT_PACKET_ID;

public record emptyload() implements CustomPayload {



    public static final Id<emptyload> ID = new Id<>(HIGHLIGHT_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, emptyload> CODEC = PacketCodec.unit(new emptyload());
    // should you need to send more data, add the appropriate record parameters and change your codec:
    // public static final PacketCodec<RegistryByteBuf, BlockHighlightPayload> CODEC = PacketCodec.tuple(
    //         BlockPos.PACKET_CODEC, BlockHighlightPayload::blockPos,
    //         PacketCodecs.INTEGER, BlockHighlightPayload::myInt,
    //         Uuids.PACKET_CODEC, BlockHighlightPayload::myUuid,
    //         BlockHighlightPayload::new
    // );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
