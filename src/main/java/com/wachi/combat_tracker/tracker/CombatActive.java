package com.wachi.combat_tracker.tracker;

import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class CombatActive extends CombatEnt{
    public final Entity entity;

    public CombatActive(Entity entity, Combat combat){
        super(combat);
        this.entity = entity;
    }

    @Override
    public UUID getUUID(){
        return entity.getUUID();
    }

    @Override
    public boolean shouldRemove(){
        return super.shouldRemove() || this.entity == null || !this.entity.isAlive();
    }
}
