package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record RequestTeleportToFrequencyPayload(UUID frequency) implements CustomPacketPayload {
    public static final Type<RequestTeleportToFrequencyPayload> TYPE = new Type<>(ModConstants.asResource("request_teleport_to_frequency"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestTeleportToFrequencyPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, RequestTeleportToFrequencyPayload::frequency,
            RequestTeleportToFrequencyPayload::new
    );
}
