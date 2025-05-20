package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport.TeleportHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestTeleportToFrequencyHandler {
    public static final RequestTeleportToFrequencyHandler INSTANCE = new RequestTeleportToFrequencyHandler();

    public static RequestTeleportToFrequencyHandler get() {
        return INSTANCE;
    }

    public void handle(final RequestTeleportToFrequencyPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer serverPlayer){
                TeleportHandler.teleportToFrequency(payload.frequency(), serverPlayer);
            }

        });
    }
}
