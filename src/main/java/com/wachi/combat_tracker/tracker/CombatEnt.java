package com.wachi.combat_tracker.tracker;

import com.wachi.combat_tracker.Config;
import com.wachi.combat_tracker.WCombatTrackerMod;
import com.wachi.combat_tracker.events.combat.ent.CombatEntInteractionEvent;
import com.wachi.combat_tracker.events.combat.ent.CombatEntOnHelpedEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;

public abstract class CombatEnt {
    /**A map for every mod register its own data on combat entities*/
    public Map<ResourceLocation, Object> API_MAP = new HashMap<>();

    public Map<UUID, Float> help_points = new HashMap<>();
    public Long last_interaction;
    public Combat combat;

    public CombatEnt(Combat c){
        this.combat = c;
        onInteraction();
    }

    public void onHelp(UUID helper, float points){
        if(NeoForge.EVENT_BUS.post(new CombatEntOnHelpedEvent(this, points, helper)).getPoints() == 0f)
            return;
        help_points.put(helper, Math.max(help_points.getOrDefault(helper, 0f) + points, 0f));
        WCombatTrackerMod.LOGGER.debug("new help detected: {}", points);
        WCombatTrackerMod.LOGGER.debug("new helper points: {}", help_points.get(helper));
    }

    public UUID getUUID(){
        return null;
    }

    public void onInteraction(){
        if(!NeoForge.EVENT_BUS.post(new CombatEntInteractionEvent(this, this.combat)).isCanceled())
            last_interaction = ServerLifecycleHooks.getCurrentServer().overworld().getGameTime();
    }

    public boolean shouldRemove(){
        return Config.outOfCombatTime != -1 && last_interaction + Config.outOfCombatTime < ServerLifecycleHooks.getCurrentServer().overworld().getGameTime();
    }
}
