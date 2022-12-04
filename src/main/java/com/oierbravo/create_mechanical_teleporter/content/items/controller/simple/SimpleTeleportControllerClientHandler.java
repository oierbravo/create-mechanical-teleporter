package com.oierbravo.create_mechanical_teleporter.content.items.controller.simple;

import com.mojang.blaze3d.vertex.PoseStack;
import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple.LinkedTeleportControllerStopLecternPacket;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.oierbravo.create_mechanical_teleporter.registrate.ModPackets;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.linked.LinkBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.ControlsUtil;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.*;

public class SimpleTeleportControllerClientHandler {

	public static final IGuiOverlay OVERLAY = SimpleTeleportControllerClientHandler::renderOverlay;

	public static Mode MODE = Mode.IDLE;
	public static int PACKET_RATE = 5;
	private static BlockPos lecternPos;
	private static BlockPos selectedLocation = BlockPos.ZERO;
	private static int packetCooldown;

	public static void toggleBindMode(BlockPos location) {
		selectedLocation = location;
	}

	public static void activate() {

		if(MODE == Mode.IDLE){
			ModPackets.channel.sendToServer(new SimpleTeleportControllerActivatePacket( true));
		}
	}

	public static void activateInLectern(BlockPos lecternAt) {
		if (MODE == Mode.IDLE) {
			MODE = Mode.ACTIVE;
			lecternPos = lecternAt;
		}
	}

	public static void deactivateInLectern() {
		if (MODE == Mode.ACTIVE && inLectern()) {
			MODE = Mode.IDLE;
			onReset();
		}
	}

	public static boolean inLectern() {
		return lecternPos != null;
	}

	protected static void onReset() {
		ControlsUtil.getControls()
			.forEach(kb -> kb.setDown(ControlsUtil.isActuallyPressed(kb)));
		packetCooldown = 0;
		selectedLocation = BlockPos.ZERO;

		if (inLectern())
			ModPackets.channel.sendToServer(new LinkedTeleportControllerStopLecternPacket(lecternPos));
		lecternPos = null;

		SimpleTeleportControllerItemRenderer.resetButtons();
	}

	public static void tick() {
		SimpleTeleportControllerItemRenderer.tick();

		if (packetCooldown > 0)
			packetCooldown--;

		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		ItemStack heldItem = player.getMainHandItem();

		if (player.isSpectator()) {
			MODE = Mode.IDLE;
			onReset();
			return;
		}

		if (!inLectern() && !ModItems.SIMPLE_TELEPORT_CONTROLLER.isIn(heldItem)) {
			heldItem = player.getOffhandItem();
			if (!ModItems.SIMPLE_TELEPORT_CONTROLLER.isIn(heldItem)) {
				MODE = Mode.IDLE;
				onReset();
				return;
			}
		}

		if (inLectern() && AllBlocks.LECTERN_CONTROLLER.get()
			.getTileEntityOptional(mc.level, lecternPos)
			.map(te -> !te.isUsedBy(mc.player))
			.orElse(true)) {
			deactivateInLectern();
			return;
		}

		/*if (mc.screen != null) {
			MODE = Mode.IDLE;
			onReset();
			return;
		}

		if (InputConstants.isKeyDown(mc.getWindow()
			.getWindow(), GLFW.GLFW_KEY_ESCAPE)) {
			MODE = Mode.IDLE;
			onReset();
			return;
		}*/




		if (MODE == Mode.BIND) {
			VoxelShape shape = mc.level.getBlockState(selectedLocation)
				.getShape(mc.level, selectedLocation);
			if (!shape.isEmpty())
				CreateClient.OUTLINER.showAABB("controller", shape.bounds()
					.move(selectedLocation))
					.colored(0xB73C2D)
					.lineWidth(1 / 16f);
			LinkBehaviour linkBehaviour = TileEntityBehaviour.get(mc.level, selectedLocation, LinkBehaviour.TYPE);
			if (linkBehaviour != null) {
				ModPackets.channel.sendToServer(new SimpleTeleportControllerBindPacket( selectedLocation));
				Lang.translate("simple_teleport_controller.key_bound", "TODO")
						.sendStatus(mc.player);
			}
			MODE = Mode.IDLE;

		}

	}

	public static void renderOverlay(ForgeGui gui, PoseStack poseStack, float partialTicks, int width1,
		int height1) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui)
			return;

		if (MODE != Mode.IDLE)
			return;
		LocalPlayer player = mc.player;
		assert player != null;
		ItemStack heldItem = player.getMainHandItem();


		if(!ModItems.SIMPLE_TELEPORT_CONTROLLER.isIn(heldItem)){
			ItemStack offHandItem = player.getOffhandItem();

			if(!ModItems.SIMPLE_TELEPORT_CONTROLLER.isIn(offHandItem)){
				return;
			}
		}
		poseStack.pushPose();
		Screen tooltipScreen = new Screen(Components.immutableEmpty()) {
		};
		tooltipScreen.init(mc, width1, height1);

		/*Object[] keys = new Object[6];
		Vector<KeyMapping> controls = ControlsUtil.getControls();
		for (int i = 0; i < controls.size(); i++) {
			KeyMapping keyBinding = controls.get(i);
			keys[i] = keyBinding.getTranslatedKeyMessage()
				.getString();
		}*/

		List<Component> list = new ArrayList<>();
		list.add(Component.translatable("create_mechanical_teleporter.simple_teleport_controller.bind_position",selectedLocation.toShortString())
			.withStyle(ChatFormatting.GOLD));
		/*list.addAll(TooltipHelper.cutTextComponent(Lang.translateDirect("simple_teleport_controller.press_keybind", keys),
			ChatFormatting.GRAY, ChatFormatting.GRAY));*/

		int width = 0;
		int height = list.size() * mc.font.lineHeight;
		for (Component iTextComponent : list)
			width = Math.max(width, mc.font.width(iTextComponent));
		int x = (width1 / 3) - width / 2;
		int y = height1 - height - 24;

		// TODO
		tooltipScreen.renderComponentTooltip(poseStack, list, x, y);

		poseStack.popPose();
	}

	public static void activateBind(BlockPos pos) {
		selectedLocation = pos;
		Minecraft mc = Minecraft.getInstance();
		assert mc.level != null;
		VoxelShape shape = mc.level.getBlockState(pos)
				.getShape(mc.level, selectedLocation);
		if (!shape.isEmpty())
			CreateClient.OUTLINER.showAABB("controller", shape.bounds()
							.move(selectedLocation))
					.colored(0xB73C2D)
					.lineWidth(1 / 16f);
		LocalPlayer player = mc.player;
		AllSoundEvents.CONTROLLER_CLICK.playAt(player.level, player.blockPosition(), 1f, .5f, true);
		ModPackets.channel.sendToServer(new SimpleTeleportControllerBindPacket( selectedLocation));
	}


	public enum Mode {
		IDLE, ACTIVE, BIND
	}

}
