package com.wachi.combat_tracker.events.combat.ent;

import com.wachi.combat_tracker.tracker.CombatEnt;
import net.neoforged.bus.api.Event;

import java.util.UUID;

public class CombatEntOnHelpedEvent extends Event {

    public final CombatEnt combatEnt;
    private float points;
    public final UUID helper;

    public CombatEntOnHelpedEvent(CombatEnt combatEnt, float points, UUID helper) {
        this.combatEnt = combatEnt;
        this.points = points;
        this.helper = helper;
    }

    public float getPoints(){
        return points;
    }

    public void setPoints(float f) {
        this.points = f;
    }

    public void cancelHelp(){
        setPoints(0f);
    }
}
