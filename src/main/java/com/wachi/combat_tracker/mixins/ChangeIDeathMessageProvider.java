package com.wachi.combat_tracker.mixins;

import com.wachi.combat_tracker.deathmessage.CombatDeathMessageProvider;
import com.wachi.combat_tracker.tracker.CombatManager;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.IDeathMessageProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CombatTracker.class)
public class ChangeIDeathMessageProvider {

    @Shadow @Final
    private LivingEntity mob;

    @Redirect(method = "getDeathMessage", at = @At(value = "INVOKE", target = "net/minecraft/world/damagesource/DeathMessageType.getMessageFunction ()Lnet/neoforged/neoforge/common/damagesource/IDeathMessageProvider;"))
    public IDeathMessageProvider getDeathMessageProvider(DeathMessageType instance){
        if(instance.getMessageFunction() == IDeathMessageProvider.DEFAULT && CombatManager.isEntityInCombat(mob))
            return new CombatDeathMessageProvider();

        return instance.getMessageFunction();
    }
}
