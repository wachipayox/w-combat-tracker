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
            .comment("Is the time in ticks in which an entity is considered retired of combat if not interacts on it (-1 to disable)")
            .defineInRange("outOfCombatTime", 300, -1, Long.MAX_VALUE);

    private static final ModConfigSpec.LongValue COMBAT_OVER_TIME = BUILDER
            .comment("Is the time in ticks in which a combat is over when there is only 1 participant left")
            .defineInRange("combatOverTime", 200, 0, Long.MAX_VALUE);

    private static final ModConfigSpec.IntValue PERCENTAGE_TO_APPEAR = BUILDER
            .comment("Is the percentage of the combat score that a killer must have to appear in the death message")
            .defineInRange("percentageToAppear", 10, 0, 100);


    public static final ModConfigSpec SPEC = BUILDER.build();

    public static Long outOfCombatTime;
    public static Long CombatOverTime;
    public static float helpersMaxPercentage;
    public static float percentageToAppear;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        outOfCombatTime = OUT_OF_COMBAT_TIME.get();
        CombatOverTime = COMBAT_OVER_TIME.get();
        helpersMaxPercentage = HELPERS_MAX_PERCENTAGE.get().floatValue() / 100f;
        percentageToAppear = PERCENTAGE_TO_APPEAR.get().floatValue() / 100f;
    }
}
