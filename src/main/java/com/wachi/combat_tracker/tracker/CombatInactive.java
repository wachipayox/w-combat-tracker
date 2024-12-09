package com.wachi.combat_tracker.tracker;

import net.minecraft.network.chat.Component;
import java.util.UUID;

public class CombatInactive extends CombatEnt{
    public final UUID uuid;
    public final Component name;

    public CombatInactive(CombatActive cA){
        super(cA.combat);
        this.API_MAP = cA.API_MAP;
        this.uuid = cA.getUUID();
        this.name = cA.entity.getDisplayName();
    }

    @Override
    public UUID getUUID(){
        return uuid;
    }
}
