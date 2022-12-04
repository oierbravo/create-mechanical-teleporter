package com.oierbravo.create_mechanical_teleporter.content.items.controller.simple;

import com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple.LinkedTeleportControllerPacketBase;
import com.simibubi.create.content.logistics.item.LecternControllerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class SimpleTeleportControllerActivatePacket extends LinkedTeleportControllerPacketBase {

	private Collection<Integer> activatedButtons;
	private boolean press;

	public SimpleTeleportControllerActivatePacket( boolean press) {
		this(press, null);
	}

	public SimpleTeleportControllerActivatePacket( boolean press, BlockPos lecternPos) {
		super(lecternPos);
		this.press = press;
	}

	public SimpleTeleportControllerActivatePacket(FriendlyByteBuf buffer) {
		super(buffer);
		press = buffer.readBoolean();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
		buffer.writeBoolean(press);
	}

	@Override
	protected void handleLectern(ServerPlayer player, LecternControllerTileEntity lectern) {
		if (lectern.isUsedBy(player))
			handleItem(player, lectern.getController());
	}

	@Override
	protected void handleItem(ServerPlayer player, ItemStack heldItem) {
		Level world = player.getCommandSenderWorld();
		UUID uniqueID = player.getUUID();
		BlockPos pos = player.blockPosition();

		if (player.isSpectator() && press)
			return;
		SimpleTeleportControllerServerHandler.receiveActivated(world, pos, uniqueID, Collections.singletonList(SimpleTeleportControllerItem.toFrequency(heldItem, 0)),player);
	}

}
