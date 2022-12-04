package com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple;

import com.simibubi.create.content.logistics.item.LecternControllerTileEntity;
import com.simibubi.create.content.logistics.item.LinkedControllerItem;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.linked.LinkBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class LinkedTeleportControllerBindPacket extends LinkedTeleportControllerPacketBase {

	private int button;
	private BlockPos linkLocation;

	public LinkedTeleportControllerBindPacket(int button, BlockPos linkLocation) {
		super((BlockPos) null);
		this.button = button;
		this.linkLocation = linkLocation;
	}

	public LinkedTeleportControllerBindPacket(FriendlyByteBuf buffer) {
		super(buffer);
		this.button = buffer.readVarInt();
		this.linkLocation = buffer.readBlockPos();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
		buffer.writeVarInt(button);
		buffer.writeBlockPos(linkLocation);
	}

	@Override
	protected void handleItem(ServerPlayer player, ItemStack heldItem) {
		if (player.isSpectator())
			return;

		ItemStackHandler frequencyItems = LinkedControllerItem.getFrequencyItems(heldItem);
		LinkBehaviour linkBehaviour = TileEntityBehaviour.get(player.level, linkLocation, LinkBehaviour.TYPE);
		if (linkBehaviour == null)
			return;

		linkBehaviour.getNetworkKey()
			.forEachWithContext((f, first) -> frequencyItems.setStackInSlot(button * 2 + (first ? 0 : 1), f.getStack()
				.copy()));

		heldItem.getTag()
			.put("Items", frequencyItems.serializeNBT());
	}

	@Override
	protected void handleLectern(ServerPlayer player, LecternControllerTileEntity lectern) {}

}
