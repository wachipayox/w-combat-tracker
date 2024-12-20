package com.wachi.combat_tracker.events.deathmessage;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class KillerLoggedInDeathEvent extends Event implements ICancellableEvent {

    public final Entity killer, victim;
    public final float percentage, total;

    public KillerLoggedInDeathEvent(Entity killer, Entity victim, float percentage, float total) {
        this.killer = killer;
        this.victim = victim;
        this.percentage = percentage;
        this.total = total;
    }
}
