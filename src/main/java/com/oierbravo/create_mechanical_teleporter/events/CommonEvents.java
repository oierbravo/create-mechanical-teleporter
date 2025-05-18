package com.oierbravo.create_mechanical_teleporter.events;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.items.wand.TeleportWandServerHandler;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsServerHandler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class CommonEvents {
	@SubscribeEvent
	public static void onWorldTick(LevelTickEvent event) {
		if (event.phase == Phase.START)
			return;
		Level world = event.level;
		TeleportWandServerHandler.tick(world);
		ControlsServerHandler.tick(world);
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
