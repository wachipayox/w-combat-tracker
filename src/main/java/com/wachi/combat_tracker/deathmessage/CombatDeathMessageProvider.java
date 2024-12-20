package com.wachi.combat_tracker.deathmessage;

import com.wachi.combat_tracker.Config;
import com.wachi.combat_tracker.events.deathmessage.DeathMessageResultEvent;
import com.wachi.combat_tracker.events.deathmessage.KillerAddedInDeathMessageEvent;
import com.wachi.combat_tracker.events.deathmessage.KillerLoggedInDeathEvent;
import com.wachi.combat_tracker.tracker.DeathDistributor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.IDeathMessageProvider;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatDeathMessageProvider implements IDeathMessageProvider {

    @Override
    public Component getDeathMessage(LivingEntity entity, CombatEntry lastEntry, @Nullable CombatEntry mostSignificantFall) {
        DeathDistributor DD = new DeathDistributor(entity);
        Map<UUID, Float> killers = DD.result_map; // All killers and points
        Map<Entity, Float> filtered = new HashMap<>(); // Only 10% up killers and percentage
        float total = 0;

        for(UUID uuid : killers.keySet()){
            total += killers.get(uuid);
        }
        for(UUID uuid : killers.keySet()){
            float percentage = killers.get(uuid) / total;
            if(getInWorld(uuid) instanceof Entity e)
                if(percentage >= Config.percentageToAppear || lastEntry.source().getEntity() != null && lastEntry.source().getEntity().getUUID().equals(uuid)
                 && !NeoForge.EVENT_BUS.post(new KillerLoggedInDeathEvent(e, entity, percentage, total)).isCanceled())
                    filtered.put(e, percentage);
        }
        if(lastEntry.source().getEntity() != null) filtered.remove(lastEntry.source().getEntity());
        if(entity.getKillCredit() != null) filtered.remove(entity.getKillCredit());
        if(filtered.isEmpty()) return lastEntry.source().getLocalizedDeathMessage(entity);

        MutableComponent mc = (MutableComponent) lastEntry.source().getLocalizedDeathMessage(entity);
        mc.append(" " + Component.translatable("combat_tracker.deathmessage.transition").getString() + " ");

        int i = 0;
        for(Entity e : filtered.keySet()){
            KillerAddedInDeathMessageEvent event = NeoForge.EVENT_BUS.post(new KillerAddedInDeathMessageEvent(e, entity, DD.last_striker == e.getUUID(), true));
            if(!event.isCanceled()) {
                mc.append(event.getKillerDisplayName());
                if (i + 2 == filtered.size())
                    mc.append(" " + Component.translatable("combat_tracker.deathmessage.and").getString() + " ");
                else if (i + 2 < filtered.size())
                    mc.append(", ");
            }
            i++;
        }

        DeathMessageResultEvent event = NeoForge.EVENT_BUS.post(new DeathMessageResultEvent(mc, killers, filtered, DD));
        return event.isCanceled() ? lastEntry.source().getLocalizedDeathMessage(entity) : event.result;
    }

    static Entity getInWorld(UUID uuid){
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (ServerLevel lvl : server.getAllLevels())
            if (lvl.getEntity(uuid) != null)
                return lvl.getEntity(uuid);

        return null;
    }
}
