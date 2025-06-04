package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportersNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class LockNetworkHandler {
    public static final LockNetworkHandler INSTANCE = new LockNetworkHandler();

    public static LockNetworkHandler get() {
        return INSTANCE;
    }

    public void handle(final LockNetworkPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer serverPlayer){
                if (!MechanicalTeleporter.TELEPORTERS.mayAdministrate(payload.freqId(),serverPlayer))
                    return;
                TeleportersNetwork network = MechanicalTeleporter.TELEPORTERS.teleportersNetworks.get(payload.freqId());
                if (network != null) {
                    network.locked = payload.locked();
                    MechanicalTeleporter.TELEPORTERS.markDirty();
                }
            }

        });
    }
}
