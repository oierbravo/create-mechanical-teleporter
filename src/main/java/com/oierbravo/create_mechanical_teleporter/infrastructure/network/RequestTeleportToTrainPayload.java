package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record RequestTeleportToTrainPayload(UUID trainId, int carriageId, String address) implements CustomPacketPayload {
    public static final Type<RequestTeleportToTrainPayload> TYPE = new Type<>(ModConstants.asResource("request_teleport_to_train"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestTeleportToTrainPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, RequestTeleportToTrainPayload::trainId,
            ByteBufCodecs.INT, RequestTeleportToTrainPayload::carriageId,
            ByteBufCodecs.STRING_UTF8, RequestTeleportToTrainPayload::address,
            RequestTeleportToTrainPayload::new
    );
}
