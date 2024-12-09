package com.wachi.combat_tracker.tracker;

import com.wachi.combat_tracker.Config;
import com.wachi.combat_tracker.WCombatTrackerMod;
import com.wachi.combat_tracker.events.combat.CombatKillResultEvent;
import com.wachi.combat_tracker.events.LivingHealWithSourceEvent;
import com.wachi.combat_tracker.mixins.ILE;
import com.wachi.combat_tracker.tracker.records.CombatRecord;
import com.wachi.combat_tracker.tracker.records.DamageRecord;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

@EventBusSubscriber(modid = WCombatTrackerMod.MODID)
public class CombatManager {
    static List<Combat> l = new ArrayList<>();
    static List<Combat> removing = new ArrayList<>();

    static final Logger LOGGER = WCombatTrackerMod.LOGGER;

    @SubscribeEvent
    static void onServerTick(ServerTickEvent.Post event){
        l.forEach(Combat::tick);
        removing.forEach((c) -> {l.remove(c); c = null;});
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    static void onEntityHit(LivingHurtEvent event){
        LivingEntity victim = event.getEntity();
        Entity source = event.getSource().getEntity();
        DamageSource dmgSrc = event.getSource();
        float amount = event.getAmount();
        amount = ((ILE) victim).callGetDamageAfterArmorAbsorb(dmgSrc, amount);
        amount = ((ILE) victim).callGetDamageAfterMagicAbsorb(dmgSrc, amount);
        amount = victim.getHealth() - amount >= 0 ? amount : victim.getHealth();

        if(source == null || victim.level().isClientSide)
            return;

        Combat c = getOrCreateOrMixCombat(victim, source, amount, victim.isAlive() && !source.equals(victim));
        if(c != null)
            c.onDamage(victim, dmgSrc, amount);

    }


    @SubscribeEvent
    public static void onHeal(LivingHealWithSourceEvent event){
        Entity healed = event.getEntity();
        Entity healer = event.getSource();

        float amount = event.getRealAmount();
        if(healer == null || healer.equals(healed) || healed.level().isClientSide || amount <= 0f)
            return;

        Combat c = getOrCreateOrMixCombat(healed, healer, amount, false);
        if(c != null)
            c.onHeal(healed, healer, amount);
    }


    /**Obtains the combat of two entities, if the both combats aren't the same, them will be mixed. If one entity isn't in combat
     * it will be added to it and viceversa.
     * @param startIfNoCombat if true the game will create a new combat in case both entities aren't in any combat
     * @return the result combat
     * */
    @Nullable
    public static Combat getOrCreateOrMixCombat(@NotNull Entity ent1, @NotNull Entity ent2, float amount, boolean startIfNoCombat) {
        Combat a = getCombat(ent1);
        Combat b = getCombat(ent2);
        Combat c = null;

        if(a != null && b != null) {
            if (!a.equals(b)) { //if both entities are in different combats
                l.remove(a);
                l.remove(b);
                c = Combat.mix(a, b);
                l.add(c);
                a = null; b = null;

            } else { //if both entities are in the same combat
                c = a;
            }
        }
        else if(a != null) { // if ent1 was already in combat
            a.addParticipant(ent2);
            c = a;
        }
        else if (b != null) { // if ent2 was already in combat
            b.addParticipant(ent1);
            c = b;
        }
        else if (startIfNoCombat){ // if both weren't in combat
            c = Combat.start(ent1, ent2);
            l.add(c);
        }
        return c;
    }

    @SubscribeEvent
    public static void onDeadDistribute(LivingDeathEvent event){
        LivingEntity dead = event.getEntity();
        Combat c = getCombat(dead);
        if(c == null || dead.level().isClientSide)
            return;

        Map<UUID, List<CombatRecord>> action_map = new HashMap<>();
        Map<UUID, Float> amount_map = new HashMap<>();
        Map<UUID, Float> multiply_map = new HashMap<>();
        Map<UUID, Float> result_map = new HashMap<>();
        UUID last_striker = null;
        float total = 0f;

        for(CombatRecord record : c.combat_records) {
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

        // saves all the helpers amount and rest it from the helped
        Map<UUID, Float> helpers_add = new HashMap<>();
        for (UUID uuid : new HashMap<>(amount_map).keySet()) {
            float amount = amount_map.get(uuid);

            CombatEnt cE = c.getParticipant(uuid);
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
                NeoForge.EVENT_BUS.post(new CombatKillResultEvent(ent, dead, total, amount, lH, c,
                        action_map.getOrDefault(uuid, new ArrayList<>())));
            else
                //when an unloaded or dead entity receives its percentage
                NeoForge.EVENT_BUS.post(new CombatKillResultEvent(uuid, dead, total, amount, lH, c,
                        action_map.getOrDefault(uuid, new ArrayList<>())));;
        }

    }

    static boolean isEntityInCombat(Entity ent){
        return getCombat(ent) != null;
    }

    static Combat getCombat(Entity ent){
        for(Combat c : l){
            for(CombatActive ce : c.getActiveParts())
                if(ce.entity.equals(ent))
                    return c;
        }
        return null;
    }
}
