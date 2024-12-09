package com.wachi.combat_tracker.tracker.records;

import com.wachi.combat_tracker.events.records.CombatRecordCreatedEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatRecord {

    public enum recordType{
        ADD_POINTS_TO_EXECUTOR,
        RETIRE_POINTS_FROM_EXECUTOR,
        MULTIPLY_ACTUAL_POINTS_FROM_EXECUTOR,
        MULTIPLY_TOTAL_POINTS_FROM_EXECUTOR;
    }

    public final long created_at = ServerLifecycleHooks.getCurrentServer().overworld().getGameTime();
    public UUID objetive;
    public UUID executor;
    public Float value;
    public recordType type;

    /**A map for every mod register its own data on combat records*/
    final Map<ResourceLocation, Object> API_MAP = new HashMap<>();

    public CombatRecord(UUID objetive, float value, UUID executor, recordType type){
        this.objetive = objetive;
        this.value = value;
        this.executor = executor;
        this.type = type;
        NeoForge.EVENT_BUS.post(new CombatRecordCreatedEvent(this));
    }
}
