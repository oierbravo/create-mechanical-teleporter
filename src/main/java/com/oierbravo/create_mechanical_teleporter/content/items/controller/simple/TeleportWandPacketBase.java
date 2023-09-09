package com.oierbravo.create_mechanical_teleporter.content.items.controller.simple;

import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public abstract class TeleportWandPacketBase extends SimplePacketBase {


	public TeleportWandPacketBase() {

	}

	public TeleportWandPacketBase(FriendlyByteBuf buffer) {
	}



	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null)
				return;

			ItemStack wand = player.getMainHandItem();
			if (!ModItems.TELEPORT_WAND.isIn(wand)) {
				wand = player.getOffhandItem();
				if (!ModItems.TELEPORT_WAND.isIn(wand))
					return;
			}
			handleItem(player, wand);
		});

		context.setPacketHandled(true);
		return true;
	}

	protected abstract void handleItem(ServerPlayer player, ItemStack heldItem);

}
