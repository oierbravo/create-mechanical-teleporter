package com.oierbravo.create_mechanical_teleporter.events;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

	@SubscribeEvent
	public static void onTick(ClientTickEvent.Post event) {
		if (!isGameActive())
			return;

		//Level world = Minecraft.getInstance().level;
		//if (event.phase == Phase.START) {
			//TeleportWandClientHandler.tick();
			return;
		//}

		//TeleportLinkRenderer.tick();
	}


	protected static boolean isGameActive() {
		return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
	}

}
