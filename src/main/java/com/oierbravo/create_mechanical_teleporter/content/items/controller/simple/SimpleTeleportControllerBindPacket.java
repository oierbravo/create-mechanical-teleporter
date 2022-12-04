package com.oierbravo.create_mechanical_teleporter.content.items.controller.simple;

import com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple.LinkedTeleportControllerPacketBase;
import com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport.TeleportLinkBehaviour;
import com.simibubi.create.content.logistics.item.LecternControllerTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class SimpleTeleportControllerBindPacket extends LinkedTeleportControllerPacketBase {

	private int button = 0;
	private BlockPos linkLocation;

	public SimpleTeleportControllerBindPacket( BlockPos linkLocation) {
		super((BlockPos) null);
		this.linkLocation = linkLocation;
	}

	public SimpleTeleportControllerBindPacket(FriendlyByteBuf buffer) {
		super(buffer);
		this.linkLocation = buffer.readBlockPos();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
		buffer.writeBlockPos(linkLocation);
	}

	@Override
	protected void handleItem(ServerPlayer player, ItemStack heldItem) {
		if (player.isSpectator())
			return;

		ItemStackHandler frequencyItems = SimpleTeleportControllerItem.getFrequencyItems(heldItem);
		TeleportLinkBehaviour teleportLinkBehaviour = TileEntityBehaviour.get(player.level, linkLocation, TeleportLinkBehaviour.TYPE);
		if (teleportLinkBehaviour == null)
			return;

		teleportLinkBehaviour.getNetworkKey()
			.forEachWithContext((f, first) -> frequencyItems.setStackInSlot(button * 2 + (first ? 0 : 1), f.getStack()
				.copy()));

		heldItem.getTag()
			.put("Items", frequencyItems.serializeNBT());
	}

	@Override
	protected void handleLectern(ServerPlayer player, LecternControllerTileEntity lectern) {}

}
