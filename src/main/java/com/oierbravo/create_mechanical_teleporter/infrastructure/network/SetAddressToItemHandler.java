package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterBlockItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SetAddressToItemHandler {
    public static final SetAddressToItemHandler INSTANCE = new SetAddressToItemHandler();

    public static SetAddressToItemHandler get() {
        return INSTANCE;
    }

    public void handle(final SetAddressToItemPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer serverPlayer){
                ItemStack itemStack = serverPlayer.getInventory().getItem(payload.slot());
                if(!HandTeleporterBlockItem.isHandTeleporterItem(itemStack))
                    return;
                HandTeleporterBlockItem.setAddress(itemStack, payload.address());
            }

        });
    }
}
