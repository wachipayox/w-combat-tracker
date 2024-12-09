package com.wachi.combat_tracker.events.combat.ent;

import com.wachi.combat_tracker.tracker.Combat;
import com.wachi.combat_tracker.tracker.CombatActive;
import net.neoforged.bus.api.ICancellableEvent;

public class CombatRetiringEntEvent extends CombatEntEvent implements ICancellableEvent {

    public CombatRetiringEntEvent(CombatActive combatActive, Combat combat) {
        super(combatActive, combat);
    }
}
