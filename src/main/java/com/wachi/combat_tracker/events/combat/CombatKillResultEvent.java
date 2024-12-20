package com.wachi.combat_tracker.events.combat;

import com.wachi.combat_tracker.WCombatTrackerMod;
import com.wachi.combat_tracker.tracker.Combat;
import com.wachi.combat_tracker.tracker.records.CombatRecord;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CombatKillResultEvent extends Event {

    public final boolean killerExistInWorld;
    public final boolean isLastHitter;
    public final UUID killer_uuid;
    public final Entity killer;
    public final Entity victim;
    private final List<CombatRecord> actions;
    private final Combat combat;
    public final float totalCombatScore;
    public final float ownScorePercentage;

    public CombatKillResultEvent(Entity killer, Entity victim, float totalScore, float percentage, boolean isLastHitter, Combat combat, List<CombatRecord> actions){
        this.killer = killer;
        this.killer_uuid = killer.getUUID();
        this.victim = victim;
        this.killerExistInWorld = true;
        this.actions = actions;
        this.combat = combat;
        this.isLastHitter = isLastHitter;
        this.totalCombatScore = totalScore;
        this.ownScorePercentage = percentage;
    }

    public CombatKillResultEvent(UUID killer, Entity victim, float totalScore, float percentage, boolean isLastHitter, Combat combat, List<CombatRecord> actions){
        this.killer = null;
        this.killer_uuid = killer;
        this.victim = victim;
        this.killerExistInWorld = false;
        this.actions = actions;
        this.combat = combat;
        this.isLastHitter = isLastHitter;
        this.totalCombatScore = totalScore;
        this.ownScorePercentage = percentage;
    }


    /** @return a list of CombatRecords in which the killer has damage victim.*/
    public List<CombatRecord> getKillerActions(){
        return new ArrayList<>(this.actions);
    }

    /**@return the combat were the kill took place*/
    public Combat getCombat(){
        return combat;
    }

    /**@return the killer combat score for the kill, this counts the help given and received too.*/
    public float getOwnCombatScore(){
        return totalCombatScore * (ownScorePercentage / 100f);
    }

    /**@return if the killer is the victim*/
    public boolean selfKill(){
        return this.killer_uuid == this.victim.getUUID();
    }

}
