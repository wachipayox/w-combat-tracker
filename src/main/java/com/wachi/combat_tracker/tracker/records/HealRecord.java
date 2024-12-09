package com.wachi.combat_tracker.tracker.records;

import com.wachi.combat_tracker.tracker.CombatEnt;

import java.util.UUID;

public class HealRecord extends CombatRecord{
    public final CombatEnt healed;
    public final UUID healer;
    public final float amount;

    public HealRecord(CombatEnt healed, UUID healer, float amount){
        super(healed.getUUID(), amount, healer, recordType.RETIRE_POINTS_FROM_EXECUTOR);
        healed.onHelp(healer, amount);
        this.healed = healed;
        this.healer = healer;
        this.amount = amount;
    }
}

