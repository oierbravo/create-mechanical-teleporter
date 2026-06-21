package com.oierbravo.create_mechanical_teleporter.events;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.PendingContraptionSeats;
import com.oierbravo.create_mechanical_teleporter.content.logistics.PendingTrainSeats;
import com.simibubi.create.Create;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class CommonEvents {
	@SubscribeEvent
	public static void onWorldTick(net.neoforged.neoforge.event.tick.LevelTickEvent.Post event) {
		//if (event.phase == Phase.START)
		//	return;
		Level world = event.getLevel();
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		MinecraftServer server = event.getServer();
		PendingTrainSeats.tick(server);
		PendingContraptionSeats.tick(server);
		if (server.getTickCount() % 20 == 0) {
			MechanicalTeleporter.TELEPORTERS.pruneTrainLinks(Create.RAILWAYS.trains.keySet());
			MechanicalTeleporter.TELEPORTERS.pruneContraptionLinks(server);
		}
	}
	@SubscribeEvent
	public static void onLoadWorld(LevelEvent.Load event) {
		LevelAccessor world = event.getLevel();
		MechanicalTeleporter.TELEPORTERS.levelLoaded(world);
	}

	/*@SubscribeEvent
	public static void onUnloadWorld(LevelEvent.Unload event) {
		LevelAccessor world = event.getLevel();
	}*/



}
