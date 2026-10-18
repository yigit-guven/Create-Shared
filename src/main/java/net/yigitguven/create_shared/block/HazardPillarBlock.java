package net.yigitguven.create_shared.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HazardPillarBlock extends RotatedPillarBlock implements IWrenchable {
    public HazardPillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction.Axis currentAxis = state.getValue(AXIS);
        Direction.Axis nextAxis = switch (currentAxis) {
            case Y -> Direction.Axis.X;
            case X -> Direction.Axis.Z;
            case Z -> Direction.Axis.Y;
        };
        BlockState newState = state.setValue(AXIS, nextAxis);
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        IWrenchable.playRotateSound(level, pos);
        return InteractionResult.SUCCESS;
    }
}
