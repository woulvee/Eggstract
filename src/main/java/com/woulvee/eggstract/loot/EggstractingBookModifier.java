package com.woulvee.eggstract.loot;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.woulvee.eggstract.Config;
import com.woulvee.eggstract.Eggstract;
import com.woulvee.eggstract.Eggstracting;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

// Adds Eggstracting books to bartering, fishing and chest loot, according to Config
public class EggstractingBookModifier extends LootModifier {
    public static final MapCodec<EggstractingBookModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
            .apply(instance, EggstractingBookModifier::new));

    private static final Identifier BARTERING_TABLE = Identifier.withDefaultNamespace("gameplay/piglin_bartering");
    private static final Identifier FISHING_TABLE = Identifier.withDefaultNamespace("gameplay/fishing");

    public EggstractingBookModifier(LootItemCondition[] conditions, int priority) {
        super(conditions, priority);
    }

    private static Config.@Nullable Source sourceOf(Identifier table) {
        if (table.equals(BARTERING_TABLE)) {
            return Config.BARTERING;
        }
        if (table.equals(FISHING_TABLE)) {
            return Config.FISHING;
        }
        // Mods use the same folder for their chests
        return table.getPath().startsWith("chests/") ? Config.GENERATED_LOOT : null;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Config.Source source = sourceOf(context.getQueriedLootTableId());
        if (source != null && source.roll(context.getRandom())) {
            Eggstracting.holder(context.getLevel().registryAccess()).ifPresent(enchantment ->
                    generatedLoot.add(Eggstracting.book(enchantment, Eggstracting.randomLevel(enchantment, context.getRandom()))));
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return Eggstract.EGGSTRACTING_BOOK.get();
    }
}
