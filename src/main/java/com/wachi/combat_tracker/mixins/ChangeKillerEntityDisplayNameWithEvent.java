package com.wachi.combat_tracker.mixins;

import com.wachi.combat_tracker.events.deathmessage.KillerAddedInDeathMessageEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public class ChangeKillerEntityDisplayNameWithEvent {

    @Unique
    private LivingEntity mob = null;


    @Inject(method = "getLocalizedDeathMessage", at = @At("HEAD"))
    public void getDeathMessageProvider(LivingEntity pLivingEntity, CallbackInfoReturnable<Component> cir){
        mob = pLivingEntity;
    }

    @Redirect(method = "getLocalizedDeathMessage", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/Entity.getDisplayName ()Lnet/minecraft/network/chat/Component;"))
    public Component getDeathMessageProvider(Entity instance){
        return NeoForge.EVENT_BUS.post(new KillerAddedInDeathMessageEvent(instance, mob, true, false)).getKillerDisplayName();
    }

    @Redirect(method = "getLocalizedDeathMessage", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/LivingEntity.getDisplayName ()Lnet/minecraft/network/chat/Component;", ordinal = 1))
    public Component getDeathMessageProvider(LivingEntity instance){
        return NeoForge.EVENT_BUS.post(new KillerAddedInDeathMessageEvent(instance, mob, false, false)).getKillerDisplayName();
    }
}
