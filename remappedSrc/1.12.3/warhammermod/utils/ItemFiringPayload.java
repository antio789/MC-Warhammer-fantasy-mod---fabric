package warhammermod.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static warhammermod.mainInit.HIGHLIGHT_PACKET_ID;

public record ItemFiringPayload(ItemStack stack) implements CustomPayload {



    public static final CustomPayload.Id<ItemFiringPayload> ID = new CustomPayload.Id<>(HIGHLIGHT_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ItemFiringPayload> CODEC = PacketCodec.tuple(ItemStack.PACKET_CODEC, ItemFiringPayload::stack, ItemFiringPayload::new);
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
