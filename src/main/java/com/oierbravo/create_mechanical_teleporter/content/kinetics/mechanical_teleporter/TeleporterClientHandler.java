package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter;

import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterBehavior;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;

public class TeleporterClientHandler {

	private static UUID previouslyHeldFrequency;

	public static void tick() {
		previouslyHeldFrequency = null;

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null)
			return;
		ItemStack mainHandItem = player.getMainHandItem();
		if (!(mainHandItem.getItem() instanceof TeleporterBlockItem)
			|| !TeleporterBlockItem.isTuned(mainHandItem))
			return;

		CompoundTag tag = mainHandItem.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
		if (!tag.hasUUID("Freq"))
			return;

		UUID uuid = tag.getUUID("Freq");
		previouslyHeldFrequency = uuid;

		for (TeleporterBehavior behaviour : TeleporterBehavior.getAllPresent(uuid, false, true)) {
			SmartBlockEntity be = behaviour.blockEntity;
			VoxelShape shape = be.getBlockState()
				.getShape(player.level(), be.getBlockPos());
			if (shape.isEmpty())
				continue;
			if (!player.blockPosition()
				.closerThan(be.getBlockPos(), 64))
				continue;
			for (int i = 0; i < shape.toAabbs()
				.size(); i++) {
				AABB aabb = shape.toAabbs()
					.get(i);
				Outliner.getInstance()
					.showAABB(Pair.of(behaviour, i), aabb.inflate(-1 / 128f)
						.move(be.getBlockPos()), 2)
					.lineWidth(1 / 32f)
					.disableLineNormals()
					.colored(AnimationTickHolder.getTicks() % 16 < 8 ? 0x009999 : 0x008888);
			}

		}
	}
}
