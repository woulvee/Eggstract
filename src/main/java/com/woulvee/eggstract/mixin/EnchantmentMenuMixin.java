package com.woulvee.eggstract.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.woulvee.eggstract.Config;
import com.woulvee.eggstract.Eggstracting;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {
    @Shadow
    @Final
    private DataSlot enchantmentSeed;

    // This method runs once for the preview and again for the actual enchanting, both have to
    // agree. So the result may only depend on the seed, and the menu's own random stays untouched.
    @Inject(method = "getEnchantmentList", at = @At("RETURN"), cancellable = true)
    private void eggstract$addEggstracting(RegistryAccess registries, ItemStack stack, int slot, int cost, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        Holder<Enchantment> enchantment = Eggstracting.holder(registries).orElse(null);
        if (enchantment == null) {
            return;
        }

        RandomSource random = RandomSource.create(this.enchantmentSeed.get() + slot);
        if (!Config.ENCHANTING_TABLE.roll(random)) {
            return;
        }

        List<EnchantmentInstance> selected = EnchantmentHelper.selectEnchantment(random, stack, cost, Stream.of(enchantment));
        List<EnchantmentInstance> current = cir.getReturnValue();
        boolean compatible = current.stream().allMatch(other -> Enchantment.areCompatible(other.enchantment(), enchantment));
        if (!selected.isEmpty() && compatible) {
            List<EnchantmentInstance> combined = new ArrayList<>(current);
            combined.addAll(selected);
            cir.setReturnValue(combined);
        }
    }
}
