package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RequestTeleportToGlobalPosPayload(GlobalPos globalPos) implements CustomPacketPayload {
    public static final Type<RequestTeleportToGlobalPosPayload> TYPE = new Type<>(ModConstants.asResource("request_teleport_to_globalpos"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestTeleportToGlobalPosPayload> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, RequestTeleportToGlobalPosPayload::globalPos,
            RequestTeleportToGlobalPosPayload::new
    );
}
