package com.wachi.combat_tracker.mixins;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface ILE {

    @Invoker("getDamageAfterMagicAbsorb")
    float callGetDamageAfterMagicAbsorb(DamageSource pDamageSource, float pDamageAmount);

    @Invoker("getDamageAfterArmorAbsorb")
    float callGetDamageAfterArmorAbsorb(DamageSource pDamageSource, float pDamageAmount);


}
