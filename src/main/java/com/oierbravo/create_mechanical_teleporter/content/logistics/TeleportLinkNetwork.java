package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TeleportLinkNetwork {

	static final Map<LevelAccessor, Map<Couple<TeleportLinkNetwork.Frequency>, Set<ITeleportLinkable>>> connections =
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

	public Set<ITeleportLinkable> getNetworkOf(LevelAccessor level, ITeleportLinkable actor) {
		Map<Couple<TeleportLinkNetwork.Frequency>, Set<ITeleportLinkable>> networksInWorld = networksIn(level);
		Couple<TeleportLinkNetwork.Frequency> key = actor.getNetworkKey();
		if (!networksInWorld.containsKey(key))
			networksInWorld.put(key, new LinkedHashSet<>());
		return networksInWorld.get(key);
	}

	public Set<ITeleportLinkable> getNetworkOf(LevelAccessor level, Couple<TeleportLinkNetwork.Frequency> key ) {
		Map<Couple<TeleportLinkNetwork.Frequency>, Set<ITeleportLinkable>> networksInWorld = networksIn(level);
		if (!networksInWorld.containsKey(key))
			networksInWorld.put(key, new LinkedHashSet<>());
		return networksInWorld.get(key);
	}

	public void addToNetwork(LevelAccessor world, ITeleportLinkable actor) {
		getNetworkOf(world, actor).add(actor);
		updateNetworkOf(world, actor);
	}

	public void removeFromNetwork(LevelAccessor world, ITeleportLinkable actor) {
		Set<ITeleportLinkable> network = getNetworkOf(world, actor);
		network.remove(actor);
		if (network.isEmpty()) {
			networksIn(world).remove(actor.getNetworkKey());
			return;
		}
		updateNetworkOf(world, actor);
	}

	public void updateNetworkOf(LevelAccessor world, ITeleportLinkable actor) {
		Set<ITeleportLinkable> network = getNetworkOf(world, actor);
		globalPowerVersion.incrementAndGet();

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

		}

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

	public Map<Couple<TeleportLinkNetwork.Frequency>, Set<ITeleportLinkable>> networksIn(LevelAccessor world) {
		if (!connections.containsKey(world)) {
			MechanicalTeleporter.LOGGER.warn("Tried to Access unprepared network space of " + WorldHelper.getDimensionID(world));
			return new HashMap<>();
		}
		return connections.get(world);
	}

	public List<ITeleportLinkable> getTeleporters(Level level) {
		ArrayList<ITeleportLinkable> allTeleporters = new ArrayList<>();
		networksIn(level).forEach((frequencies, iTeleportLinkables) -> {
            allTeleporters.addAll(iTeleportLinkables);
		});
		return allTeleporters;
	}
	public static class Frequencies {
		public static Couple<Frequency> from(ItemStack first, ItemStack second){
			return Couple.create(Frequency.of(first),Frequency.of(second));
		}
	}
	public static class Frequency {
		public static final TeleportLinkNetwork.Frequency EMPTY = new TeleportLinkNetwork.Frequency(ItemStack.EMPTY);
		private static final Map<Item, TeleportLinkNetwork.Frequency> simpleFrequencies = new IdentityHashMap<>();
		private ItemStack stack;
		private Item item;
		private int color;

		public static StreamCodec<RegistryFriendlyByteBuf, Frequency> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.registry(Registries.ITEM), i -> i.item,
				Frequency::new
		);

		public static TeleportLinkNetwork.Frequency of(ItemStack stack) {
			if (stack.isEmpty())
				return EMPTY;
			if (stack.getComponents().isEmpty())
				return simpleFrequencies.computeIfAbsent(stack.getItem(), $ -> new TeleportLinkNetwork.Frequency(stack));
			return new TeleportLinkNetwork.Frequency(stack);
		}
		private Frequency(Item item){
			this(new ItemStack(item));
		}
		private Frequency(ItemStack stack) {
			this.stack = stack;
			item = stack.getItem();
			color = stack.has(DataComponents.DYED_COLOR) ? stack.get(DataComponents.DYED_COLOR).rgb() : -1;
		}

		public ItemStack getStack() {
			return stack;
		}

		@Override
		public int hashCode() {
			return (item.hashCode() * 31) ^ color;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			return obj instanceof Frequency && ((Frequency) obj).item == item && ((Frequency) obj).color == color;
		}

	}
}
