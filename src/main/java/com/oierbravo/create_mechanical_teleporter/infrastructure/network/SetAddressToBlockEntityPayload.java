package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SetAddressToBlockEntityPayload(BlockPos blockPos, String address) implements CustomPacketPayload {
    public static final Type<SetAddressToBlockEntityPayload> TYPE = new Type<>(ModConstants.asResource("set_address_to_be"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, SetAddressToBlockEntityPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetAddressToBlockEntityPayload::blockPos,
            ByteBufCodecs.STRING_UTF8, SetAddressToBlockEntityPayload::address,
            SetAddressToBlockEntityPayload::new
    );

}
