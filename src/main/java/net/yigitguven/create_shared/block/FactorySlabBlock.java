package net.yigitguven.create_shared.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public class FactorySlabBlock extends SlabBlock implements IWrenchable {
    public FactorySlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        SlabType type = state.getValue(TYPE);
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (type == SlabType.BOTTOM) {
            level.setBlock(pos, state.setValue(TYPE, SlabType.TOP), Block.UPDATE_ALL);
            IWrenchable.playRotateSound(level, pos);
            return InteractionResult.SUCCESS;
        } else if (type == SlabType.TOP) {
            level.setBlock(pos, state.setValue(TYPE, SlabType.BOTTOM), Block.UPDATE_ALL);
            IWrenchable.playRotateSound(level, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
