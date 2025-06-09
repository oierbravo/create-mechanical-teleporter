package com.oierbravo.create_mechanical_teleporter.registrate;

import com.simibubi.create.AllShapes;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.core.Direction.SOUTH;

public class ModShapes {
    public static final VoxelShape TELEPORTERS = shape(0, 0, 0, 16, 2, 16)
            .add(2, 0, 2, 14, 14, 14)
            .add(0,14,0,16,16,16)
            .build();


    public static final VoxelShape VERTICAL_TABLET_SHAPE = cuboid(5, 4, 0, 11, 12, 2), HORIZONTAL_TABLET_SHAPE = cuboid(5, 0, 4, 11, 2, 12);


    public static final VoxelShaper HAND_TELEPORTER = shape(VERTICAL_TABLET_SHAPE).forDirectional(SOUTH)
			.withVerticalShapes(HORIZONTAL_TABLET_SHAPE);

    private static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }

    private static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }
    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }

}
