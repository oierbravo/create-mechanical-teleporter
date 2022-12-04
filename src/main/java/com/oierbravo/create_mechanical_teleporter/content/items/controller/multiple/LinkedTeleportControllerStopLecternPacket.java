package com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple;

import com.simibubi.create.content.logistics.item.LecternControllerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class LinkedTeleportControllerStopLecternPacket extends LinkedTeleportControllerPacketBase {

	public LinkedTeleportControllerStopLecternPacket(FriendlyByteBuf buffer) {
		super(buffer);
	}

	public LinkedTeleportControllerStopLecternPacket(BlockPos lecternPos) {
		super(lecternPos);
	}

	@Override
	protected void handleLectern(ServerPlayer player, LecternControllerTileEntity lectern) {
		lectern.tryStopUsing(player);
	}

	@Override
	protected void handleItem(ServerPlayer player, ItemStack heldItem) { }

}
