package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Vector;

public class HandTeleporterItemRenderer extends CustomRenderedItemModelRenderer {

	protected static final PartialModel BUTTON = PartialModel.of(ModConstants.asResource("block/hand_teleporter/button"));
	protected static final PartialModel BUTTON_ACTIVE = PartialModel.of(ModConstants.asResource("block/hand_teleporter/button_active"));
	protected static final PartialModel ANTENNA = PartialModel.of(ModConstants.asResource("block/hand_teleporter/antenna"));
	protected static final PartialModel ANTENNA_ACTIVE = PartialModel.of(ModConstants.asResource("block/hand_teleporter/antenna_active"));

	static LerpedFloat equipProgress;
	static Vector<LerpedFloat> buttons;

	static {
		equipProgress = LerpedFloat.linear()
			.startWithValue(0);
		buttons = new Vector<>(1);
		for (int i = 0; i < 1; i++)
			buttons.add(LerpedFloat.linear()
				.startWithValue(0));
	}

	static void tick() {
		if (Minecraft.getInstance()
			.isPaused())
			return;

		boolean active = true;
		equipProgress.chase(active ? 1 : 0, .2f, LerpedFloat.Chaser.EXP);
		equipProgress.tickChaser();

		if (!active)
			return;

		for (int i = 0; i < buttons.size(); i++) {
			LerpedFloat lerpedFloat = buttons.get(i);
			lerpedFloat.tickChaser();
		}
	}

	static void resetButtons() {
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).startWithValue(0);
		}
	}

	@Override
	protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
						  ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light,
						  int overlay) {
		renderNormal(stack, model, renderer, transformType, ms, light);
	}

	protected static void renderNormal(ItemStack stack, CustomRenderedItemModel model,
									   PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms,
									   int light) {
		render(stack, model, renderer, transformType, ms, light,  false, false);
	}



	protected static void render(ItemStack stack, CustomRenderedItemModel model,
                                 PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms,
                                 int light, boolean active, boolean renderDepression) {
		float pt = AnimationTickHolder.getPartialTicks();
		var msr = TransformStack.of(ms);

		ms.pushPose();

		Minecraft mc = Minecraft.getInstance();
		boolean rightHanded = mc.options.mainHand().get() == HumanoidArm.RIGHT;
		ItemDisplayContext mainHand =
				rightHanded ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
		ItemDisplayContext offHand =
				rightHanded ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;

		TeleporterFrequency teleporterFrequency = TeleporterFrequency.from(stack);

		active = teleporterFrequency.isPresent();
		boolean noControllerInMain = !(mc.player.getMainHandItem().getItem() instanceof HandTeleporterBlockItem);

		if (transformType == mainHand || (transformType == offHand && noControllerInMain)) {
			float equip = equipProgress.getValue(pt);
			int handModifier = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1;
			msr.translate(0, equip / 4, equip / 4 * handModifier);
			msr.rotateYDegrees(equip * -30 * handModifier);
			msr.rotateZDegrees(equip * -30);
		}

		renderer.render(model.getOriginalModel(), light);

		BakedModel button = (active) ? BUTTON_ACTIVE.get() : BUTTON.get();
		renderer.renderSolid(button, light);

		BakedModel antenna = (active) ? ANTENNA_ACTIVE.get() : ANTENNA.get();
		renderer.render(antenna, light);

		ms.popPose();
	}


}
