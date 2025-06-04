package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestTeleportToGlobalPosHandler {
    public static final RequestTeleportToGlobalPosHandler INSTANCE = new RequestTeleportToGlobalPosHandler();

    public static RequestTeleportToGlobalPosHandler get() {
        return INSTANCE;
    }

    public void handle(final RequestTeleportToGlobalPosPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {

            if(context.player() instanceof ServerPlayer player) {
                return TeleportHandler.tryTeleportToGlobalPosAndSit(payload.globalPos(), "*",  player, true);
            }
            return false;
        });
    }
}
