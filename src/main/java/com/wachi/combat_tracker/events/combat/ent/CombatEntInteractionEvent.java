package com.wachi.combat_tracker.events.combat.ent;

import com.wachi.combat_tracker.tracker.Combat;
import com.wachi.combat_tracker.tracker.CombatEnt;
import net.neoforged.bus.api.ICancellableEvent;

public class CombatEntInteractionEvent extends CombatEntEvent implements ICancellableEvent {

    public CombatEntInteractionEvent(CombatEnt combatEnt, Combat combat) {
        super(combatEnt, combat);
    }
}
