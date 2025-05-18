package com.oierbravo.create_mechanical_teleporter.content.items.wand;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.ITeleportLinkable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;

import java.util.*;

public class TeleportWandServerHandler {

//	public static WorldAttached<UUID> receivedInputs =
//		new WorldAttached<>($ -> new UUID);
	static final int TIMEOUT = 30;

	public static void tick(LevelAccessor world) {
		/*Map<UUID, Collection<ManualFrequencyEntry>> map = receivedInputs.get(world);
		for (Iterator<Entry<UUID, Collection<ManualFrequencyEntry>>> iterator = map.entrySet()
			.iterator(); iterator.hasNext();) {

			Entry<UUID, Collection<ManualFrequencyEntry>> entry = iterator.next();
			Collection<ManualFrequencyEntry> list = entry.getValue();

			for (Iterator<ManualFrequencyEntry> entryIterator = list.iterator(); entryIterator.hasNext();) {
				ManualFrequencyEntry manualFrequencyEntry = entryIterator.next();
				manualFrequencyEntry.decrement();
				if (!manualFrequencyEntry.isAlive()) {
					MechanicalTeleporter.TELEPORT_NETWORK_HANDLER.removeFromNetwork(world, manualFrequencyEntry);
					entryIterator.remove();
				}
			}

			if (list.isEmpty())
				iterator.remove();
		}*/
	}

	public static void receiveActivated(LevelAccessor world, BlockPos pos, UUID uniqueID, BlockPos destinationPos, ServerPlayer pPlayer) {
		Set<ITeleportLinkable> teleport = MechanicalTeleporter.TELEPORT_NETWORK_HANDLER.getNetworkOf(world, uniqueID);
		if(!teleport.isEmpty()){
			ITeleportLinkable teleportLink = teleport.iterator().next();
			teleportLink.doTeleport(pPlayer);

		} else {
			pPlayer.displayClientMessage(Component.translatable("create_mechanical_teleporter.simple_teleport_controller.not_found"),true);
		}
	}


}
