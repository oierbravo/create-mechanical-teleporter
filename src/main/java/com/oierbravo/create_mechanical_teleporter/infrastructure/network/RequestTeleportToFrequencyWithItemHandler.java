package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterBlockItem;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestTeleportToFrequencyWithItemHandler {
    public static final RequestTeleportToFrequencyWithItemHandler INSTANCE = new RequestTeleportToFrequencyWithItemHandler();

    public static RequestTeleportToFrequencyWithItemHandler get() {
        return INSTANCE;
    }

    public void handle(final RequestTeleportToFrequencyWithItemPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer serverPlayer){
                ItemStack itemStack = serverPlayer.getItemBySlot(payload.slot());

                if(!HandTeleporterBlockItem.hasEnoughResources(serverPlayer))
                    return;

                boolean success = TeleportHandler.teleportToFrequency(payload.frequency(), serverPlayer);
                if(success){
                    if(!HandTeleporterBlockItem.isHandTeleporterItem(itemStack))
                        return;
                    HandTeleporterBlockItem.consumeResources(itemStack, serverPlayer, payload.slot());
                }
            }

        });
    }
}
