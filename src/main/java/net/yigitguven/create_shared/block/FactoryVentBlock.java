package net.yigitguven.create_shared.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FactoryVentBlock extends Block implements IWrenchable {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public FactoryVentBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(OPEN, false)
            .setValue(POWERED, false)
            .setValue(CONNECTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, POWERED, CONNECTED);
    }

    public static boolean isConnectedAtBack(LevelReader level, BlockPos pos, Direction facing) {
        Direction back = facing.getOpposite();
        BlockPos backPos = pos.relative(back);
        BlockState backState = level.getBlockState(backPos);

        if (backState.getBlock() instanceof FactoryVentDuctBlock) {
            return true;
        }

        if (backState.getBlock() instanceof FactoryVentBlock) {
            Direction otherFacing = backState.getValue(FACING);
            // Connect if back-to-back or if other vent faces into this vent
            return otherFacing == back || (otherFacing == facing && backState.getValue(OPEN));
        }

        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getNearestLookingDirection().getOpposite();
        boolean powered = level.hasNeighborSignal(pos);
        boolean connected = isConnectedAtBack(level, pos, facing);

        return this.defaultBlockState()
            .setValue(FACING, facing)
            .setValue(POWERED, powered)
            .setValue(OPEN, powered)
            .setValue(CONNECTED, connected);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        if (direction == facing.getOpposite()) {
            boolean connected = isConnectedAtBack(level, pos, facing);
            return state.setValue(CONNECTED, connected);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (level.isClientSide()) return;

        boolean hasSignal = level.hasNeighborSignal(pos);
        if (state.getValue(POWERED) != hasSignal) {
            boolean wasOpen = state.getValue(OPEN);
            boolean shouldBeOpen = hasSignal;
            BlockState newState = state.setValue(POWERED, hasSignal).setValue(OPEN, shouldBeOpen);
            level.setBlock(pos, newState, Block.UPDATE_ALL);

            if (wasOpen != shouldBeOpen) {
                level.playSound(null, pos,
                    shouldBeOpen ? SoundEvents.IRON_TRAPDOOR_OPEN : SoundEvents.IRON_TRAPDOOR_CLOSE,
                    SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!state.getValue(POWERED)) {
            state = state.cycle(OPEN);
            level.setBlock(pos, state, Block.UPDATE_ALL);
            boolean isOpen = state.getValue(OPEN);
            level.playSound(null, pos,
                isOpen ? SoundEvents.IRON_TRAPDOOR_OPEN : SoundEvents.IRON_TRAPDOOR_CLOSE,
                SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return VentVoxelShapes.getVentShape(
            state.getValue(FACING),
            state.getValue(OPEN),
            state.getValue(CONNECTED)
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
        return state.getValue(FACING).getAxis() == Direction.Axis.Y;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction current = state.getValue(FACING);
        Direction next = switch (current) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.UP;
            case UP -> Direction.DOWN;
            case DOWN -> Direction.NORTH;
        };
        boolean connected = isConnectedAtBack(level, pos, next);
        level.setBlock(pos, state.setValue(FACING, next).setValue(CONNECTED, connected), Block.UPDATE_ALL);
        IWrenchable.playRotateSound(level, pos);
        return InteractionResult.SUCCESS;
    }
}
