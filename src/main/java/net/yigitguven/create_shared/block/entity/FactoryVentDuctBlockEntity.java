package net.yigitguven.create_shared.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.yigitguven.create_shared.registry.ModBlockEntities;

public class FactoryVentDuctBlockEntity extends AbstractVentBlockEntity {

    public FactoryVentDuctBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FACTORY_VENT_DUCT.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FactoryVentDuctBlockEntity blockEntity) {
        if (level.isClientSide()) return;
        VentNetwork.recalculateIfDirtyOrPeriodic(level, pos, blockEntity);
    }
}
