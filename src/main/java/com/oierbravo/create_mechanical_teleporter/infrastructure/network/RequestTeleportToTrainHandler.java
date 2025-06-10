package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestTeleportToTrainHandler {
    public static final RequestTeleportToTrainHandler INSTANCE = new RequestTeleportToTrainHandler();

    public static RequestTeleportToTrainHandler get() {
        return INSTANCE;
    }

    public void handle(final RequestTeleportToTrainPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer serverPlayer){
                TeleportHandler.teleportToTrain(payload.trainId(), payload.carriageId(), payload.address(), serverPlayer);
            }
        });
    }
}
