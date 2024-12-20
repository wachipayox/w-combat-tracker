package com.wachi.combat_tracker.events.deathmessage;

import com.wachi.combat_tracker.tracker.DeathDistributor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

import java.util.Map;
import java.util.UUID;

public class DeathMessageResultEvent extends Event implements ICancellableEvent {

    public final MutableComponent result;
    public final Map<UUID, Float> killers;
    public final Map<Entity, Float> valid_killers;
    public final DeathDistributor DD;

    public DeathMessageResultEvent(MutableComponent result, Map<UUID, Float> killers, Map<Entity, Float> valid_killers, DeathDistributor DD){
        this.result = result;
        this.killers = killers;
        this.valid_killers = valid_killers;
        this.DD = DD;
    }
}
