package com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters;

import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import com.oierbravo.create_mechanical_teleporter.registrate.ModDataComponents;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class TeleporterBlockItem extends AbstractTeleporterBlockItem {

	public TeleporterBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public boolean isFoil(@NotNull ItemStack pStack) {
		return TeleporterItemUtils.isTuned(pStack);
	}


	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext tooltipContext,
								@NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag);

		UUID freqId = stack.get(ModDataComponents.TELEPORTER_FREQUENCY);
		if(freqId == null)
			return;

		CreateLang.translate("logistically_linked.tooltip")
			.style(ChatFormatting.GOLD)
			.addTo(tooltipComponents);

		CreateLang.translate("logistically_linked.tooltip_clear")
			.style(ChatFormatting.GRAY)
			.addTo(tooltipComponents);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		if (!player.isShiftKeyDown())
			return super.use(level, player, usedHand);

		ItemStack stack = player.getItemInHand(usedHand);
		TeleporterItemUtils.clearFrequency(stack, player);
		return super.use(level, player, usedHand);
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext pContext) {
		ItemStack stack = pContext.getItemInHand();
		BlockPos pos = pContext.getClickedPos();
		Level level = pContext.getLevel();
		Player player = pContext.getPlayer();

		if (player == null)
			return InteractionResult.FAIL;
		if (player.isShiftKeyDown())
			return super.useOn(pContext);

		TeleporterFrequency teleporterFrequency = TeleporterFrequency.from(level.getBlockEntity(pos));

		boolean tuned = TeleporterItemUtils.isTuned(stack);

		if (teleporterFrequency != null) {
			if (level.isClientSide)
				return InteractionResult.SUCCESS;
			if (!teleporterFrequency.mayInteractMessage(player))
				return InteractionResult.SUCCESS;

			TeleporterItemUtils.setFrequency(stack, player, teleporterFrequency.freqId(), "");
			return InteractionResult.SUCCESS;
		}

		InteractionResult useOn = super.useOn(pContext);
		if (level.isClientSide || useOn == InteractionResult.FAIL)
			return useOn;

		player.displayClientMessage(tuned ? CreateLang.translateDirect("logistically_linked.connected")
			: CreateLang.translateDirect("logistically_linked.new_network_started"), true);
		return useOn;
	}
}
