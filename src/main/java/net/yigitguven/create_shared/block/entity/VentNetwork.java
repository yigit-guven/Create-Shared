package net.yigitguven.create_shared.block.entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.yigitguven.create_shared.block.FactoryVentBlock;
import net.yigitguven.create_shared.block.FactoryVentDuctBlock;
import net.yigitguven.create_shared.config.CreateSharedServerConfig;

public class VentNetwork {

    public static void recalculateIfDirtyOrPeriodic(Level level, BlockPos pos, AbstractVentBlockEntity originBE) {
        if (level == null || level.isClientSide()) return;
        long currentTime = level.getGameTime();
        if (originBE.isNetworkDirty() || (currentTime - originBE.getLastCalculatedTick() >= 20)) {
            scanAndRecalculate(level, pos, currentTime);
        }
    }

    public static void scanAndRecalculate(Level level, BlockPos startPos, long currentTime) {
        int maxBlocks = CreateSharedServerConfig.MAX_NETWORK_BLOCKS.get();

        Set<BlockPos> visitedDucts = new HashSet<>();
        Set<BlockPos> visitedVents = new HashSet<>();
        Map<BlockPos, Direction> connectedFans = new HashMap<>(); // fan pos -> fan facing (towards network)
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(startPos);

        while (!queue.isEmpty() && (visitedDucts.size() + visitedVents.size() < maxBlocks)) {
            BlockPos curr = queue.poll();
            if (!level.isLoaded(curr)) continue;
            BlockState state = level.getBlockState(curr);

            if (state.getBlock() instanceof FactoryVentDuctBlock) {
                if (!visitedDucts.add(curr)) continue;

                for (Direction dir : Direction.values()) {
                    if (!state.getValue(FactoryVentDuctBlock.PROPERTY_BY_DIRECTION.get(dir))) continue;
                    BlockPos neighborPos = curr.relative(dir);
                    if (visitedDucts.contains(neighborPos) || visitedVents.contains(neighborPos) || !level.isLoaded(neighborPos)) continue;

                    BlockState neighborState = level.getBlockState(neighborPos);

                    if (neighborState.getBlock() instanceof FactoryVentDuctBlock) {
                        if (neighborState.getValue(FactoryVentDuctBlock.PROPERTY_BY_DIRECTION.get(dir.getOpposite()))) {
                            queue.add(neighborPos);
                        }
                    } else if (neighborState.getBlock() instanceof FactoryVentBlock) {
                        Direction ventFacing = neighborState.getValue(FactoryVentBlock.FACING);
                        boolean ventOpen = neighborState.getValue(FactoryVentBlock.OPEN);
                        // Connects at back of vent or at front of vent if open
                        if (ventFacing == dir || (ventFacing.getOpposite() == dir && ventOpen)) {
                            queue.add(neighborPos);
                        }
                    } else if (neighborState.getBlock() instanceof EncasedFanBlock) {
                        Direction fanFacing = neighborState.getValue(BlockStateProperties.FACING);
                        if (fanFacing == dir.getOpposite()) {
                            connectedFans.put(neighborPos, fanFacing);
                        }
                    }
                }
            } else if (state.getBlock() instanceof FactoryVentBlock) {
                if (!visitedVents.add(curr)) continue;

                Direction ventFacing = state.getValue(FactoryVentBlock.FACING);
                Direction back = ventFacing.getOpposite();
                BlockPos backPos = curr.relative(back);

                // Back connection
                if (!visitedDucts.contains(backPos) && !visitedVents.contains(backPos) && level.isLoaded(backPos)) {
                    BlockState backState = level.getBlockState(backPos);
                    if (backState.getBlock() instanceof FactoryVentDuctBlock) {
                        if (backState.getValue(FactoryVentDuctBlock.PROPERTY_BY_DIRECTION.get(ventFacing))) {
                            queue.add(backPos);
                        }
                    } else if (backState.getBlock() instanceof FactoryVentBlock) {
                        Direction otherFacing = backState.getValue(FactoryVentBlock.FACING);
                        if (otherFacing == back || (otherFacing == ventFacing && backState.getValue(FactoryVentBlock.OPEN))) {
                            queue.add(backPos);
                        }
                    } else if (backState.getBlock() instanceof EncasedFanBlock) {
                        Direction fanFacing = backState.getValue(BlockStateProperties.FACING);
                        if (fanFacing == ventFacing) {
                            connectedFans.put(backPos, fanFacing);
                        }
                    }
                }

                // Front connection (if vent is open and faces into a duct)
                if (state.getValue(FactoryVentBlock.OPEN)) {
                    BlockPos frontPos = curr.relative(ventFacing);
                    if (!visitedDucts.contains(frontPos) && level.isLoaded(frontPos)) {
                        BlockState frontState = level.getBlockState(frontPos);
                        if (frontState.getBlock() instanceof FactoryVentDuctBlock) {
                            if (frontState.getValue(FactoryVentDuctBlock.PROPERTY_BY_DIRECTION.get(back))) {
                                queue.add(frontPos);
                            }
                        }
                    }
                }
            }
        }

        // Determine open exhaust/intake vents (front facing open world, not an internal duct)
        List<BlockPos> openExhaustVents = new ArrayList<>();
        for (BlockPos vPos : visitedVents) {
            BlockState vState = level.getBlockState(vPos);
            if (vState.getBlock() instanceof FactoryVentBlock && vState.getValue(FactoryVentBlock.OPEN)) {
                Direction vFacing = vState.getValue(FactoryVentBlock.FACING);
                BlockPos inFront = vPos.relative(vFacing);
                if (!visitedDucts.contains(inFront)) {
                    openExhaustVents.add(vPos);
                }
            }
        }

        // Calculate total airflow from fans
        float totalAirflow = 0.0f;
        for (Map.Entry<BlockPos, Direction> fanEntry : connectedFans.entrySet()) {
            BlockPos fanPos = fanEntry.getKey();
            Direction fanFacing = fanEntry.getValue();
            BlockEntity be = level.getBlockEntity(fanPos);
            if (be instanceof KineticBlockEntity kbe) {
                float speed = kbe.getSpeed();
                float flowSpeed = KineticBlockEntity.convertToDirection(speed, fanFacing);
                totalAirflow += flowSpeed;
            }
        }

        // Calculate friction dissipation
        int ductCount = visitedDucts.size();
        double frictionLoss = ductCount * CreateSharedServerConfig.FRICTION_LOSS_PER_DUCT.get();
        float effectiveAirflow = 0.0f;
        boolean dissipated = false;

        if (totalAirflow != 0.0f) {
            float absTotal = Math.abs(totalAirflow);
            if (absTotal <= frictionLoss) {
                effectiveAirflow = 0.0f;
                dissipated = true;
            } else {
                effectiveAirflow = Math.signum(totalAirflow) * (float)(absTotal - frictionLoss);
                dissipated = false;
            }
        }

        // Distribute airflow across open exhaust vents
        Map<BlockPos, Float> ventAirflowMap = new HashMap<>();
        int openVentCount = openExhaustVents.size();

        if (openVentCount > 0 && effectiveAirflow != 0.0f) {
            CreateSharedServerConfig.DistributionMode distMode = CreateSharedServerConfig.DISTRIBUTION_MODE.get();
            if (distMode == CreateSharedServerConfig.DistributionMode.EQUAL || connectedFans.isEmpty()) {
                float equalShare = effectiveAirflow / openVentCount;
                for (BlockPos vPos : openExhaustVents) {
                    ventAirflowMap.put(vPos, equalShare);
                }
            } else {
                // Distance attenuated distribution
                Map<BlockPos, Integer> distances = computeFanDistances(level, connectedFans, visitedDucts, visitedVents);
                double decayRatio = CreateSharedServerConfig.DISTANCE_DECAY_RATIO.get();
                double totalWeight = 0.0;
                Map<BlockPos, Double> weights = new HashMap<>();

                for (BlockPos vPos : openExhaustVents) {
                    int dist = distances.getOrDefault(vPos, 1);
                    double w = 1.0 / (1.0 + dist * decayRatio);
                    weights.put(vPos, w);
                    totalWeight += w;
                }

                if (totalWeight > 0.0) {
                    for (BlockPos vPos : openExhaustVents) {
                        double fraction = weights.get(vPos) / totalWeight;
                        ventAirflowMap.put(vPos, (float)(effectiveAirflow * fraction));
                    }
                } else {
                    float equalShare = effectiveAirflow / openVentCount;
                    for (BlockPos vPos : openExhaustVents) {
                        ventAirflowMap.put(vPos, equalShare);
                    }
                }
            }
        }

        // Update all visited ducts and vents
        int fanCount = connectedFans.size();
        for (BlockPos dPos : visitedDucts) {
            BlockEntity be = level.getBlockEntity(dPos);
            if (be instanceof AbstractVentBlockEntity avbe) {
                avbe.setLastCalculatedTick(currentTime);
                avbe.updateNetworkStats(totalAirflow, effectiveAirflow, fanCount, openVentCount, ductCount, dissipated, 0.0f);
            }
        }

        for (BlockPos vPos : visitedVents) {
            BlockEntity be = level.getBlockEntity(vPos);
            if (be instanceof AbstractVentBlockEntity avbe) {
                avbe.setLastCalculatedTick(currentTime);
                float ventFlow = ventAirflowMap.getOrDefault(vPos, 0.0f);
                avbe.updateNetworkStats(totalAirflow, effectiveAirflow, fanCount, openVentCount, ductCount, dissipated, ventFlow);
            }
        }
    }

    private static Map<BlockPos, Integer> computeFanDistances(Level level, Map<BlockPos, Direction> fans, Set<BlockPos> ducts, Set<BlockPos> vents) {
        Map<BlockPos, Integer> distances = new HashMap<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        for (Map.Entry<BlockPos, Direction> fanEntry : fans.entrySet()) {
            BlockPos intakePos = fanEntry.getKey().relative(fanEntry.getValue());
            if (ducts.contains(intakePos) || vents.contains(intakePos)) {
                distances.put(intakePos, 0);
                queue.add(intakePos);
            }
        }

        while (!queue.isEmpty()) {
            BlockPos curr = queue.poll();
            int currentDist = distances.get(curr);

            for (Direction dir : Direction.values()) {
                BlockPos next = curr.relative(dir);
                if ((ducts.contains(next) || vents.contains(next)) && !distances.containsKey(next)) {
                    distances.put(next, currentDist + 1);
                    queue.add(next);
                }
            }
        }

        return distances;
    }
}
