package com.oierbravo.create_mechanical_teleporter.content.items.wand;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TeleportWandItemRenderer extends CustomRenderedItemModelRenderer {

	protected static final PartialModel CORE = PartialModel.of(ModConstants.asResource("item/teleport_wand/core"));
	protected static final PartialModel CORE_GLOW = PartialModel.of(ModConstants.asResource("item/teleport_wand/core_glow"));



	@Override
	protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		float worldTime = AnimationTickHolder.getRenderTime() / 20;
		int maxLight = LightTexture.FULL_BRIGHT;
		Player player = Minecraft.getInstance().player;
		boolean playerHasAir = player != null && player.getPersistentData().contains("PlayerHasAir") && player.getPersistentData().getInt("PlayerHasAir") > MConfigs.server().wand.airAmount.get();

        renderer.render(model.getOriginalModel(), light);

		if(playerHasAir)
			renderer.renderGlowing(CORE_GLOW.get(), maxLight);
		else
			renderer.renderSolidGlowing(CORE.get(), maxLight);

		float floating = Mth.sin(worldTime) * .05f;
		float angle = worldTime * -10 % 360;

		ms.translate(0, floating, 0);
		ms.mulPose(Axis.YP.rotationDegrees(angle));

		if(playerHasAir)
			renderer.renderGlowing(CORE_GLOW.get(), maxLight);
		else
			renderer.renderSolidGlowing(CORE.get(), maxLight);
	}
}
