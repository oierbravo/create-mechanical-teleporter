package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportLinkNetworkHandler;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record RequestTeleportPayload(UUID playerUUID, Couple<TeleportLinkNetworkHandler.Frequency> key) implements CustomPacketPayload {
    public static final Type<RequestTeleportPayload> TYPE = new Type<>(ModConstants.asResource("request_teleport"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestTeleportPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, RequestTeleportPayload::playerUUID,
            Couple.streamCodec(TeleportLinkNetworkHandler.Frequency.STREAM_CODEC), RequestTeleportPayload::key,
            RequestTeleportPayload::new
    );
}
