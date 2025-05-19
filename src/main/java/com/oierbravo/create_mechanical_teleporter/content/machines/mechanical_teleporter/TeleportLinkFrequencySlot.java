package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TeleportLinkFrequencySlot extends ValueBoxTransform.Dual {

	public TeleportLinkFrequencySlot(boolean first) {
		super(first);
	}

	protected Direction direction = Direction.NORTH;

	@Override
	public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
		Vec3 location = getSouthLocation();
		location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(getSide()), Direction.Axis.Y);
		location = VecHelper.rotateCentered(location, AngleHelper.verticalAngle(getSide()), Direction.Axis.X);
		if (isFirst())
			location = location.add(5 / 16f, 0, 0f);

		return location;
	}

	@Override
	public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
		float yRot = AngleHelper.horizontalAngle(getSide()) + 180;
		float xRot = getSide() == Direction.UP ? 90 : getSide() == Direction.DOWN ? 270 : 0;
		TransformStack.of(ms)
				.rotateYDegrees(yRot)
				.rotateXDegrees(xRot);
	}
	public Direction getSide() {
		return direction;
	}
	@Override
	public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
		return isSideActive(state, getSide()) && super.testHit(level, pos, state, localHit);
	}
	@Override
	public boolean shouldRender(LevelAccessor level, BlockPos pos, BlockState state) {
		return super.shouldRender(level, pos, state) && isSideActive(state, getSide());
	}
	protected boolean isSideActive(BlockState state, Direction direction) {
		return true;
	}
	protected Vec3 getSouthLocation() {
		return VecHelper.voxelSpace(10.5f, 8,  15f);
	}

	@Override
	public float getScale() {
		return .4975f;
	}

}
