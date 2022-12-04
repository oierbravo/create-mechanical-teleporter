package com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple;

import com.simibubi.create.content.logistics.item.LecternControllerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class LinkedTeleportControllerInputPacket extends LinkedTeleportControllerPacketBase {

	private Collection<Integer> activatedButtons;
	private boolean press;

	public LinkedTeleportControllerInputPacket(Collection<Integer> activatedButtons, boolean press) {
		this(activatedButtons, press, null);
	}

	public LinkedTeleportControllerInputPacket(Collection<Integer> activatedButtons, boolean press, BlockPos lecternPos) {
		super(lecternPos);
		this.activatedButtons = activatedButtons;
		this.press = press;
	}

	public LinkedTeleportControllerInputPacket(FriendlyByteBuf buffer) {
		super(buffer);
		activatedButtons = new ArrayList<>();
		press = buffer.readBoolean();
		int size = buffer.readVarInt();
		for (int i = 0; i < size; i++)
			activatedButtons.add(buffer.readVarInt());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
		buffer.writeBoolean(press);
		buffer.writeVarInt(activatedButtons.size());
		activatedButtons.forEach(buffer::writeVarInt);
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
		
		LinkedTeleportControllerServerHandler.receivePressed(world, pos, uniqueID, activatedButtons.stream()
			.map(i -> LinkedTeleportControllerItem.toFrequency(heldItem, i))
			.collect(Collectors.toList()), press);
	}

}
