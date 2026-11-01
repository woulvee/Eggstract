package com.woulvee.eggstract.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.woulvee.eggstract.Eggstract;
import com.woulvee.eggstract.Eggstracting;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

// Replaces only the model of Eggstracting books, so items/enchanted_book.json stays free for other mods
@Mixin(ItemModelResolver.class)
public abstract class ItemModelResolverMixin {
    private static final Identifier ENCHANTED_BOOK = Identifier.withDefaultNamespace("enchanted_book");

    @ModifyVariable(method = "appendItemLayers", at = @At("STORE"))
    private Identifier eggstract$useBookModel(Identifier modelId, ItemStackRenderState output, ItemStack item, ItemDisplayContext displayContext, Level level, ItemOwner owner, int seed) {
        ItemEnchantments enchantments = item.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchantments == null || !ENCHANTED_BOOK.equals(modelId)) {
            return modelId;
        }

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(Eggstracting.KEY)) {
                String suffix = entry.getIntValue() <= 3 ? String.valueOf(entry.getIntValue()) : "creative";
                return Identifier.fromNamespaceAndPath(Eggstract.MODID, "eggstracting_" + suffix);
            }
        }
        return modelId;
    }
}
