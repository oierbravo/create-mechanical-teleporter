package com.oierbravo.create_mechanical_teleporter.content.items.controller.simple;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import java.util.Vector;

public class SimpleTeleportControllerItemRenderer extends CustomRenderedItemModelRenderer<SimpleTeleportControllerModel> {

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

		boolean active = SimpleTeleportControllerClientHandler.MODE != SimpleTeleportControllerClientHandler.Mode.IDLE;
		equipProgress.chase(active ? 1 : 0, .2f, Chaser.EXP);
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
	protected void render(ItemStack stack, SimpleTeleportControllerModel model, PartialItemModelRenderer renderer,
		TransformType transformType, PoseStack ms, MultiBufferSource buffer, int light,
		int overlay) {
		renderNormal(stack, model, renderer, transformType, ms, light);
	}

	protected static void renderNormal(ItemStack stack, SimpleTeleportControllerModel model,
	  	PartialItemModelRenderer renderer, TransformType transformType, PoseStack ms,
  		int light) {
		render(stack, model, renderer, transformType, ms, light, RenderType.NORMAL, false, false);
	}

	public static void renderInLectern(ItemStack stack, SimpleTeleportControllerModel model,
	  	PartialItemModelRenderer renderer, TransformType transformType, PoseStack ms,
  		int light, boolean active, boolean renderDepression) {
		render(stack, model, renderer, transformType, ms, light, RenderType.LECTERN, active, renderDepression);
	}

	protected static void render(ItemStack stack, SimpleTeleportControllerModel model,
	  	PartialItemModelRenderer renderer, TransformType transformType, PoseStack ms,
  		int light, RenderType renderType, boolean active, boolean renderDepression) {
		float pt = AnimationTickHolder.getPartialTicks();
		TransformStack msr = TransformStack.cast(ms);

		ms.pushPose();

		if (renderType == RenderType.NORMAL) {
			Minecraft mc = Minecraft.getInstance();
			boolean rightHanded = mc.options.mainHand().get() == HumanoidArm.RIGHT;
			TransformType mainHand =
					rightHanded ? TransformType.FIRST_PERSON_RIGHT_HAND : TransformType.FIRST_PERSON_LEFT_HAND;
			TransformType offHand =
					rightHanded ? TransformType.FIRST_PERSON_LEFT_HAND : TransformType.FIRST_PERSON_RIGHT_HAND;

			active = false;
			boolean noControllerInMain = !ModItems.SIMPLE_TELEPORT_CONTROLLER.isIn(mc.player.getMainHandItem());

			if (transformType == mainHand || (transformType == offHand && noControllerInMain)) {
				float equip = equipProgress.getValue(pt);
				int handModifier = transformType == TransformType.FIRST_PERSON_LEFT_HAND ? -1 : 1;
				msr.translate(0, equip / 4, equip / 4 * handModifier);
				msr.rotateY(equip * -30 * handModifier);
				msr.rotateZ(equip * -30);
				active = true;
			}

			if (transformType == TransformType.GUI) {
				if (stack == mc.player.getMainHandItem())
					active = true;
				if (stack == mc.player.getOffhandItem() && noControllerInMain)
					active = true;
			}

			active &= SimpleTeleportControllerClientHandler.MODE != SimpleTeleportControllerClientHandler.Mode.IDLE;

			renderDepression = true;
		}

		renderer.render( model.getOriginalModel(), light);
		ms.popPose();
		BakedModel button = model.getPartial("button");
		float s = 1 / 16f;
		float b = s * -.75f;
		int index = 0;

		if (renderType == RenderType.NORMAL) {
			if (SimpleTeleportControllerClientHandler.MODE == SimpleTeleportControllerClientHandler.Mode.BIND) {
				int i = (int) Mth.lerp((Mth.sin(AnimationTickHolder.getRenderTime() / 4f) + 1) / 2, 5, 15);
				light = i << 20;
			}
		}

		ms.pushPose();
		msr.translate(2 * s, 0, 8 * s);
		renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);

		ms.popPose();


	}

	protected static void renderButton(PartialItemModelRenderer renderer, PoseStack ms, int light, float pt, BakedModel button,
		float b, int index, boolean renderDepression) {
		ms.pushPose();
		if (renderDepression) {
			float depression = b * buttons.get(index).getValue(pt);
			ms.translate(0, depression, 0);
		}
		renderer.renderSolid(button, light);
		ms.popPose();
	}

	@Override
	public SimpleTeleportControllerModel createModel(BakedModel originalModel) {
		return new SimpleTeleportControllerModel(originalModel);
	}

	protected enum RenderType {
		NORMAL, LECTERN;
	}

}
