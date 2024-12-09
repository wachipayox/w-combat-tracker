package com.wachi.combat_tracker.mixins;

import com.wachi.combat_tracker.events.LivingHealWithSourceEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.effect.HealOrHarmMobEffect")
public class AddHealWithSource {

    @Shadow
    @Final
    private boolean isHarm;

    @Inject(method = "applyInstantenousEffect", at = @At("HEAD"))
    private void addHealWithSource(Entity pSource, Entity pIndirectSource, LivingEntity pLivingEntity, int pAmplifier, double pHealth, CallbackInfo ci) {
        if (isHarm == pLivingEntity.isInvertedHealAndHarm() && pIndirectSource != null) {
            int i = (int) (pHealth * (double) (4 << pAmplifier) + 0.5);

            NeoForge.EVENT_BUS.post(new LivingHealWithSourceEvent(pLivingEntity, pIndirectSource, (float) i));
        }
    }
}
