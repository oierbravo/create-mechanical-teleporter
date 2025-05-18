package com.oierbravo.create_mechanical_teleporter.events;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.items.wand.TeleportWandServerHandler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber
public class CommonEvents {
	@SubscribeEvent
	public static void onWorldTick(net.neoforged.neoforge.event.tick.LevelTickEvent.Post event) {
		//if (event.phase == Phase.START)
		//	return;
		Level world = event.getLevel();
		TeleportWandServerHandler.tick(world);
		//ControlsServerHandler.tick(world);
	}
	@SubscribeEvent
	public static void onLoadWorld(LevelEvent.Load event) {
		LevelAccessor world = event.getLevel();
		MechanicalTeleporter.TELEPORT_NETWORK_HANDLER.onLoadWorld(world);
	}

	@SubscribeEvent
	public static void onUnloadWorld(LevelEvent.Unload event) {
		LevelAccessor world = event.getLevel();
		MechanicalTeleporter.TELEPORT_NETWORK_HANDLER.onUnloadWorld(world);
	}



}
