package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestTeleportToBlockPosHandler {
    public static final RequestTeleportToBlockPosHandler INSTANCE = new RequestTeleportToBlockPosHandler();

    public static RequestTeleportToBlockPosHandler get() {
        return INSTANCE;
    }

    public void handle(final RequestTeleportToBlockPosPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {

            if(context.player() instanceof ServerPlayer player) {
                TeleportHandler.teleportToGlobalPosSimple(payload.blockPos(), player);
                return true;
            }
            return false;
        });
    }
}
