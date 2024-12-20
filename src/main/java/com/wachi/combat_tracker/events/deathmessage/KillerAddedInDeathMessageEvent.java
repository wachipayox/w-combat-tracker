package com.wachi.combat_tracker.events.deathmessage;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class KillerAddedInDeathMessageEvent extends Event implements ICancellableEvent {

    private Component killer_display_name;
    public final Entity killer, victim;

    /**represents if the killer is the last to hit the victim*/
    public final boolean killerIsLastHitter;

    /**represents if the killer display name is in the first part of the text or in the ", / and"*/
    public final boolean isInHelpList;

    public KillerAddedInDeathMessageEvent(Entity killer, Entity victim, boolean isLastHitter, boolean isInHelpList) {
        this.killer_display_name = killer.getDisplayName();
        this.killer = killer;
        this.victim = victim;
        this.killerIsLastHitter = isLastHitter;
        this.isInHelpList = isInHelpList;
    }

    public void setKillerDisplayName(Component name) {
        this.killer_display_name = name;
    }

    public Component getKillerDisplayName() {
        return this.killer_display_name;
    }
}
