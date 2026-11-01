package com.woulvee.eggstract;

import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public final class Eggstracting {
    // Deliberately in no vanilla enchantment tag: Config decides where it can be obtained
    public static final ResourceKey<Enchantment> KEY = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Eggstract.MODID, "eggstracting"));

    private Eggstracting() {
    }

    // Datapacks can remove the enchantment, so it may be missing
    public static Optional<Holder.Reference<Enchantment>> holder(HolderLookup.Provider registries) {
        return registries.lookupOrThrow(Registries.ENCHANTMENT).get(KEY);
    }

    public static ItemStack book(Holder<Enchantment> enchantment, int level) {
        return EnchantmentHelper.createBook(new EnchantmentInstance(enchantment, level));
    }

    public static int randomLevel(Holder<Enchantment> enchantment, RandomSource random) {
        return 1 + random.nextInt(enchantment.value().getMaxLevel());
    }
}
