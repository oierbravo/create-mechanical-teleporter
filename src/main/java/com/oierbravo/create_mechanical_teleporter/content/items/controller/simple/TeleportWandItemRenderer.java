package com.oierbravo.create_mechanical_teleporter.content.items.controller.simple;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import java.util.Vector;

public class TeleportWandItemRenderer extends CustomRenderedItemModelRenderer {

	protected static final PartialModel BITS = new PartialModel(Create.asResource("item/wand_of_symmetry/bits"));
	protected static final PartialModel CORE = new PartialModel(MechanicalTeleporter.asResource("item/teleport_wand/core"));
	protected static final PartialModel CORE_GLOW = new PartialModel(MechanicalTeleporter.asResource("item/teleport_wand/core_glow"));

	@Override
	protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemTransforms.TransformType transformType,
						  PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		float worldTime = AnimationTickHolder.getRenderTime() / 20;
		int maxLight = LightTexture.FULL_BRIGHT;

		renderer.render(model.getOriginalModel(), light);
		renderer.renderSolidGlowing(CORE.get(), maxLight);
		renderer.renderGlowing(CORE_GLOW.get(), maxLight);

		float floating = Mth.sin(worldTime) * .05f;
		float angle = worldTime * -10 % 360;

		ms.translate(0, floating, 0);
		ms.mulPose(Vector3f.YP.rotationDegrees(angle));

		renderer.renderSolidGlowing(CORE.get(), maxLight);
		renderer.renderGlowing(CORE_GLOW.get(), maxLight);

		//renderer.renderGlowing(BITS.get(), maxLight);
	}

}
