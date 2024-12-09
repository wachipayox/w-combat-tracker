package com.wachi.combat_tracker.events.records;

import com.wachi.combat_tracker.tracker.Combat;
import com.wachi.combat_tracker.tracker.records.CombatRecord;
import net.neoforged.bus.api.ICancellableEvent;

public class CombatRecordAddedEvent extends CombatRecordEvent implements ICancellableEvent {

    protected final Combat c;

    public CombatRecordAddedEvent(CombatRecord cR, Combat c) {
        super(cR);
        this.c = c;
    }

    public Combat getCombat() {
        return c;
    }
}
