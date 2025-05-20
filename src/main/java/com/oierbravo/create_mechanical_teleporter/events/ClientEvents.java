package com.oierbravo.create_mechanical_teleporter.events;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.global.TeleporterClientHandler;
import com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport.TeleportHandler;
import com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport.TeleportersDisplayOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
	private static boolean LAST_JUMPING = false;
	private static boolean LAST_SNEAKING = false;
	private static int JUMP_COOLDOWN = 0;

	@SubscribeEvent
	public static void onTick(ClientTickEvent.Post event) {
		if (!isGameActive())
			return;
		TeleporterClientHandler.tick();

	}


	protected static boolean isGameActive() {
		return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
	}
	@SubscribeEvent
	public static void movementInputUpdate(MovementInputUpdateEvent event) {
		Input input = event.getInput();
		Player player = event.getEntity();
		boolean isNewJump = input.jumping && !LAST_JUMPING;
		LAST_JUMPING = input.jumping;
		boolean isNewCrouch = input.shiftKeyDown && !LAST_SNEAKING;
		LAST_SNEAKING = input.shiftKeyDown;

		if (!player.onGround() || !TeleportHandler.canBlockTeleport(player)) {
			JUMP_COOLDOWN = 0;
			return;
		}
		if (isNewJump) {

			boolean success = TeleportHandler.blockTeleport(player.level(), player, true);

			if (success) {
				JUMP_COOLDOWN = 7;
			} else {
				JUMP_COOLDOWN = 0;
			}
		}/* else if (isNewCrouch) {
			boolean success = TeleportHandler.blockElevatorTeleport(player.level(), player, Direction.DOWN, true);
			if (!success) {
				TeleportHandler.blockTeleport(player.level(), player, true);
			}
		}*/

		if (JUMP_COOLDOWN > 0) {
			JUMP_COOLDOWN -= 1;
			input.jumping = false;
		}
	}

	@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {
		@SubscribeEvent
		public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
			event.registerAbove(VanillaGuiLayers.AIR_LEVEL, ModConstants.asResource("teleporters_display"), TeleportersDisplayOverlay.INSTANCE);
		}
	}
}
