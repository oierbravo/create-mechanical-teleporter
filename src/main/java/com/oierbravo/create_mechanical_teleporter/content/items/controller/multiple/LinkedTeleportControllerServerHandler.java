package com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.ITeleportLinkable;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportLinkNetworkHandler;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.IntAttached;
import com.simibubi.create.foundation.utility.WorldAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;

import java.util.*;
import java.util.Map.Entry;

public class LinkedTeleportControllerServerHandler {

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

	public static void receivePressed(LevelAccessor world, BlockPos pos, UUID uniqueID, List<Couple<TeleportLinkNetworkHandler.Frequency>> collect,
		boolean pressed) {
		Map<UUID, Collection<ManualFrequencyEntry>> map = receivedInputs.get(world);
		Collection<ManualFrequencyEntry> list = map.computeIfAbsent(uniqueID, $ -> new ArrayList<>());

		WithNext: for (Couple<TeleportLinkNetworkHandler.Frequency> activated : collect) {
			for (Iterator<ManualFrequencyEntry> iterator = list.iterator(); iterator.hasNext();) {
				ManualFrequencyEntry entry = iterator.next();
				if (entry.getSecond()
					.equals(activated)) {
					if (!pressed)
						entry.setFirst(0);
					else
						entry.updatePosition(pos);
					continue WithNext;
				}
			}

			if (!pressed)
				continue;

			ManualFrequencyEntry entry = new ManualFrequencyEntry(pos, activated);
			MechanicalTeleporter.TELEPORT_LINK_NETWORK_HANDLER.addToNetwork(world, entry);
			list.add(entry);
			
			//for (ITeleportLinkable linkable : MechanicalTeleporter.TELEPORT_LINK_NETWORK_HANDLER.getNetworkOf(world, entry))
				//if (linkable instanceof TeleportLinkBehaviour lb && lb.isListening())
				//	AllAdvancements.LINKED_CONTROLLER.awardTo(world.getPlayerByUUID(uniqueID));
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
		public void doTeleport(ServerPlayer player) {
		}


		@Override
		public Couple<TeleportLinkNetworkHandler.Frequency> getNetworkKey() {
			return getSecond();
		}

	}

}
