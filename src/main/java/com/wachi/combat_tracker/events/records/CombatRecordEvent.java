package com.wachi.combat_tracker.events.records;

import com.wachi.combat_tracker.tracker.records.CombatRecord;
import net.neoforged.bus.api.Event;

public abstract class CombatRecordEvent extends Event {

    protected final CombatRecord cR;

    public CombatRecordEvent(CombatRecord cR){
        this.cR = cR;
    }

    public CombatRecord getRecord(){
        return cR;
    }

}
