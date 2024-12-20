package com.wachi.combat_tracker;

import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(WCombatTrackerMod.MODID)
public class WCombatTrackerMod
{
    public static final String MODID = "combat_tracker";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WCombatTrackerMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    public static Long getGameTime() {
        return ServerLifecycleHooks.getCurrentServer().overworld().getGameTime();
    }

}
