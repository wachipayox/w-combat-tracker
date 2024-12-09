package com.wachi.combat_tracker.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

import javax.annotation.Nullable;

/**Gives you a source, but you can't cancel it or modifying it, for that use LivingHealEvent,
 * also gives the real heal amount*/
public class LivingHealWithSourceEvent extends LivingEvent implements ICancellableEvent {

    private final Entity source;
    private final float amount;
    private final float real_amount;


    public LivingHealWithSourceEvent(LivingEntity entity, Entity source, float amount) {
        super(entity);
        this.source = source;
        this.amount = amount;
        this.real_amount = entity.getHealth() + amount > entity.getMaxHealth() ? entity.getMaxHealth() - entity.getHealth() : amount;
    }

    /**@return the heal amount*/
    public float getAmount(){
        return this.amount;
    }

    /**@return the real amount of heal that has been applied to entity*/
    public float getRealAmount(){
        return this.real_amount;
    }

    @Nullable
    public Entity getSource(){
        return this.source;
    }
}
