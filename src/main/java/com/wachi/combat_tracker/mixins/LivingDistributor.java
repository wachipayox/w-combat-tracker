package com.wachi.combat_tracker.mixins;

import com.wachi.combat_tracker.accessor.ILE;
import com.wachi.combat_tracker.tracker.DeathDistributor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public abstract class LivingDistributor implements ILE {

    @Invoker("getDamageAfterMagicAbsorb")
    public abstract float callGetDamageAfterMagicAbsorb(DamageSource pDamageSource, float pDamageAmount);

    @Invoker("getDamageAfterArmorAbsorb")
    public abstract float callGetDamageAfterArmorAbsorb(DamageSource pDamageSource, float pDamageAmount);

    @Unique
    private DeathDistributor deadDistributor;

    @Override
    public void setDeadDistributor(DeathDistributor DD) { deadDistributor = DD; }

    @Override
    public DeathDistributor getDeadDistributor() { return deadDistributor; }
}
