package com.oierbravo.create_mechanical_teleporter.content.items.controller.simple;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.ITeleportLinkable;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportLinkNetworkHandler;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.IntAttached;
import com.simibubi.create.foundation.utility.WorldAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.*;
import java.util.Map.Entry;

public class SimpleTeleportControllerServerHandler {

	public static WorldAttached<Map<UUID, Collection<ManualFrequencyEntry>>> receivedInputs =
		new WorldAttached<>($ -> new HashMap<>());
	static final int TIMEOUT = 30;

	public static void tick(LevelAccessor world) {
		Map<UUID, Collection<ManualFrequencyEntry>> map = receivedInputs.get(world);
		for (Iterator<Entry<UUID, Collection<ManualFrequencyEntry>>> iterator = map.entrySet()
			.iterator(); iterator.hasNext();) {

			Entry<UUID, Collection<ManualFrequencyEntry>> entry = iterator.next();
			Collection<ManualFrequencyEntry> list = entry.getValue();

			for (Iterator<ManualFrequencyEntry> entryIterator = list.iterator(); entryIterator.hasNext();) {
				ManualFrequencyEntry manualFrequencyEntry = entryIterator.next();
				manualFrequencyEntry.decrement();
				if (!manualFrequencyEntry.isAlive()) {
					MechanicalTeleporter.TELEPORT_LINK_NETWORK_HANDLER.removeFromNetwork(world, manualFrequencyEntry);
					entryIterator.remove();
				}
			}

			if (list.isEmpty())
				iterator.remove();
		}
	}

	public static void receiveActivated(LevelAccessor world, BlockPos pos, UUID uniqueID, List<Couple<TeleportLinkNetworkHandler.Frequency>> collect, ServerPlayer pPlayer) {
		ManualFrequencyEntry entry = new ManualFrequencyEntry(pos,collect.get(0));
		Set<ITeleportLinkable> teleport = MechanicalTeleporter.TELEPORT_LINK_NETWORK_HANDLER.getNetworkOf(world, entry);
		if(!teleport.isEmpty()){
			ITeleportLinkable teleportLink = teleport.iterator().next();
			teleportLink.doTeleport(pPlayer);

		}
	}

	static class ManualFrequencyEntry extends IntAttached<Couple<TeleportLinkNetworkHandler.Frequency>> implements ITeleportLinkable {

		private BlockPos pos;

		public ManualFrequencyEntry(BlockPos pos, Couple<TeleportLinkNetworkHandler.Frequency> second) {
			super(TIMEOUT, second);
			this.pos = pos;
		}

		public void updatePosition(BlockPos pos) {
			this.pos = pos;
			setFirst(TIMEOUT);
		}


		@Override
		public boolean isAlive() {
			return getFirst() > 0;
		}

		@Override
		public BlockPos getLocation() {
			return pos;
		}

		@Override
		public void doTeleport(ServerPlayer pPlayer) {

		}


		@Override
		public Couple<TeleportLinkNetworkHandler.Frequency> getNetworkKey() {
			return getSecond();
		}

	}

}
