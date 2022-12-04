package com.oierbravo.create_mechanical_teleporter.events;

import com.oierbravo.create_mechanical_teleporter.content.items.controller.simple.SimpleTeleportControllerClientHandler;
import com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport.TeleportLinkRenderer;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple.LinkedTeleportControllerClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

	@SubscribeEvent
	public static void onTick(ClientTickEvent event) {
		if (!isGameActive())
			return;

		Level world = Minecraft.getInstance().level;
		if (event.phase == Phase.START) {
			LinkedTeleportControllerClientHandler.tick();
			SimpleTeleportControllerClientHandler.tick();
			return;
		}

		TeleportLinkRenderer.tick();
	}


	protected static boolean isGameActive() {
		return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
	}

	@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {
		@SubscribeEvent
		public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
			// Register overlays in reverse order
			event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "simple_teleport_controller", SimpleTeleportControllerClientHandler.OVERLAY);
		}


	}

}
