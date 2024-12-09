package com.wachi.combat_tracker.tracker.records;

import com.wachi.combat_tracker.tracker.CombatEnt;
import net.minecraft.world.damagesource.DamageSource;

public class DamageRecord extends CombatRecord{
    public final CombatEnt victim;
    public final DamageSource dmgSrc;
    public final float amount;

    public DamageRecord(CombatEnt victim, DamageSource dmgSrc, float amount){
        super(victim.getUUID(), amount, dmgSrc.getEntity().getUUID(), recordType.ADD_POINTS_TO_EXECUTOR);
        victim.onHelp(dmgSrc.getEntity().getUUID(), -amount); //negative help
        this.victim = victim;
        this.dmgSrc = dmgSrc;
        this.amount = amount;
    }
}

