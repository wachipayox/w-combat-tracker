package com.wachi.combat_tracker.tracker;

import com.wachi.combat_tracker.events.combat.ent.CombatActiveEntTickEvent;
import com.wachi.combat_tracker.events.combat.ent.CombatAddParticipantEvent;
import com.wachi.combat_tracker.events.combat.CombatEvent;
import com.wachi.combat_tracker.events.combat.ent.CombatRetiringEntEvent;
import com.wachi.combat_tracker.events.records.CombatRecordAddedEvent;
import com.wachi.combat_tracker.tracker.records.CombatRecord;
import com.wachi.combat_tracker.tracker.records.DamageRecord;
import com.wachi.combat_tracker.tracker.records.HealRecord;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;

public class Combat {
    public final List<CombatActive> participants = new ArrayList<>();
    public final List<CombatInactive> old_participants = new ArrayList<>();
    public final List<CombatRecord> combat_records = new ArrayList<>();

    public final long startTime = ServerLifecycleHooks.getCurrentServer().overworld().getGameTime();

    /**A map for every mod register its own data on combats*/
    public final Map<ResourceLocation, Object> API_MAP = new HashMap<>();

    private Combat(List<CombatActive> participants, List<CombatInactive> old_participants){
        participants.forEach((cL) -> cL.combat = this);
        old_participants.forEach((cI) -> cI.combat = this);
        this.participants.addAll(participants);
        this.old_participants.addAll(old_participants);
    }

    void tick(){
        if(NeoForge.EVENT_BUS.post(new CombatEvent(this, CombatEvent.EventType.Tick)).isCanceled())
            return;

        tickParticipants();
    }

    void onDamage(Entity victim, DamageSource dmgSrc, float amount){
        CombatEnt victim_cE = getParticipant(victim, true);
        CombatEnt source_cE = getParticipant(dmgSrc.getEntity(), true);
        if(victim_cE == null || source_cE == null) return;
        victim_cE.onInteraction();
        source_cE.onInteraction();

        addCombatRecord(new DamageRecord(victim_cE, dmgSrc, amount));
    }

    void onHeal(Entity healed, Entity healer, float amount){
        CombatEnt healed_cE = getParticipant(healed, true);
        CombatEnt healer_cE = getParticipant(healer, true);
        if(healed_cE == null || healer_cE == null) return;
        healed_cE.onInteraction();
        healer_cE.onInteraction();

        addCombatRecord(new HealRecord(healed_cE, healer_cE.getUUID(), amount));
    }

    private void addCombatRecord(CombatRecord combatRecord){
        if(!NeoForge.EVENT_BUS.post(new CombatRecordAddedEvent(combatRecord, this)).isCanceled())
            combat_records.add(combatRecord);
    }

    private void tickParticipants() {
        List<CombatActive> remove = new ArrayList<>();
        for(CombatActive cL : participants){
            if(NeoForge.EVENT_BUS.post(new CombatActiveEntTickEvent(cL, this)).isCanceled())
                continue;

            if(cL.shouldRemove())
                if(!NeoForge.EVENT_BUS.post(new CombatRetiringEntEvent(cL, this)).isCanceled())
                    remove.add(cL);

        }
        remove.forEach((cL) -> {
            participants.remove(cL);
            old_participants.add(new CombatInactive(cL));
        });

        if(participants.size() <= 1)
            if(!NeoForge.EVENT_BUS.post(new CombatEvent(this, CombatEvent.EventType.End)).isCanceled())
                CombatManager.removing.add(this);
    }

    //creates a combat
    static Combat start(Entity entity1, Entity entity2){
        Combat c = new Combat(List.of(new CombatActive(entity1, null), new CombatActive(entity2, null)), new ArrayList<>());
        NeoForge.EVENT_BUS.post(new CombatEvent(c, CombatEvent.EventType.Creation));
        return c;
    }

    //mixes 2 combats
    static Combat mix(Combat a, Combat b){
        List<CombatActive> mix1 = new ArrayList<>(a.participants);
        List<CombatInactive> mix2 = new ArrayList<>(a.old_participants);
        mix1.addAll(b.participants);
        mix2.addAll(b.old_participants);

        Combat c = new Combat(mix1, mix2);
        NeoForge.EVENT_BUS.post(new CombatEvent(c, CombatEvent.EventType.Mix));
        return c;
    }

    List<CombatActive> getActiveParts(){
        return new ArrayList<>(participants);
    }

    List<CombatInactive> getRetiredParts(){
        return new ArrayList<>(old_participants);
    }

    List<CombatEnt> getAllParts(){
        List<CombatEnt> l = new ArrayList<>(participants);
        l.addAll(old_participants);
        return l;
    }

    //tries to return an active participant, if is not founded tries to return an inactive one, boolean reactive makes the
    //retired participant an active one if founded.
    CombatEnt getParticipant(Entity ent, boolean reactive){
        CombatEnt cE = getActiveParticipant(ent);
        if(cE != null) return cE;

        cE = getInactiveParticipant(ent);
        if(cE != null)
            return reactive ? addParticipant(ent) : getInactiveParticipant(ent);

        return null;
    }

    CombatEnt getParticipant(UUID uuid){
        for(CombatEnt cE : getAllParts()){
            if(cE.getUUID().equals(uuid))
                return cE;
        }
        return null;
    }

    CombatActive getActiveParticipant(Entity ent){
        for(CombatActive c : getActiveParts())
            if(c.entity.equals(ent))
                return c;
        return null;
    }

    CombatInactive getInactiveParticipant(Entity ent){
        for(CombatInactive c : getRetiredParts())
            if(c.uuid.equals(ent.getUUID()))
                return c;
        return null;
    }

    CombatActive addParticipant(Entity e){
        if(getActiveParticipant(e) != null)
            return getActiveParticipant(e);

        CombatActive cL = new CombatActive(e, this);
        CombatInactive cR = getInactiveParticipant(e);
        if(cR != null)
            old_participants.remove(cR);

        NeoForge.EVENT_BUS.post(new CombatAddParticipantEvent(cL, this, cR));
        participants.add(cL);
        return cL;
    }

    String getDebugLog(){
        String txt = null;
        for(CombatActive c : participants){
            if(txt != null)
                txt = txt + " vs " + c.entity.getName().getString();
            else
                txt = c.entity.getName().getString();
        }
        return txt;
    }
}
