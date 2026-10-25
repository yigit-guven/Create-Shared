package net.yigitguven.create_shared.client.ponder;

import com.simibubi.create.AllBlocks;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import net.yigitguven.create_shared.block.FactoryVentBlock;
import net.yigitguven.create_shared.block.FactoryVentDuctBlock;
import net.yigitguven.create_shared.registry.ModBlocks;

public class VentScenes {

    public static void ventGate(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("factory_vent_gate", "Operating Factory Exhaust Vents");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);

        // Build a decorative wall on z=2 facing NORTH towards the Ponder camera
        BlockPos wallL = util.grid().at(3, 1, 2);
        BlockPos wallR = util.grid().at(1, 1, 2);
        BlockPos ventPos = util.grid().at(2, 1, 2);
        BlockPos leverPos = util.grid().at(3, 1, 1); // in front of the andesite casing next to exhaust vent

        scene.world().setBlock(wallL, AllBlocks.ANDESITE_CASING.getDefaultState(), false);
        scene.world().setBlock(wallR, AllBlocks.ANDESITE_CASING.getDefaultState(), false);
        scene.world().setBlock(wallL.above(), AllBlocks.ANDESITE_CASING.getDefaultState(), false);
        scene.world().setBlock(wallR.above(), AllBlocks.ANDESITE_CASING.getDefaultState(), false);
        scene.world().setBlock(ventPos.above(), AllBlocks.ANDESITE_CASING.getDefaultState(), false);

        // Show all wall casing blocks
        scene.world().showSection(util.select().fromTo(1, 1, 2, 3, 2, 2).substract(util.select().position(ventPos)), Direction.DOWN);
        scene.idle(10);

        // Place Factory Exhaust Vent facing NORTH (towards the default Ponder front camera view)
        scene.world().setBlock(ventPos, ModBlocks.FACTORY_VENT.get().defaultBlockState()
                .setValue(FactoryVentBlock.FACING, Direction.NORTH)
                .setValue(FactoryVentBlock.OPEN, false), false);
        scene.world().showSection(util.select().position(ventPos), Direction.NORTH);
        scene.idle(15);

        Vec3 ventFront = util.vector().blockSurface(ventPos, Direction.NORTH);

        scene.overlay().showText(70)
                .text("Factory Exhaust Vents feature a sturdy mechanical gate")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(ventFront);
        scene.idle(80);

        // Manual Right-Click toggle
        scene.overlay().showControls(ventFront, Pointing.DOWN, 40).rightClick();
        scene.idle(15);
        scene.world().setBlock(ventPos, ModBlocks.FACTORY_VENT.get().defaultBlockState()
                .setValue(FactoryVentBlock.FACING, Direction.NORTH)
                .setValue(FactoryVentBlock.OPEN, true), false);
        scene.effects().indicateSuccess(ventPos);
        scene.idle(25);

        scene.overlay().showText(70)
                .text("Players can manually click the front to toggle the gate open or closed")
                .placeNearTarget()
                .pointAt(ventFront);
        scene.idle(80);

        // Close it again
        scene.overlay().showControls(ventFront, Pointing.DOWN, 30).rightClick();
        scene.idle(10);
        scene.world().setBlock(ventPos, ModBlocks.FACTORY_VENT.get().defaultBlockState()
                .setValue(FactoryVentBlock.FACING, Direction.NORTH)
                .setValue(FactoryVentBlock.OPEN, false), false);
        scene.idle(25);

        // Place Lever on wall facing NORTH for Redstone control
        scene.world().setBlock(leverPos, Blocks.LEVER.defaultBlockState()
                .setValue(LeverBlock.FACING, Direction.NORTH)
                .setValue(LeverBlock.FACE, AttachFace.WALL)
                .setValue(LeverBlock.POWERED, false), false);
        scene.world().showSection(util.select().position(leverPos), Direction.NORTH);
        scene.idle(15);

        scene.overlay().showText(70)
                .text("Redstone power can also automate opening and closing the gate")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(leverPos, Direction.NORTH));
        scene.idle(80);

        // Flip lever
        scene.overlay().showControls(util.vector().blockSurface(leverPos, Direction.NORTH), Pointing.DOWN, 25).rightClick();
        scene.idle(10);
        scene.world().setBlock(leverPos, Blocks.LEVER.defaultBlockState()
                .setValue(LeverBlock.FACING, Direction.NORTH)
                .setValue(LeverBlock.FACE, AttachFace.WALL)
                .setValue(LeverBlock.POWERED, true), false);
        scene.effects().indicateRedstone(leverPos);
        scene.world().setBlock(ventPos, ModBlocks.FACTORY_VENT.get().defaultBlockState()
                .setValue(FactoryVentBlock.FACING, Direction.NORTH)
                .setValue(FactoryVentBlock.OPEN, true)
                .setValue(FactoryVentBlock.POWERED, true), false);
        scene.idle(25);

        // Crawling into the vent
        scene.overlay().showText(80)
                .text("Walking towards an open wall vent smoothly drops players into crawl pose to enter inside!")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(ventFront);

        // Spawn Birb approaching the vent from NORTH (z=0) walking towards SOUTH (into vent)
        ElementLink<ParrotElement> birb = scene.special().createBirb(util.vector().topOf(2, 0, 0), ParrotPose.FlappyPose::new);
        scene.idle(5);
        scene.special().moveParrot(birb, util.vector().of(0, 0, 1.8), 35);
        scene.idle(40);
        scene.effects().indicateSuccess(ventPos);

        scene.markAsFinished();
        scene.idle(30);
    }

    public static void ventDuctNetwork(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("factory_vent_duct_network", "Vent Ducts & Fan Wind Currents");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);

        BlockPos ventPos = util.grid().at(2, 1, 1);
        BlockPos ductPos1 = util.grid().at(2, 1, 2);
        BlockPos ductPos2 = util.grid().at(2, 1, 3);
        BlockPos ductUp = util.grid().at(2, 2, 3);
        BlockPos fanPos = util.grid().at(2, 1, 4);

        // Show Vent Ducts connecting
        scene.world().setBlock(ductPos1, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.NORTH, true)
                .setValue(FactoryVentDuctBlock.SOUTH, true), false);
        scene.world().showSection(util.select().position(ductPos1), Direction.DOWN);
        scene.idle(10);

        scene.world().setBlock(ductPos2, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.NORTH, true)
                .setValue(FactoryVentDuctBlock.SOUTH, true)
                .setValue(FactoryVentDuctBlock.UP, true), false);
        scene.world().showSection(util.select().position(ductPos2), Direction.DOWN);
        scene.idle(10);

        scene.world().setBlock(ductUp, ModBlocks.FACTORY_VENT_DUCT.get().defaultBlockState()
                .setValue(FactoryVentDuctBlock.DOWN, true), false);
        scene.world().showSection(util.select().position(ductUp), Direction.DOWN);
        scene.idle(15);

        scene.overlay().showText(70)
                .text("Factory Vent Ducts seamlessly connect on all 6 sides to build crawl tunnels")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(ductPos2));
        scene.idle(80);

        // Highlight vertical duct climbing
        scene.overlay().showText(60)
                .text("Vertical ducts act like ladders, allowing players to climb straight up and down")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(ductUp));
        scene.overlay().showOutline(PonderPalette.GREEN, ductUp, util.select().position(ductUp), 60);
        scene.idle(70);

        // Connect Exhaust Vent to the front of duct facing NORTH
        scene.world().setBlock(ventPos, ModBlocks.FACTORY_VENT.get().defaultBlockState()
                .setValue(FactoryVentBlock.FACING, Direction.NORTH)
                .setValue(FactoryVentBlock.OPEN, true), false);
        scene.world().showSection(util.select().position(ventPos), Direction.NORTH);
        scene.idle(15);

        scene.overlay().showText(70)
                .text("Ducts and Vents connect dynamically, opening back-flaps whenever joined together")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(ventPos));
        scene.idle(80);

        // Connect Encased Fan behind duct at z=4 facing NORTH towards duct and vent
        Selection fanSelection = util.select().position(fanPos);
        scene.world().setBlock(fanPos, AllBlocks.ENCASED_FAN.getDefaultState()
                .setValue(com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING, Direction.NORTH), false);
        scene.world().showSection(fanSelection, Direction.SOUTH);
        scene.idle(15);

        // Power fan with kinetic speed (positive speed blowing forward in facing direction)
        scene.world().setKineticSpeed(fanSelection, 64.0f);
        scene.effects().rotationSpeedIndicator(fanPos);
        scene.idle(20);

        // Emit air current particles through duct and blasting out the front of the vent (NORTH, into z=0)
        Vec3 ventFront = util.vector().blockSurface(ventPos, Direction.NORTH);
        scene.effects().emitParticles(ventFront.add(0, -0.1, -0.2),
                scene.effects().simpleParticleEmitter(ParticleTypes.SPIT, new Vec3(0, 0, -0.25)),
                1.0f, 60);

        scene.overlay().showText(80)
                .text("Encased Fans blowing into vents or ducts push air currents through the network and into rooms!")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(ventFront);
        scene.idle(90);

        scene.markAsFinished();
        scene.idle(30);
    }
}
