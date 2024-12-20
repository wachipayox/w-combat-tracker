package com.wachi.combat_tracker.tracker;

import com.wachi.combat_tracker.WCombatTrackerMod;
import com.wachi.combat_tracker.events.LivingHealWithSourceEvent;
import com.wachi.combat_tracker.accessor.ILE;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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

    public static final Logger LOGGER = WCombatTrackerMod.LOGGER;

    @SubscribeEvent
    static void onServerTick(ServerTickEvent.Post event){
        l.forEach(Combat::tick);
        for (Combat combat : removing) {
            l.remove(combat);
        }
        removing.clear();
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

        Combat c = getOrCreateOrMixCombat(victim, source, victim.isAlive() && !source.equals(victim));
        if(c != null)
            c.onDamage(victim, dmgSrc, amount);

    }


    @SubscribeEvent
    static void onHeal(LivingHealWithSourceEvent event){
        Entity healed = event.getEntity();
        Entity healer = event.getSource();

        float amount = event.getRealAmount();
        if(healer == null || healer.equals(healed) || healed.level().isClientSide || amount <= 0f)
            return;

        Combat c = getOrCreateOrMixCombat(healed, healer, false);
        if(c != null)
            c.onHeal(healed, healer, amount);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    static void onDie(LivingDeathEvent event){
        if(event.isCanceled() || getCombat(event.getEntity()) == null)
            return;

        DeathDistributor DD = new DeathDistributor(event.getEntity());
        ((ILE) event.getEntity()).setDeadDistributor(DD);
    }


    /**Obtains the combat of two entities, if the both combats aren't the same, them will be mixed. If one entity isn't in combat
     * it will be added to the other's combat and viceversa.
     * @param startIfNoCombat if true the game will create a new combat in case both entities aren't in any combat
     * @return the result combat
     * */
    @Nullable
    static Combat getOrCreateOrMixCombat(@NotNull Entity ent1, @NotNull Entity ent2, boolean startIfNoCombat) {
        Combat a = getCombat(ent1);
        Combat b = getCombat(ent2);
        Combat c = null;

        if(a != null && b != null) {
            if (!a.equals(b)) { //if both entities are in different combats
                l.remove(a);
                l.remove(b);
                c = Combat.mix(a, b);
                l.add(c);

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

    public static boolean isEntityInCombat(Entity ent){
        return getCombat(ent) != null;
    }

    public static Combat getCombat(Entity ent){
        for(Combat c : l){
            for(CombatActive ce : c.getActiveParts())
                if(ce.entity.equals(ent))
                    return c;
        }
        return null;
    }
}
