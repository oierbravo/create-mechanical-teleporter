package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.RequestTeleportToFrequencyPayload;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.oierbravo.create_mechanical_teleporter.registrate.ModMessages;
import com.oierbravo.create_mechanical_teleporter.registrate.ModPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class HandTeleporterActivatePacket implements ServerboundPacketPayload {
	public static final StreamCodec<ByteBuf, HandTeleporterActivatePacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, p -> p.pos,
			HandTeleporterActivatePacket::new
	);
	private final BlockPos pos;

	public HandTeleporterActivatePacket(BlockPos pos) {
        this.pos = pos;
	}

	@Override
	public void handle(ServerPlayer player) {
		ItemStack handTeleporter = player.getMainHandItem();
		if (!ModItems.HAND_TELEPORTER.isIn(handTeleporter)) {
			handTeleporter = player.getOffhandItem();
			if (!ModItems.HAND_TELEPORTER.isIn(handTeleporter))
				return;
		}
		if (player.isSpectator())
			return;
		if (player.isSpectator())
			return;

		TeleporterFrequency teleporterFrequency = TeleporterFrequency.fromHandTeleporter(handTeleporter);
		if(teleporterFrequency.freqId() != null)
			ModMessages.sendToServer(new RequestTeleportToFrequencyPayload(teleporterFrequency));

	}

	@Override
	public PacketTypeProvider getTypeProvider() {
		return ModPackets.HAND_TELEPORTER_ACTIVATE;
	}
}