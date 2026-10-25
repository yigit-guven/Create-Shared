package net.yigitguven.create_shared.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.yigitguven.create_shared.block.FactoryVentBlock;
import net.yigitguven.create_shared.block.FactoryVentDuctBlock;

public class VentCrawlHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player == null || !player.isAlive()) return;
        Level level = player.level();

        // If spectator, riding, or creative flying, clear forced swimming pose
        if (player.isSpectator() || player.isPassenger() || player.getAbilities().flying) {
            if (player.getForcedPose() == Pose.SWIMMING) {
                player.setForcedPose(null);
            }
            return;
        }

        boolean inVentOrDuct = isInsideVentOrDuct(level, player.getBoundingBox());
        boolean approachingOpenVent = !inVentOrDuct && isApproachingOpenVent(player, level);

        if (inVentOrDuct || approachingOpenVent) {
            if (player.getForcedPose() != Pose.SWIMMING) {
                player.setForcedPose(Pose.SWIMMING);
            }
            if (player.getPose() != Pose.SWIMMING) {
                player.setPose(Pose.SWIMMING);
            }
        } else if (player.getForcedPose() == Pose.SWIMMING) {
            // Check if player has clearance to stand up
            AABB standingBox = player.getDimensions(Pose.STANDING).makeBoundingBox(player.position()).deflate(1.0E-5);
            if (level.noCollision(player, standingBox)) {
                player.setForcedPose(null);
            }
        }
    }

    private static boolean isInsideVentOrDuct(Level level, AABB box) {
        BlockPos min = BlockPos.containing(box.minX + 1.0E-4, box.minY + 1.0E-4, box.minZ + 1.0E-4);
        BlockPos max = BlockPos.containing(box.maxX - 1.0E-4, box.maxY - 1.0E-4, box.maxZ - 1.0E-4);
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof FactoryVentBlock || state.getBlock() instanceof FactoryVentDuctBlock) {
                return true;
            }
        }
        return false;
    }

    private static boolean isApproachingOpenVent(Player player, Level level) {
        AABB playerBox = player.getBoundingBox();
        AABB searchBox = playerBox.inflate(1.2);
        BlockPos minPos = BlockPos.containing(searchBox.minX, searchBox.minY, searchBox.minZ);
        BlockPos maxPos = BlockPos.containing(searchBox.maxX, searchBox.maxY, searchBox.maxZ);

        Vec3 look = player.getLookAngle();
        Vec3 deltaMove = player.getDeltaMovement();

        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof FactoryVentBlock)) continue;
            if (!state.getValue(FactoryVentBlock.OPEN)) continue;

            Direction facing = state.getValue(FactoryVentBlock.FACING);
            // Vents facing UP or DOWN should only make the player crawl when inside the vent
            if (facing.getAxis() == Direction.Axis.Y) continue;

            AABB approachBox = getApproachBox(pos, facing);

            if (playerBox.intersects(approachBox)) {
                Vec3 intoVent = new Vec3(-facing.getStepX(), -facing.getStepY(), -facing.getStepZ());

                double lookDot = look.x * intoVent.x + look.z * intoVent.z;
                double moveDot = deltaMove.x * intoVent.x + deltaMove.z * intoVent.z;
                if (lookDot > 0.0 || moveDot > 0.005) {
                    return true;
                }
            }
        }
        return false;
    }

    private static AABB getApproachBox(BlockPos pos, Direction facing) {
        double px = pos.getX();
        double py = pos.getY();
        double pz = pos.getZ();

        return switch (facing) {
            case NORTH -> new AABB(px + 0.1, py, pz - 0.9, px + 0.9, py + 1.8, pz + 0.1);
            case SOUTH -> new AABB(px + 0.1, py, pz + 0.9, px + 0.9, py + 1.8, pz + 1.9);
            case WEST  -> new AABB(px - 0.9, py, pz + 0.1, px + 0.1, py + 1.8, pz + 0.9);
            case EAST  -> new AABB(px + 0.9, py, pz + 0.1, px + 1.9, py + 1.8, pz + 0.9);
            case UP    -> new AABB(px + 0.1, py + 0.9, pz + 0.1, px + 0.9, py + 1.9, pz + 0.9);
            case DOWN  -> new AABB(px + 0.1, py - 0.9, pz + 0.1, px + 0.9, py + 0.1, pz + 0.9);
        };
    }
}
