package com.wachi.combat_tracker.tracker;

import com.wachi.combat_tracker.Config;
import com.wachi.combat_tracker.events.combat.CombatKillResultEvent;
import com.wachi.combat_tracker.tracker.records.CombatRecord;
import com.wachi.combat_tracker.tracker.records.DamageRecord;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;

public class DeathDistributor {
    public final Combat combat;
    public final LivingEntity dead;
    public UUID last_striker = null;
    public final Map<UUID, List<CombatRecord>> action_map = new HashMap<>();;
    public final Map<UUID, Float> amount_map = new HashMap<>();;
    public final Map<UUID, Float> multiply_map = new HashMap<>();;
    public final Map<UUID, Float> result_map = new HashMap<>();;
    public final Map<UUID, Float> helpers_add = new HashMap<>();;

    public DeathDistributor(LivingEntity dead_ent){
        this.dead = dead_ent;
        this.combat = CombatManager.getCombat(dead);
        if(combat == null || dead.level().isClientSide) {
            return;
        }
        float total = 0f;

        List<CombatRecord> all_actions = new ArrayList<>();
        for(CombatRecord record : combat.combat_records) {
            if(record.objetive == dead.getUUID() || record.executor == dead.getUUID())
                all_actions.add(record);

            if(record.objetive != dead.getUUID()) continue;

            if(record instanceof DamageRecord)
                last_striker = record.executor;

            if(record.type != CombatRecord.recordType.MULTIPLY_TOTAL_POINTS_FROM_EXECUTOR) {
                Float amount = amount_map.getOrDefault(record.executor, 0f);
                switch (record.type) {
                    case ADD_POINTS_TO_EXECUTOR -> amount += record.value;
                    case RETIRE_POINTS_FROM_EXECUTOR -> amount -= record.value;
                    case MULTIPLY_ACTUAL_POINTS_FROM_EXECUTOR -> amount *= record.value;
                }
                amount_map.put(record.executor, amount);
                List<CombatRecord> actions = action_map.getOrDefault(record.executor, new ArrayList<>());
                actions.add(record);
                action_map.put(record.executor, actions);

            } else {
                Float amount = multiply_map.getOrDefault(record.executor, 1f);
                amount *= record.value;
                multiply_map.put(record.executor, amount);
            }
        }

        // save in the list the actions for removing them in the next tick so if revives it doesn't count the old points
        combat.to_forgot_records.addAll(all_actions);

        // saves all the helpers amount and rest it from the helped
        for (UUID uuid : new HashMap<>(amount_map).keySet()) {
            float amount = amount_map.get(uuid);

            CombatEnt cE = combat.getParticipant(uuid);
            if(cE != null) {
                float total_help = 0f;
                Map<UUID, Float> help_percentage = new HashMap<>();

                for(UUID helper : cE.help_points.keySet()){
                    total_help += cE.help_points.get(helper);
                }
                for(UUID helper : cE.help_points.keySet()){
                    float percentage = cE.help_points.get(helper) > 0f ? cE.help_points.get(helper) / total_help : 0f;
                    help_percentage.put(helper, percentage);
                }
                total_help = Math.min(total_help, amount * Config.helpersMaxPercentage);

                for(UUID helper : help_percentage.keySet()){
                    float f = help_percentage.get(helper) * total_help;
                    f += helpers_add.getOrDefault(helper, 0f);
                    if(f > 0f)
                        helpers_add.put(helper, f);
                }
                amount -= total_help;
            }
            amount_map.put(uuid, amount);
        }

        //apply the helpers amount to the amount map
        for(UUID uuid : helpers_add.keySet()){
            float amount = helpers_add.get(uuid);
            amount_map.put(uuid, amount_map.getOrDefault(uuid, 0f) + amount);
        }

        // applies the MULTIPLY_TOTAL_POINTS_FROM_EXECUTOR
        for (UUID uuid : amount_map.keySet()) {
            float amount = Math.max(amount_map.get(uuid) * multiply_map.getOrDefault(uuid, 1f), 0f);
            result_map.put(uuid, amount);
            total += amount;
        }

        total = Math.max(total, dead.getMaxHealth());

        // converts to percentage
        for(UUID uuid : result_map.keySet()){
            boolean ent_loaded = false;
            Entity ent = null;
            float amount = (result_map.get(uuid) * 100) / total;

            for (ServerLevel lvl : dead.getServer().getAllLevels())
                if (lvl.getEntity(uuid) != null) {
                    ent = lvl.getEntity(uuid);
                    ent_loaded = true;
                    break;
                }

            boolean lH = last_striker != null && last_striker.equals(uuid);
            if(ent_loaded)
                //when a loaded entity receives its percentage
                NeoForge.EVENT_BUS.post(new CombatKillResultEvent(ent, dead, total, amount, lH, combat,
                        action_map.getOrDefault(uuid, new ArrayList<>())));
            else
                //when an unloaded or dead entity receives its percentage
                NeoForge.EVENT_BUS.post(new CombatKillResultEvent(uuid, dead, total, amount, lH, combat,
                        action_map.getOrDefault(uuid, new ArrayList<>())));;
        }
    }
}
