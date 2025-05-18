package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.world.level.LevelAccessor;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TeleportLinkNetworkHandler {

	static final Map<LevelAccessor, Map<String, Set<ITeleportLinkable>>> connections =
			new IdentityHashMap<>();

	public final AtomicInteger globalPowerVersion = new AtomicInteger();
	public void onLoadWorld(LevelAccessor world) {
		connections.put(world, new HashMap<>());
		MechanicalTeleporter.LOGGER.debug("Prepared Teleport Network Space for " + WorldHelper.getDimensionID(world));
	}

	public void onUnloadWorld(LevelAccessor world) {
		connections.remove(world);
		MechanicalTeleporter.LOGGER.debug("Removed Teleport Network Space for " + WorldHelper.getDimensionID(world));
	}

	public Set<ITeleportLinkable> getNetworkOf(LevelAccessor level, UUID actor) {
		Map<String, Set<ITeleportLinkable>> networksInWorld = networksIn(level);
		UUID key = actor;
		//if (!networksInWorld.containsKey(key))
		//	networksInWorld.put(key, new LinkedHashSet<>());
		return networksInWorld.get(key);
	}

	public void addToNetwork(LevelAccessor world, ITeleportLinkable actor) {
		//getNetworkOf(world, UUID).add(actor);
		//updateNetworkOf(world, actor);
	}

	public void removeFromNetwork(LevelAccessor world, ITeleportLinkable actor) {
		/*Set<ITeleportLinkable> network = getNetworkOf(world, actor.getUUID());
		network.remove(actor);
		if (network.isEmpty()) {
			networksIn(world).remove(actor.getNetworkKey());
			return;
		}
		updateNetworkOf(world, actor);*/
	}

	public void updateNetworkOf(LevelAccessor world, ITeleportLinkable actor) {
		/*Set<ITeleportLinkable> network = getNetworkOf(world, actor.getUUID());
		//globalPowerVersion.incrementAndGet();

		for (Iterator<ITeleportLinkable> iterator = network.iterator(); iterator.hasNext();) {
			ITeleportLinkable other = iterator.next();
			if (!other.isAlive()) {
				iterator.remove();
				continue;
			}
			if (!(world instanceof Level level) || !level.isLoaded(other.getLocation())) {
				iterator.remove();
				continue;
			}
			if (!withinRange(actor, other))
				continue;

		}*/

		/*if (actor instanceof TeleportLinkBehaviour) {
			TeleportLinkBehaviour teleportLinkBehaviour = (TeleportLinkBehaviour) actor;
			// fix one-to-one loading order problem
			//if (teleportLinkBehaviour.isListening()) {
				teleportLinkBehaviour.newPosition = true;
			teleportLinkBehaviour.doTeleport();
			//}
		}

		for (ITeleportLinkable other : network) {
			if (other != actor && withinRange(actor, other))
				other.doTeleport();
		}*/
	}

	public static boolean withinRange(ITeleportLinkable from, ITeleportLinkable to) {
		return true;
		/*if (from == to)
			return true;
		return from.getLocation()
			.closerThan(to.getLocation(), AllConfigs.SERVER.logistics.linkRange.get());*/
	}

	public Map<String, Set<ITeleportLinkable>> networksIn(LevelAccessor world) {
		if (!connections.containsKey(world)) {
			MechanicalTeleporter.LOGGER.warn("Tried to Access unprepared network space of " + WorldHelper.getDimensionID(world));
			return new HashMap<>();
		}
		return connections.get(world);
	}


}
