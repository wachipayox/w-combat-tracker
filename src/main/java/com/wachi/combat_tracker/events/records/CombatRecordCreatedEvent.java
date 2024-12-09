package com.wachi.combat_tracker.events.records;

import com.wachi.combat_tracker.tracker.records.CombatRecord;

public class CombatRecordCreatedEvent extends CombatRecordEvent{
    public CombatRecordCreatedEvent(CombatRecord cR) {
        super(cR);
    }
}
