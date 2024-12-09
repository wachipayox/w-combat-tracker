package com.wachi.combat_tracker;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = WCombatTrackerMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.DoubleValue HELPERS_MAX_PERCENTAGE = BUILDER
            .comment("Is the max percentage of the combat score that a helper can take ")
            .defineInRange("helpersMaxPercentage", 25, 0, 100.0);

    private static final ModConfigSpec.LongValue OUT_OF_COMBAT_TIME = BUILDER
            .comment("Is the time in ticks in which a entity is considered retired of combat if not interacts on it (-1 to disable)")
            .defineInRange("outOfCombatTime", 200, -1, Long.MAX_VALUE);


    public static final ModConfigSpec SPEC = BUILDER.build();

    public static Long outOfCombatTime;
    public static float helpersMaxPercentage;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        outOfCombatTime = OUT_OF_COMBAT_TIME.get();
        helpersMaxPercentage = HELPERS_MAX_PERCENTAGE.get().floatValue() / 100f;
    }
}
