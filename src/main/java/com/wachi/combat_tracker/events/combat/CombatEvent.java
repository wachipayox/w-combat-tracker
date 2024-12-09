package com.wachi.combat_tracker.events.combat;

import com.wachi.combat_tracker.tracker.Combat;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class CombatEvent extends Event implements ICancellableEvent {

    public enum EventType{
        Creation,
        Mix,
        Tick,
        End
    }

    private final EventType type;
    private final Combat c;

    public CombatEvent(Combat c, EventType type){
        this.c = c;
        this.type = type;
    }

    public Combat getCombat(){
        return c;
    }

    public EventType getType(){
        return type;
    }
}
