package com.woulvee.eggstract.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.woulvee.eggstract.Config;

import jeresources.api.conditionals.Conditional;
import jeresources.api.drop.LootDrop;
import jeresources.api.render.TextModifier;
import jeresources.entry.MobEntry;
import jeresources.registry.MobRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

// Only loaded when Just Enough Resources is installed
final class JerCompat {
    private static final Conditional REQUIRES_EGGSTRACTING = new Conditional("jer.eggstract.requires_eggstracting", TextModifier.lightCyan);

    private JerCompat() {
    }

    // JER's API can only register new mob pages, so this edits its entries directly
    static void addSpawnEggs() {
        for (MobEntry entry : MobRegistry.getInstance().getMobs()) {
            EntityType<?> type = entry.getEntity().getType();
            if (Config.isBlacklisted(type)) {
                continue;
            }
            SpawnEggItem.byId(type).ifPresent(egg -> {
                List<LootDrop> drops = new ArrayList<>(entry.getDrops());
                drops.add(new LootDrop(new ItemStack(egg), 1, 1, (float) Config.dropChance(3) / 100, REQUIRES_EGGSTRACTING));
                entry.setDrops(drops);
            });
        }
    }
}
