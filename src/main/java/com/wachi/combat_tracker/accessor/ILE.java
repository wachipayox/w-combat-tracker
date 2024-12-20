package com.wachi.combat_tracker.accessor;

import com.wachi.combat_tracker.tracker.DeathDistributor;
import net.minecraft.world.damagesource.DamageSource;


public interface ILE {
    void setDeadDistributor(DeathDistributor DD);

    DeathDistributor getDeadDistributor();

    float callGetDamageAfterArmorAbsorb(DamageSource pDamageSource, float pDamageAmount);

    float callGetDamageAfterMagicAbsorb(DamageSource pDamageSource, float pDamageAmount);
}
