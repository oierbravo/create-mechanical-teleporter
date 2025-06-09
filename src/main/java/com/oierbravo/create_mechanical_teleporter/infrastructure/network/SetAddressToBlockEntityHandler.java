package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SetAddressToBlockEntityHandler {
    public static final SetAddressToBlockEntityHandler INSTANCE = new SetAddressToBlockEntityHandler();

    public static SetAddressToBlockEntityHandler get() {
        return INSTANCE;
    }

    public void handle(final SetAddressToBlockEntityPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
                BlockEntity blockEntity = context.player().level().getBlockEntity(payload.blockPos());
                if(blockEntity instanceof HandTeleporterBlockEntity handTeleporterBlockEntity)
                    handTeleporterBlockEntity.setAddress(payload.address());

        });
    }
}
