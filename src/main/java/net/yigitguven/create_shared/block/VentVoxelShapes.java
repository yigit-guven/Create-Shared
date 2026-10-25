package net.yigitguven.create_shared.block;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class VentVoxelShapes {
    private VentVoxelShapes() {}

    // 12 outer frame edges (2-pixel thick)
    public static final VoxelShape FRAME;

    // 6 wall panels for the faces (12x12x2 pixels)
    public static final Map<Direction, VoxelShape> PANELS = new EnumMap<>(Direction.class);

    // Precomputed shapes for FactoryVentDuctBlock (64 states: 6 boolean directions)
    private static final VoxelShape[] DUCT_SHAPES = new VoxelShape[64];

    // Precomputed shapes for FactoryVentBlock: [facing.ordinal()][open ? 1 : 0][connected ? 1 : 0]
    private static final VoxelShape[][][] VENT_SHAPES = new VoxelShape[6][2][2];

    static {
        // Pillars
        VoxelShape pNW = Block.box(0, 0, 0, 2, 16, 2);
        VoxelShape pNE = Block.box(14, 0, 0, 16, 16, 2);
        VoxelShape pSW = Block.box(0, 0, 14, 2, 16, 16);
        VoxelShape pSE = Block.box(14, 0, 14, 16, 16, 16);

        // X-bars
        VoxelShape bN = Block.box(2, 0, 0, 14, 2, 2);
        VoxelShape bS = Block.box(2, 0, 14, 14, 2, 16);
        VoxelShape tN = Block.box(2, 14, 0, 14, 16, 2);
        VoxelShape tS = Block.box(2, 14, 14, 14, 16, 16);

        // Z-bars
        VoxelShape bW = Block.box(0, 0, 2, 2, 2, 14);
        VoxelShape bE = Block.box(14, 0, 2, 16, 2, 14);
        VoxelShape tW = Block.box(0, 14, 2, 2, 16, 14);
        VoxelShape tE = Block.box(14, 14, 2, 16, 16, 14);

        FRAME = Shapes.or(
            pNW, pNE, pSW, pSE,
            bN, bS, tN, tS,
            bW, bE, tW, tE
        );

        PANELS.put(Direction.DOWN, Block.box(2, 0, 2, 14, 2, 14));
        PANELS.put(Direction.UP, Block.box(2, 14, 2, 14, 16, 14));
        PANELS.put(Direction.NORTH, Block.box(2, 2, 0, 14, 14, 2));
        PANELS.put(Direction.SOUTH, Block.box(2, 2, 14, 14, 14, 16));
        PANELS.put(Direction.WEST, Block.box(0, 2, 2, 2, 14, 14));
        PANELS.put(Direction.EAST, Block.box(14, 2, 2, 16, 14, 14));

        // Mid-height pillars (Y: 2..14)
        VoxelShape mNW = Block.box(0, 2, 0, 2, 14, 2);
        VoxelShape mNE = Block.box(14, 2, 0, 16, 14, 2);
        VoxelShape mSW = Block.box(0, 2, 14, 2, 14, 16);
        VoxelShape mSE = Block.box(14, 2, 14, 16, 14, 16);

        // 8 Corner cubes (2x2x2)
        VoxelShape cDNW = Block.box(0, 0, 0, 2, 2, 2);
        VoxelShape cDNE = Block.box(14, 0, 0, 16, 2, 2);
        VoxelShape cDSW = Block.box(0, 0, 14, 2, 2, 16);
        VoxelShape cDSE = Block.box(14, 0, 14, 16, 2, 16);
        VoxelShape cUNW = Block.box(0, 14, 0, 2, 16, 2);
        VoxelShape cUNE = Block.box(14, 14, 0, 16, 16, 2);
        VoxelShape cUSW = Block.box(0, 14, 14, 2, 16, 16);
        VoxelShape cUSE = Block.box(14, 14, 14, 16, 16, 16);

        // Precompute DUCT_SHAPES
        for (int i = 0; i < 64; i++) {
            boolean down = (i & 1) != 0;
            boolean up = (i & 2) != 0;
            boolean north = (i & 4) != 0;
            boolean south = (i & 8) != 0;
            boolean west = (i & 16) != 0;
            boolean east = (i & 32) != 0;

            VoxelShape shape = Shapes.empty();
            if (!down) shape = Shapes.or(shape, PANELS.get(Direction.DOWN));
            if (!up) shape = Shapes.or(shape, PANELS.get(Direction.UP));
            if (!north) shape = Shapes.or(shape, PANELS.get(Direction.NORTH));
            if (!south) shape = Shapes.or(shape, PANELS.get(Direction.SOUTH));
            if (!west) shape = Shapes.or(shape, PANELS.get(Direction.WEST));
            if (!east) shape = Shapes.or(shape, PANELS.get(Direction.EAST));

            // 4 X-bars
            if (!down || !north) shape = Shapes.or(shape, bN);
            if (!down || !south) shape = Shapes.or(shape, bS);
            if (!up || !north) shape = Shapes.or(shape, tN);
            if (!up || !south) shape = Shapes.or(shape, tS);

            // 4 Z-bars
            if (!down || !west) shape = Shapes.or(shape, bW);
            if (!down || !east) shape = Shapes.or(shape, bE);
            if (!up || !west) shape = Shapes.or(shape, tW);
            if (!up || !east) shape = Shapes.or(shape, tE);

            // 4 Mid pillars
            if (!north || !west) shape = Shapes.or(shape, mNW);
            if (!north || !east) shape = Shapes.or(shape, mNE);
            if (!south || !west) shape = Shapes.or(shape, mSW);
            if (!south || !east) shape = Shapes.or(shape, mSE);

            // 8 Corner cubes
            if (!down || !north || !west) shape = Shapes.or(shape, cDNW);
            if (!down || !north || !east) shape = Shapes.or(shape, cDNE);
            if (!down || !south || !west) shape = Shapes.or(shape, cDSW);
            if (!down || !south || !east) shape = Shapes.or(shape, cDSE);
            if (!up || !north || !west) shape = Shapes.or(shape, cUNW);
            if (!up || !north || !east) shape = Shapes.or(shape, cUNE);
            if (!up || !south || !west) shape = Shapes.or(shape, cUSW);
            if (!up || !south || !east) shape = Shapes.or(shape, cUSE);

            DUCT_SHAPES[i] = shape.optimize();
        }

        // Precompute VENT_SHAPES
        for (Direction facing : Direction.values()) {
            Direction back = facing.getOpposite();
            for (int open = 0; open <= 1; open++) {
                for (int conn = 0; conn <= 1; conn++) {
                    VoxelShape shape = FRAME;

                    // The 4 perpendicular casing sides are always closed
                    for (Direction dir : Direction.values()) {
                        if (dir != facing && dir != back) {
                            shape = Shapes.or(shape, PANELS.get(dir));
                        }
                    }

                    // Front panel if not open
                    if (open == 0) {
                        shape = Shapes.or(shape, PANELS.get(facing));
                    }

                    // Back panel if not connected
                    if (conn == 0) {
                        shape = Shapes.or(shape, PANELS.get(back));
                    }

                    VENT_SHAPES[facing.ordinal()][open][conn] = shape.optimize();
                }
            }
        }
    }

    public static VoxelShape getDuctShape(boolean down, boolean up, boolean north, boolean south, boolean west, boolean east) {
        int index = (down ? 1 : 0) | (up ? 2 : 0) | (north ? 4 : 0) | (south ? 8 : 0) | (west ? 16 : 0) | (east ? 32 : 0);
        return DUCT_SHAPES[index];
    }

    public static VoxelShape getVentShape(Direction facing, boolean open, boolean connected) {
        return VENT_SHAPES[facing.ordinal()][open ? 1 : 0][connected ? 1 : 0];
    }
}
