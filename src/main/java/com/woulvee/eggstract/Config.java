package com.woulvee.eggstract;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.DoubleValue LEVEL_1_CHANCE;
    private static final ModConfigSpec.DoubleValue LEVEL_2_CHANCE;
    private static final ModConfigSpec.DoubleValue LEVEL_3_CHANCE;
    private static final ModConfigSpec.DoubleValue ABOVE_MAX_LEVEL_CHANCE;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    public static final Source ENCHANTING_TABLE;
    public static final Source TRADING;
    public static final Source BARTERING;
    public static final Source FISHING;
    public static final Source GENERATED_LOOT;

    static {
        BUILDER.comment("Spawn egg drops. All chances are percentages from 0 to 100.").push("drops");
        LEVEL_1_CHANCE = chance("level1Chance", "Drop chance of Eggstracting I", 0.5);
        LEVEL_2_CHANCE = chance("level2Chance", "Drop chance of Eggstracting II", 1.0);
        LEVEL_3_CHANCE = chance("level3Chance", "Drop chance of Eggstracting III", 1.5);
        ABOVE_MAX_LEVEL_CHANCE = chance("aboveMaxLevelChance", "Drop chance of levels above III, which only exist through commands", 100.0);
        BLACKLIST = BUILDER
                .comment("Mobs that never drop their spawn egg.",
                        "Entries can be a mob (\"minecraft:wither\"), all mobs of a mod (\"somemod:*\") or an entity tag (\"#minecraft:raiders\").")
                .defineListAllowEmpty("blacklist", List.of("minecraft:wither", "minecraft:ender_dragon"), () -> "", Config::isBlacklistEntry);
        BUILDER.pop();

        BUILDER.comment("Ways to obtain the enchantment. All chances are percentages from 0 to 100.").push("sources");
        ENCHANTING_TABLE = source("enchantingTable", true, 2.0, "per offer. Only offers with a high level cost qualify");
        TRADING = source("trading", false, 1.0, "that an enchanted book trade of a villager sells this enchantment");
        BARTERING = source("bartering", false, 0.5, "per piglin barter");
        FISHING = source("fishing", true, 0.2, "per fishing catch");
        GENERATED_LOOT = source("generatedLoot", true, 1.0, "per generated loot chest");
        BUILDER.pop();
    }

    static final ModConfigSpec SPEC = BUILDER.build();

    private static ModConfigSpec.DoubleValue chance(String name, String comment, double defaultValue) {
        return BUILDER.comment(comment).defineInRange(name, defaultValue, 0.0, 100.0);
    }

    private static Source source(String name, boolean enabled, double chance, String chanceComment) {
        return new Source(
                BUILDER.define(name + "Enabled", enabled),
                chance(name + "Chance", "Chance " + chanceComment, chance));
    }

    private static boolean isBlacklistEntry(Object obj) {
        if (!(obj instanceof String entry) || entry.isEmpty()) {
            return false;
        }
        if (entry.endsWith(":*")) {
            return Identifier.tryParse(entry.substring(0, entry.length() - 1) + "any") != null;
        }
        return Identifier.tryParse(entry.startsWith("#") ? entry.substring(1) : entry) != null;
    }

    public static double dropChance(int level) {
        return (switch (level) {
            case 1 -> LEVEL_1_CHANCE;
            case 2 -> LEVEL_2_CHANCE;
            case 3 -> LEVEL_3_CHANCE;
            default -> ABOVE_MAX_LEVEL_CHANCE;
        }).getAsDouble();
    }

    public static boolean isBlacklisted(EntityType<?> type) {
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        for (String entry : BLACKLIST.get()) {
            if (entry.startsWith("#")) {
                if (type.builtInRegistryHolder().is(TagKey.create(Registries.ENTITY_TYPE, Identifier.parse(entry.substring(1))))) {
                    return true;
                }
            } else if (entry.endsWith(":*")) {
                if (id.getNamespace().equals(entry.substring(0, entry.length() - 2))) {
                    return true;
                }
            } else if (id.equals(Identifier.parse(entry))) {
                return true;
            }
        }
        return false;
    }

    public static boolean roll(RandomSource random, double percent) {
        return random.nextDouble() * 100 < percent;
    }

    public record Source(ModConfigSpec.BooleanValue enabled, ModConfigSpec.DoubleValue chance) {
        public boolean isEnabled() {
            return this.enabled.getAsBoolean();
        }

        public double percent() {
            return this.chance.getAsDouble();
        }

        public boolean roll(RandomSource random) {
            return this.isEnabled() && Config.roll(random, this.percent());
        }
    }
}
