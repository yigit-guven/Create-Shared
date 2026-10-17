package net.yigitguven.create_shared;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(CreateShared.MODID)
public class CreateShared {
    public static final String MODID = "create_shared";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateShared(IEventBus modEventBus, ModContainer modContainer) {
    }
}
