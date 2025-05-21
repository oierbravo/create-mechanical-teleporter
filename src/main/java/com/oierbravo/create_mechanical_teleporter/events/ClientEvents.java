package com.oierbravo.create_mechanical_teleporter.events;

import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterClientHandler;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterClientHandler;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

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
		HandTeleporterClientHandler.tick();

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

}
