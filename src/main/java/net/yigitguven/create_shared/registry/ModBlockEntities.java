package net.yigitguven.create_shared.registry;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yigitguven.create_shared.CreateShared;
import net.yigitguven.create_shared.block.entity.FactoryVentBlockEntity;
import net.yigitguven.create_shared.block.entity.FactoryVentDuctBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateShared.MODID);

    public static final Supplier<BlockEntityType<FactoryVentBlockEntity>> FACTORY_VENT =
        BLOCK_ENTITIES.register("factory_vent", () ->
            BlockEntityType.Builder.of(FactoryVentBlockEntity::new, ModBlocks.FACTORY_VENT.get()).build(null)
        );

    public static final Supplier<BlockEntityType<FactoryVentDuctBlockEntity>> FACTORY_VENT_DUCT =
        BLOCK_ENTITIES.register("factory_vent_duct", () ->
            BlockEntityType.Builder.of(FactoryVentDuctBlockEntity::new, ModBlocks.FACTORY_VENT_DUCT.get()).build(null)
        );
}
