package com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple;

import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.item.LecternControllerTileEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public abstract class LinkedTeleportControllerPacketBase extends SimplePacketBase {

	private BlockPos lecternPos;

	public LinkedTeleportControllerPacketBase(BlockPos lecternPos) {
		this.lecternPos = lecternPos;
	}

	public LinkedTeleportControllerPacketBase(FriendlyByteBuf buffer) {
		if (buffer.readBoolean()) {
			lecternPos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
		}
	}

	protected boolean inLectern() {
		return lecternPos != null;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(inLectern());
		if (inLectern()) {
			buffer.writeInt(lecternPos.getX());
			buffer.writeInt(lecternPos.getY());
			buffer.writeInt(lecternPos.getZ());
		}
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			ServerPlayer player = context.get().getSender();
			if (player == null)
				return;

			if (inLectern()) {
				BlockEntity te = player.level.getBlockEntity(lecternPos);
				if (!(te instanceof LecternControllerTileEntity))
					return;
				handleLectern(player, (LecternControllerTileEntity) te);
			} else {
				ItemStack controller = player.getMainHandItem();
				if (!ModItems.LINKED_TELEPORT_CONTROLLER.isIn(controller) && !ModItems.SIMPLE_TELEPORT_CONTROLLER.isIn(controller)) {
					controller = player.getOffhandItem();
					if (!ModItems.LINKED_TELEPORT_CONTROLLER.isIn(controller) && !ModItems.SIMPLE_TELEPORT_CONTROLLER.isIn(controller))
						return;
				}
				handleItem(player, controller);
			}
		});

		context.get().setPacketHandled(true);
	}

	protected abstract void handleItem(ServerPlayer player, ItemStack heldItem);
	protected abstract void handleLectern(ServerPlayer player, LecternControllerTileEntity lectern);

}
