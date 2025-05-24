package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record RequestTeleportToFrequencyWithItemPayload(TeleporterFrequency frequency, EquipmentSlot slot) implements CustomPacketPayload {
    public static final Type<RequestTeleportToFrequencyWithItemPayload> TYPE = new Type<>(ModConstants.asResource("request_teleport_to_frequency_with_item"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestTeleportToFrequencyWithItemPayload> STREAM_CODEC = StreamCodec.composite(
            TeleporterFrequency.STREAM_CODEC, RequestTeleportToFrequencyWithItemPayload::frequency,
            NeoForgeStreamCodecs.enumCodec(EquipmentSlot.class), RequestTeleportToFrequencyWithItemPayload::slot,
            RequestTeleportToFrequencyWithItemPayload::new
    );
}
