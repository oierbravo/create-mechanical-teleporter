package com.oierbravo.create_mechanical_teleporter.content.items.wand;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.UUID;

public class TeleportWandActivatePacket extends TeleportWandPacketBase {

	private Collection<Integer> activatedButtons;
	private boolean press;

	public TeleportWandActivatePacket(boolean press) {
		this.press = press;
	}

	public TeleportWandActivatePacket(FriendlyByteBuf buffer) {
		super(buffer);
		press = buffer.readBoolean();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		//super.write(buffer);
		buffer.writeBoolean(press);
	}

	@Override
	public boolean handle(NetworkEvent.Context context) {
		return false;
	}


	@Override
	protected void handleItem(ServerPlayer player, ItemStack heldItem) {
		Level world = player.getCommandSenderWorld();
		UUID uniqueID = player.getUUID();
		BlockPos pos = player.blockPosition();

		if (player.isSpectator() && press)
			return;
		TeleportWandServerHandler.receiveActivated(world, pos, uniqueID, pos,player);
	}

}
