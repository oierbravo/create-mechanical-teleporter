package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModMessages {
    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(ModConstants.MODID);

        //Going to Client
        //registrar.playToClient(ItemSyncPayload.TYPE, ItemSyncPayload.STREAM_CODEC, ItemSyncPacket.get()::handle);
        //Going to server
        registrar.playToServer(RequestTeleportToFrequencyPayload.TYPE, RequestTeleportToFrequencyPayload.STREAM_CODEC, RequestTeleportToFrequencyHandler.get()::handle);
        registrar.playToServer(RequestTeleportToBlockPosPayload.TYPE, RequestTeleportToBlockPosPayload.STREAM_CODEC, RequestTeleportToBlockPosHandler.get()::handle);
        registrar.playToServer(RequestTeleportToGlobalPosPayload.TYPE, RequestTeleportToGlobalPosPayload.STREAM_CODEC, RequestTeleportToGlobalPosHandler.get()::handle);
        registrar.playToServer(RequestTeleportToFrequencyWithItemPayload.TYPE, RequestTeleportToFrequencyWithItemPayload.STREAM_CODEC, RequestTeleportToFrequencyWithItemHandler.get()::handle);
        registrar.playToServer(RequestTeleportToTrainPayload.TYPE, RequestTeleportToTrainPayload.STREAM_CODEC, RequestTeleportToTrainHandler.get()::handle);
        registrar.playToServer(SetAddressToItemPayload.TYPE, SetAddressToItemPayload.STREAM_CODEC, SetAddressToItemHandler.get()::handle);
        registrar.playToServer(SetAddressToBlockEntityPayload.TYPE, SetAddressToBlockEntityPayload.STREAM_CODEC, SetAddressToBlockEntityHandler.get()::handle);

    }
    public static void sendToAllClients(CustomPacketPayload message) {
        PacketDistributor.sendToAllPlayers(message);
    }
    public static void sendToServer(CustomPacketPayload message){
        PacketDistributor.sendToServer(message);
    }
    public static void register() {}
}
