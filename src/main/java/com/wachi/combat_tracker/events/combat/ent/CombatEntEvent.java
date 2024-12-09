package com.wachi.combat_tracker.events.combat.ent;

import com.wachi.combat_tracker.tracker.Combat;
import com.wachi.combat_tracker.tracker.CombatEnt;
import net.neoforged.bus.api.Event;

public abstract class CombatEntEvent extends Event {

    public final CombatEnt combatEnt;
    public final Combat combat;

    public CombatEntEvent(CombatEnt combatEnt, Combat combat) {
        this.combatEnt = combatEnt;
        this.combat = combat;
    }
}
