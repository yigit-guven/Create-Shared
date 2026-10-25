package net.yigitguven.create_shared.client.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.fan.AirFlowParticleData;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.ParrotElement;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import net.yigitguven.create_shared.block.FactoryVentBlock;
import net.yigitguven.create_shared.block.FactoryVentDuctBlock;
import net.yigitguven.create_shared.block.entity.FactoryVentBlockEntity;
import net.yigitguven.create_shared.registry.ModBlocks;

public class VentScenes {

    public static void ventGate(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("factory_vent_gate", "Operating Factory Exhaust Vents");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);

        BlockPos wallL = util.grid().at(3, 1, 2);
        BlockPos wallR = util.grid().at(1, 1, 2);
        BlockPos ventPos = util.grid().at(2, 1, 2);
        BlockPos leverPos = util.grid().at(3, 1, 1);
        BlockPos ductBehind1 = util.grid().at(2, 1, 3);
        BlockPos ductBehind2 = util.grid().at(2, 1, 4);

        scene.world().setBlock(wallL, AllBlocks.ANDESITE_CASING.getDefaultState(), false);
        scene.world().setBlock(wallR, AllBlocks.ANDESITE_CASING.getDefaultState(), false);
        scene.world().setBlock(wallL.above(), AllBlocks.ANDESITE_CASING.getDefaultState(), false);
        scene.world().setBlock(wallR.above(), AllBlocks.ANDESITE_CASING.getDefaultState(), false);
        scene.world().setBlock(ventPos.above(), AllBlocks.ANDESITE_CASING.getDefaultState(), false);

        scene.world().setBlock(ductBehind1, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.NORTH, true)
                .setValue(FactoryVentDuctBlock.SOUTH, true), false);
        scene.world().setBlock(ductBehind2, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.NORTH, true), false);

        scene.world().showSection(util.select().fromTo(1, 1, 2, 3, 2, 2)
                .substract(util.select().position(ventPos))
                .add(util.select().fromTo(2, 1, 3, 2, 1, 4)), Direction.DOWN);
        scene.idle(10);

        scene.world().setBlock(ventPos, ModBlocks.FACTORY_VENT.get().defaultBlockState()
                .setValue(FactoryVentBlock.FACING, Direction.NORTH)
                .setValue(FactoryVentBlock.OPEN, false)
                .setValue(FactoryVentBlock.CONNECTED, true), false);
        scene.world().showSection(util.select().position(ventPos), Direction.NORTH);
        scene.idle(15);

        Vec3 ventFront = util.vector().blockSurface(ventPos, Direction.NORTH);
        Vec3 leverFront = util.vector().blockSurface(leverPos, Direction.NORTH);

        scene.overlay().showText(70)
                .text("Factory Exhaust Vents act as operable gates for duct systems")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(ventFront);
        scene.idle(80);

        scene.overlay().showControls(ventFront, Pointing.DOWN, 40).rightClick();
        scene.idle(15);
        scene.world().setBlock(ventPos, ModBlocks.FACTORY_VENT.get().defaultBlockState()
                .setValue(FactoryVentBlock.FACING, Direction.NORTH)
                .setValue(FactoryVentBlock.OPEN, true)
                .setValue(FactoryVentBlock.CONNECTED, true), false);
        scene.effects().indicateSuccess(ventPos);
        scene.idle(25);

        scene.overlay().showText(70)
                .colored(PonderPalette.BLUE)
                .text("Right-click the front to manually open or close the vent gate")
                .placeNearTarget()
                .pointAt(ventFront);
        scene.idle(80);

        scene.overlay().showControls(ventFront, Pointing.DOWN, 30).rightClick();
        scene.idle(10);
        scene.world().setBlock(ventPos, ModBlocks.FACTORY_VENT.get().defaultBlockState()
                .setValue(FactoryVentBlock.FACING, Direction.NORTH)
                .setValue(FactoryVentBlock.OPEN, false)
                .setValue(FactoryVentBlock.CONNECTED, true), false);
        scene.idle(25);

        scene.world().setBlock(leverPos, Blocks.LEVER.defaultBlockState()
                .setValue(LeverBlock.FACING, Direction.NORTH)
                .setValue(LeverBlock.FACE, AttachFace.WALL)
                .setValue(LeverBlock.POWERED, false), false);
        scene.world().showSection(util.select().position(leverPos), Direction.NORTH);
        scene.idle(15);

        scene.overlay().showText(70)
                .colored(PonderPalette.RED)
                .text("Redstone power can also automate opening and closing the gate")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(leverFront);
        scene.idle(80);

        scene.overlay().showControls(leverFront, Pointing.DOWN, 25).rightClick();
        scene.idle(10);
        scene.world().setBlock(leverPos, Blocks.LEVER.defaultBlockState()
                .setValue(LeverBlock.FACING, Direction.NORTH)
                .setValue(LeverBlock.FACE, AttachFace.WALL)
                .setValue(LeverBlock.POWERED, true), false);
        scene.effects().indicateRedstone(leverPos);
        scene.world().setBlock(ventPos, ModBlocks.FACTORY_VENT.get().defaultBlockState()
                .setValue(FactoryVentBlock.FACING, Direction.NORTH)
                .setValue(FactoryVentBlock.OPEN, true)
                .setValue(FactoryVentBlock.POWERED, true)
                .setValue(FactoryVentBlock.CONNECTED, true), false);
        scene.idle(25);

        scene.overlay().showText(85)
                .colored(PonderPalette.GREEN)
                .text("Walking towards an open wall vent smoothly drops players into crawl pose to enter inside!")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(ventFront);

        ElementLink<ParrotElement> birb = scene.special().createBirb(util.vector().topOf(2, 0, 0), ParrotPose.FlappyPose::new);
        scene.idle(5);
        scene.special().moveParrot(birb, util.vector().of(0, 0, 2.5), 45);
        scene.idle(50);
        scene.effects().indicateSuccess(ductBehind1);
        scene.idle(25);

        scene.markAsFinished();
        scene.idle(30);
    }

    public static void ventDuctNetwork(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("factory_vent_duct_network", "Vent Ducts & Multi-Block Spaces");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);

        BlockPos ductPos1 = util.grid().at(2, 1, 2);
        BlockPos ductUp = util.grid().at(2, 2, 2);

        scene.world().setBlock(ductPos1, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState(), false);
        scene.world().showSection(util.select().position(ductPos1), Direction.DOWN);
        scene.idle(10);

        scene.world().setBlock(ductPos1, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.UP, true), false);
        scene.world().setBlock(ductUp, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.DOWN, true), false);
        scene.world().showSection(util.select().position(ductUp), Direction.DOWN);
        scene.idle(15);

        scene.overlay().showText(70)
                .text("Factory Vent Ducts seamlessly connect on all 6 sides to build crawl tunnels")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(ductPos1));
        scene.idle(80);

        scene.overlay().showOutline(PonderPalette.GREEN, ductUp, util.select().fromTo(2, 1, 2, 2, 2, 2), 70);
        scene.overlay().showText(70)
                .colored(PonderPalette.GREEN)
                .text("Vertical ducts act as internal ladders, allowing players to climb straight up and down")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(ductUp));
        scene.idle(80);

        scene.world().hideSection(util.select().position(ductUp), Direction.UP);
        scene.idle(15);

        BlockPos d00 = util.grid().at(2, 1, 2);
        BlockPos d10 = util.grid().at(3, 1, 2);
        BlockPos d01 = util.grid().at(2, 1, 3);
        BlockPos d11 = util.grid().at(3, 1, 3);

        scene.world().setBlock(d00, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.EAST, true)
                .setValue(FactoryVentDuctBlock.SOUTH, true), false);
        scene.world().setBlock(d10, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.WEST, true)
                .setValue(FactoryVentDuctBlock.SOUTH, true), false);
        scene.world().setBlock(d01, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.EAST, true)
                .setValue(FactoryVentDuctBlock.NORTH, true), false);
        scene.world().setBlock(d11, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.WEST, true)
                .setValue(FactoryVentDuctBlock.NORTH, true), false);

        scene.world().showSection(util.select().fromTo(2, 1, 2, 3, 1, 3).substract(util.select().position(d00)), Direction.DOWN);
        scene.idle(20);

        Selection twoByTwo = util.select().fromTo(2, 1, 2, 3, 1, 3);
        scene.overlay().showOutline(PonderPalette.BLUE, twoByTwo, twoByTwo, 80);

        scene.overlay().showText(80)
                .colored(PonderPalette.BLUE)
                .text("Placing ducts side-by-side automatically removes internal corner beams and edges")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(2, 1, 2));
        scene.idle(90);

        scene.overlay().showText(80)
                .text("This creates wide, open crawlways and hollow rooms without internal obstructions")
                .placeNearTarget()
                .pointAt(util.vector().centerOf(3, 1, 3));
        scene.idle(90);

        scene.effects().indicateSuccess(d11);
        scene.idle(25);

        scene.markAsFinished();
        scene.idle(30);
    }

    public static void ventAirflow(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("factory_vent_airflow", "Ventilation Networks & Fan Airflow");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);

        BlockPos vent1Pos = util.grid().at(2, 1, 2);
        BlockPos vent2Pos = util.grid().at(3, 1, 2);
        BlockPos duct1Pos = util.grid().at(2, 1, 3);
        BlockPos duct2Pos = util.grid().at(3, 1, 3);
        BlockPos fan1Pos = util.grid().at(2, 1, 4);
        BlockPos fan2Pos = util.grid().at(3, 1, 4);

        scene.world().setBlock(duct1Pos, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.NORTH, true)
                .setValue(FactoryVentDuctBlock.EAST, true)
                .setValue(FactoryVentDuctBlock.SOUTH, true), false);
        scene.world().setBlock(duct2Pos, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.WEST, true)
                .setValue(FactoryVentDuctBlock.SOUTH, true)
                .setValue(FactoryVentDuctBlock.NORTH, false), false);

        scene.world().showSection(util.select().fromTo(2, 1, 3, 3, 1, 3), Direction.DOWN);
        scene.idle(10);

        scene.world().setBlock(vent1Pos, ModBlocks.FACTORY_VENT.get().defaultBlockState()
                .setValue(FactoryVentBlock.FACING, Direction.NORTH)
                .setValue(FactoryVentBlock.OPEN, true)
                .setValue(FactoryVentBlock.CONNECTED, true), false);
        scene.world().showSection(util.select().position(vent1Pos), Direction.NORTH);
        scene.idle(15);

        scene.world().setBlock(fan1Pos, AllBlocks.ENCASED_FAN.getDefaultState()
                .setValue(com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING, Direction.NORTH), false);
        scene.world().setBlock(fan2Pos, AllBlocks.ENCASED_FAN.getDefaultState()
                .setValue(com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING, Direction.NORTH), false);

        Selection fans = util.select().position(fan1Pos).add(util.select().position(fan2Pos));
        scene.world().showSection(fans, Direction.SOUTH);
        scene.idle(15);

        scene.overlay().showOutline(PonderPalette.BLUE, fan1Pos, fans, 75);
        scene.overlay().showText(75)
                .colored(PonderPalette.BLUE)
                .text("Encased Fans facing directly into ducts supply airflow to the ventilation network")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(fan1Pos));
        scene.idle(85);

        scene.world().setKineticSpeed(util.select().position(fan1Pos), 32.0f);
        scene.effects().rotationSpeedIndicator(fan1Pos);
        scene.world().setKineticSpeed(util.select().position(fan2Pos), 64.0f);
        scene.effects().rotationSpeedIndicator(fan2Pos);
        scene.idle(20);

        scene.world().modifyBlockEntity(vent1Pos, FactoryVentBlockEntity.class, be -> {
            be.updateNetworkStats(96.0f, 96.0f, 2, 1, 2, false, 96.0f);
            be.getAirCurrent().rebuild();
        });

        scene.overlay().showOutline(PonderPalette.GREEN, duct1Pos, util.select().position(duct1Pos), 80);
        scene.overlay().showText(80)
                .colored(PonderPalette.GREEN)
                .text("The system totals all connected fan speeds into a shared Total Airflow (32 + 64 = 96 RPM)")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(duct1Pos));
        scene.idle(90);

        scene.overlay().showControls(util.vector().blockSurface(duct1Pos, Direction.UP), Pointing.DOWN, 50)
                .withItem(AllItems.GOGGLES.asStack());
        scene.idle(15);

        scene.overlay().showText(80)
                .colored(PonderPalette.WHITE)
                .text("Engineer's Goggles display the network's airflow, connected fans, and open exhausts on any vent or duct")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(duct1Pos, Direction.UP));
        scene.idle(90);

        Vec3 vent1Front = util.vector().blockSurface(vent1Pos, Direction.NORTH);
        scene.effects().emitParticles(vent1Front.add(0, 0, -0.3),
                scene.effects().simpleParticleEmitter(new AirFlowParticleData(vent1Pos), Vec3.ZERO),
                0.5f, 40);

        ElementLink<ParrotElement> birb = scene.special().createBirb(util.vector().topOf(2, 0, 1), ParrotPose.FlappyPose::new);
        scene.idle(10);

        scene.special().moveParrot(birb, util.vector().of(0, 0, -0.9), 25);
        scene.idle(15);

        scene.overlay().showText(80)
                .colored(PonderPalette.FAST)
                .text("Air blowing out of vents acts just like an Encased Fan, pushing entities and processing items")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(vent1Front);
        scene.idle(90);

        scene.special().moveParrot(birb, util.vector().of(0, 15.0, 0), 20);
        scene.idle(20);

        scene.world().modifyBlockEntity(vent1Pos, FactoryVentBlockEntity.class, be -> {
            be.updateNetworkStats(0.0f, 0.0f, 0, 0, 0, false, 0.0f);
            be.getAirCurrent().rebuild();
        });
        scene.idle(15);

        scene.world().setKineticSpeed(util.select().position(fan1Pos), -32.0f);
        scene.world().setKineticSpeed(util.select().position(fan2Pos), -64.0f);
        scene.effects().rotationSpeedIndicator(fan1Pos);
        scene.effects().rotationSpeedIndicator(fan2Pos);

        scene.world().modifyBlockEntity(vent1Pos, FactoryVentBlockEntity.class, be -> {
            be.updateNetworkStats(-96.0f, -96.0f, 2, 1, 2, false, -96.0f);
            be.getAirCurrent().rebuild();
        });
        scene.idle(10);

        scene.effects().emitParticles(vent1Front.add(0, 0, -1.8),
                scene.effects().simpleParticleEmitter(new AirFlowParticleData(vent1Pos), Vec3.ZERO),
                0.5f, 40);

        ElementLink<ParrotElement> birb2 = scene.special().createBirb(util.vector().topOf(2, 0, 0), ParrotPose.FlappyPose::new);
        scene.idle(10);

        scene.special().moveParrot(birb2, util.vector().of(0, 0, 0.9), 25);
        scene.idle(15);

        scene.overlay().showText(80)
                .colored(PonderPalette.BLUE)
                .text("Reversing fan rotation creates vacuum suction, pulling entities towards the vent")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(vent1Front);
        scene.idle(90);

        scene.special().moveParrot(birb2, util.vector().of(0, 15.0, 0), 20);
        scene.idle(15);

        scene.world().modifyBlockEntity(vent1Pos, FactoryVentBlockEntity.class, be -> {
            be.updateNetworkStats(0.0f, 0.0f, 0, 0, 0, false, 0.0f);
            be.getAirCurrent().rebuild();
        });
        scene.idle(15);

        scene.world().setKineticSpeed(util.select().position(fan1Pos), 32.0f);
        scene.world().setKineticSpeed(util.select().position(fan2Pos), 64.0f);

        scene.world().setBlock(duct2Pos, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.WEST, true)
                .setValue(FactoryVentDuctBlock.SOUTH, true)
                .setValue(FactoryVentDuctBlock.NORTH, true), false);

        scene.world().setBlock(vent2Pos, ModBlocks.FACTORY_VENT.get().defaultBlockState()
                .setValue(FactoryVentBlock.FACING, Direction.NORTH)
                .setValue(FactoryVentBlock.OPEN, true)
                .setValue(FactoryVentBlock.CONNECTED, true), false);
        scene.world().showSection(util.select().position(vent2Pos), Direction.NORTH);
        scene.idle(15);

        scene.world().modifyBlockEntity(vent1Pos, FactoryVentBlockEntity.class, be -> {
            be.updateNetworkStats(96.0f, 96.0f, 2, 2, 2, false, 48.0f);
            be.getAirCurrent().rebuild();
        });
        scene.world().modifyBlockEntity(vent2Pos, FactoryVentBlockEntity.class, be -> {
            be.updateNetworkStats(96.0f, 96.0f, 2, 2, 2, false, 48.0f);
            be.getAirCurrent().rebuild();
        });

        Vec3 vent2Front = util.vector().blockSurface(vent2Pos, Direction.NORTH);
        scene.effects().emitParticles(vent1Front.add(0, 0, -0.3),
                scene.effects().simpleParticleEmitter(new AirFlowParticleData(vent1Pos), Vec3.ZERO),
                0.4f, 40);
        scene.effects().emitParticles(vent2Front.add(0, 0, -0.3),
                scene.effects().simpleParticleEmitter(new AirFlowParticleData(vent2Pos), Vec3.ZERO),
                0.4f, 40);

        scene.overlay().showOutline(PonderPalette.GREEN, vent2Pos, util.select().position(vent1Pos).add(util.select().position(vent2Pos)), 80);
        scene.overlay().showText(80)
                .colored(PonderPalette.GREEN)
                .text("Airflow distributes across open vents. Closing one vent concentrates power into the others!")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(vent2Front);
        scene.idle(95);

        scene.overlay().showOutline(PonderPalette.SLOW, duct1Pos, util.select().position(duct1Pos).add(util.select().position(duct2Pos)), 85);
        scene.overlay().showText(85)
                .colored(PonderPalette.SLOW)
                .text("Large duct systems experience friction loss; add enough fan power to prevent dissipation")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(duct1Pos));
        scene.idle(95);

        scene.world().modifyBlockEntity(vent1Pos, FactoryVentBlockEntity.class, be -> {
            be.updateNetworkStats(0.0f, 0.0f, 0, 0, 0, false, 0.0f);
            be.getAirCurrent().rebuild();
        });
        scene.world().modifyBlockEntity(vent2Pos, FactoryVentBlockEntity.class, be -> {
            be.updateNetworkStats(0.0f, 0.0f, 0, 0, 0, false, 0.0f);
            be.getAirCurrent().rebuild();
        });
        scene.idle(15);

        scene.markAsFinished();
        scene.idle(30);
    }
}
