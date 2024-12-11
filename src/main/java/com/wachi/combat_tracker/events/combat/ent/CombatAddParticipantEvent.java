package com.wachi.combat_tracker.events.combat.ent;

import com.wachi.combat_tracker.tracker.Combat;
import com.wachi.combat_tracker.tracker.CombatActive;
import com.wachi.combat_tracker.tracker.CombatInactive;
import net.neoforged.bus.api.ICancellableEvent;

import javax.annotation.Nullable;

public class CombatAddParticipantEvent extends CombatEntEvent implements ICancellableEvent {
    /**If before adding the participant was inactive, this is the participant when was inactive, else will be null*/
    @Nullable
    final CombatInactive combatInactive;

    public CombatAddParticipantEvent(CombatActive combatActive, Combat combat, @Nullable CombatInactive combatInactive) {
        super(combatActive, combat);
        this.combatInactive = combatInactive;
    }

    public boolean wasInactive(){
        return combatInactive != null;
    }
}
