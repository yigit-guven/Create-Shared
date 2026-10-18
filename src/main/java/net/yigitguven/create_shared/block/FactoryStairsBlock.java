package net.yigitguven.create_shared.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FactoryStairsBlock extends StairBlock implements IWrenchable {
    public FactoryStairsBlock(BlockState baseState, Properties properties) {
        super(baseState, properties);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction currentFacing = state.getValue(FACING);
        Direction nextFacing = currentFacing.getClockWise();
        BlockState newState = state.setValue(FACING, nextFacing);
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        IWrenchable.playRotateSound(level, pos);
        return InteractionResult.SUCCESS;
    }
}
