package net.yigitguven.create_shared.block.entity;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.yigitguven.create_shared.block.FactoryVentBlock;
import net.yigitguven.create_shared.config.CreateSharedServerConfig;
import net.yigitguven.create_shared.registry.ModBlockEntities;

public class FactoryVentBlockEntity extends AbstractVentBlockEntity implements IAirCurrentSource {

    public AirCurrent airCurrent;
    protected int airCurrentUpdateCooldown;
    protected int entitySearchCooldown;
    protected boolean updateAirFlow = true;

    public FactoryVentBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FACTORY_VENT.get(), pos, blockState);
        this.airCurrent = new AirCurrent(this);
        this.updateAirFlow = true;
    }

    public void blockInFrontChanged() {
        this.updateAirFlow = true;
    }

    @Override
    public boolean updateNetworkStats(float totalAirflow, float effectiveAirflow, int fanCount, int openVentCount, int ductCount, boolean dissipated, float ventAirflow) {
        boolean changed = super.updateNetworkStats(totalAirflow, effectiveAirflow, fanCount, openVentCount, ductCount, dissipated, ventAirflow);
        if (changed) {
            this.updateAirFlow = true;
        }
        return changed;
    }

    @Override
    public void readClient(CompoundTag tag, HolderLookup.Provider registries) {
        super.readClient(tag, registries);
        this.updateAirFlow = true;
        this.airCurrent.rebuild();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.updateAirFlow = true;
    }

    @Override
    public AirCurrent getAirCurrent() {
        return airCurrent;
    }

    @Nullable
    @Override
    public Level getAirCurrentWorld() {
        return level;
    }

    @Override
    public BlockPos getAirCurrentPos() {
        return worldPosition;
    }

    @Override
    public float getSpeed() {
        if (level == null) return 0.0f;
        BlockState state = getBlockState();
        if (!state.hasProperty(FactoryVentBlock.OPEN) || !state.getValue(FactoryVentBlock.OPEN)) {
            return 0.0f;
        }
        if (Math.abs(cachedVentAirflow) < CreateSharedServerConfig.MIN_AIRFLOW_PER_VENT.get()) {
            return 0.0f;
        }
        return (float) (cachedVentAirflow * CreateSharedServerConfig.WIND_STRENGTH.get());
    }

    @Override
    public Direction getAirflowOriginSide() {
        BlockState state = getBlockState();
        if (state.hasProperty(FactoryVentBlock.FACING)) {
            return state.getValue(FactoryVentBlock.FACING);
        }
        return Direction.NORTH;
    }

    @Nullable
    @Override
    public Direction getAirFlowDirection() {
        float speed = getSpeed();
        if (speed == 0.0f) return null;
        Direction facing = getAirflowOriginSide();
        return speed > 0.0f ? facing : facing.getOpposite();
    }

    @Override
    public float getMaxDistance() {
        float rawSpeed = getSpeed();
        if (rawSpeed == 0.0f) return 0.0f;
        float speed = Math.abs(rawSpeed);
        int maxConfig = CreateSharedServerConfig.MAX_WIND_RANGE.get();
        float distanceFactor = Math.min(speed / 256.0f, 1.0f);
        float pushDist = Mth.lerp(distanceFactor, 3.0f, (float) maxConfig);
        float pullDist = Mth.lerp(distanceFactor, 3.0f, (float) Math.min(maxConfig, 16));
        return rawSpeed > 0 ? pushDist : pullDist;
    }

    @Override
    public boolean isSourceRemoved() {
        return isRemoved();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FactoryVentBlockEntity be) {
        boolean server = !level.isClientSide();

        if (server) {
            // Periodic network scan
            VentNetwork.recalculateIfDirtyOrPeriodic(level, pos, be);

            if (be.airCurrentUpdateCooldown-- <= 0) {
                be.airCurrentUpdateCooldown = AllConfigs.server().kinetics.fanBlockCheckRate.get();
                be.updateAirFlow = true;
            }
        }

        if (be.updateAirFlow) {
            be.updateAirFlow = false;
            be.airCurrent.rebuild();
            if (server) {
                be.sendData();
            }
        }

        if (be.getSpeed() == 0.0f) {
            return;
        }

        if (be.entitySearchCooldown-- <= 0) {
            be.entitySearchCooldown = 5;
            be.airCurrent.findEntities();
        }

        be.airCurrent.tick();
    }
}
