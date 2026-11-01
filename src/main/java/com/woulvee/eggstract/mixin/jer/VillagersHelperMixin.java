package com.woulvee.eggstract.mixin.jer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import jeresources.registry.VillagerRegistry;
import jeresources.util.VillagersHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;

// JER 1.10.1.59 reads villager trades from the client registries, which never contain them.
// The exception aborts JER's whole JEI plugin, this keeps at least its other pages working.
@Mixin(VillagersHelper.class)
public abstract class VillagersHelperMixin {
    @Inject(method = "initRegistry", at = @At("HEAD"), cancellable = true)
    private static void eggstract$skipWithoutTradeRegistry(VillagerRegistry registry, CallbackInfo ci) {
        if (Minecraft.getInstance().level.registryAccess().lookup(Registries.TRADE_SET).isEmpty()) {
            ci.cancel();
        }
    }
}
