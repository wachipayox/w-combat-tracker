package com.wachi.combat_tracker.events.combat.ent;

import com.wachi.combat_tracker.tracker.Combat;
import com.wachi.combat_tracker.tracker.CombatActive;
import net.neoforged.bus.api.ICancellableEvent;

public class CombatAddParticipantEvent extends CombatEntEvent implements ICancellableEvent {
    public final boolean wasInactive;

    public CombatAddParticipantEvent(CombatActive combatActive, Combat combat, boolean wasInactive) {
        super(combatActive, combat);
        this.wasInactive = wasInactive;
    }
}
