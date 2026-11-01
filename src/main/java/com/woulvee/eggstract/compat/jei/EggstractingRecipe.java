package com.woulvee.eggstract.compat.jei;

import java.util.List;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public record EggstractingRecipe(EntityType<?> entity, List<ItemStack> books, ItemStack egg) {
}
