package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record LockNetworkPayload(UUID freqId, boolean locked) implements CustomPacketPayload {
    public static final Type<LockNetworkPayload> TYPE = new Type<>(ModConstants.asResource("lock_network"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, LockNetworkPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, LockNetworkPayload::freqId,
            ByteBufCodecs.BOOL, LockNetworkPayload::locked,
            LockNetworkPayload::new
    );

}
