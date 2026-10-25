package net.yigitguven.create_shared.block;

import java.util.EnumMap;
import java.util.Map;

import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.yigitguven.create_shared.block.entity.AbstractVentBlockEntity;
import net.yigitguven.create_shared.block.entity.FactoryVentDuctBlockEntity;
import net.yigitguven.create_shared.registry.ModBlockEntities;

public class FactoryVentDuctBlock extends Block implements IWrenchable, EntityBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

    public FactoryVentDuctBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(SOUTH, false)
            .setValue(EAST, false)
            .setValue(WEST, false)
            .setValue(UP, false)
            .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    public static boolean canConnectTo(LevelReader level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);

        // Connect to other ducts
        if (neighborState.getBlock() instanceof FactoryVentDuctBlock) {
            return true;
        }

        // Connect to vents
        if (neighborState.getBlock() instanceof FactoryVentBlock) {
            Direction ventFacing = neighborState.getValue(FactoryVentBlock.FACING);
            // Duct is at the back of the vent
            if (ventFacing == direction) {
                return true;
            }
            // Or vent front is facing into duct and is open
            if (ventFacing.getOpposite() == direction && neighborState.getValue(FactoryVentBlock.OPEN)) {
                return true;
            }
        }

        // Connect to Create Encased Fan if the fan's fanning side points towards this duct
        if (neighborState.getBlock() instanceof com.simibubi.create.content.kinetics.fan.EncasedFanBlock) {
            return neighborState.getValue(com.simibubi.create.content.kinetics.fan.EncasedFanBlock.FACING) == direction.getOpposite();
        }

        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        for (Direction dir : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), canConnectTo(level, pos, dir));
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction), canConnectTo(level, pos, direction));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (level.isClientSide()) return;

        BlockState newState = state;
        for (Direction dir : Direction.values()) {
            newState = newState.setValue(PROPERTY_BY_DIRECTION.get(dir), canConnectTo(level, pos, dir));
        }
        if (newState != state) {
            level.setBlock(pos, newState, Block.UPDATE_ALL);
        }
        notifyNetwork(level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return VentVoxelShapes.getDuctShape(
            state.getValue(DOWN),
            state.getValue(UP),
            state.getValue(NORTH),
            state.getValue(SOUTH),
            state.getValue(WEST),
            state.getValue(EAST)
        );
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return state.getValue(UP) || state.getValue(DOWN);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> state
                .setValue(NORTH, state.getValue(SOUTH))
                .setValue(EAST, state.getValue(WEST))
                .setValue(SOUTH, state.getValue(NORTH))
                .setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90 -> state
                .setValue(NORTH, state.getValue(EAST))
                .setValue(EAST, state.getValue(SOUTH))
                .setValue(SOUTH, state.getValue(WEST))
                .setValue(WEST, state.getValue(NORTH));
            case CLOCKWISE_90 -> state
                .setValue(NORTH, state.getValue(WEST))
                .setValue(EAST, state.getValue(NORTH))
                .setValue(SOUTH, state.getValue(EAST))
                .setValue(WEST, state.getValue(SOUTH));
            default -> state;
        };
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> state
                .setValue(NORTH, state.getValue(SOUTH))
                .setValue(SOUTH, state.getValue(NORTH));
            case FRONT_BACK -> state
                .setValue(EAST, state.getValue(WEST))
                .setValue(WEST, state.getValue(EAST));
            default -> super.mirror(state, mirror);
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FactoryVentDuctBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.FACTORY_VENT_DUCT.get()
            ? (lvl, p, st, be) -> FactoryVentDuctBlockEntity.tick(lvl, p, st, (FactoryVentDuctBlockEntity) be)
            : null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            for (Direction dir : Direction.values()) {
                BlockEntity be = level.getBlockEntity(pos.relative(dir));
                if (be instanceof AbstractVentBlockEntity avbe) {
                    avbe.markNetworkDirty();
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void notifyNetwork(Level level, BlockPos pos) {
        if (level.isClientSide()) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AbstractVentBlockEntity avbe) {
            avbe.markNetworkDirty();
        }
    }
}
