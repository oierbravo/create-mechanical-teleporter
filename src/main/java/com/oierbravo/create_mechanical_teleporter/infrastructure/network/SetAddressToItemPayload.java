package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SetAddressToItemPayload(String address, int slot) implements CustomPacketPayload {
    public static final Type<SetAddressToItemPayload> TYPE = new Type<>(ModConstants.asResource("set_address_to_item"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, SetAddressToItemPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SetAddressToItemPayload::address,
            ByteBufCodecs.INT, SetAddressToItemPayload::slot,
            SetAddressToItemPayload::new
    );

}
