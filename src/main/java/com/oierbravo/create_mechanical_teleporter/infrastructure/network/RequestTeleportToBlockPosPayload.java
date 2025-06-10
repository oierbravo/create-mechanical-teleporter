package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RequestTeleportToBlockPosPayload(BlockPos blockPos) implements CustomPacketPayload {
    public static final Type<RequestTeleportToBlockPosPayload> TYPE = new Type<>(ModConstants.asResource("request_teleport_to_blockpos"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestTeleportToBlockPosPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RequestTeleportToBlockPosPayload::blockPos,
            RequestTeleportToBlockPosPayload::new
    );
}
