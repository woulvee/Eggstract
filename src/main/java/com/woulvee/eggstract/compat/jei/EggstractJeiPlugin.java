package com.woulvee.eggstract.compat.jei;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.woulvee.eggstract.Config;
import com.woulvee.eggstract.Eggstract;
import com.woulvee.eggstract.Eggstracting;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.fml.ModList;

@JeiPlugin
public class EggstractJeiPlugin implements IModPlugin {
    static final IRecipeType<EggstractingRecipe> RECIPE_TYPE = IRecipeType.create(Eggstract.MODID, "eggstracting", EggstractingRecipe.class);

    static String percent(double chance) {
        return BigDecimal.valueOf(chance).stripTrailingZeros().toPlainString();
    }

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Eggstract.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new EggstractingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        Holder<Enchantment> enchantment = connection == null ? null : Eggstracting.holder(connection.registryAccess()).orElse(null);
        if (enchantment == null) {
            return;
        }

        List<ItemStack> books = IntStream.rangeClosed(1, enchantment.value().getMaxLevel())
                .mapToObj(level -> Eggstracting.book(enchantment, level))
                .toList();

        List<EggstractingRecipe> recipes = new ArrayList<>();
        BuiltInRegistries.ENTITY_TYPE.forEach(type -> {
            if (!Config.isBlacklisted(type)) {
                SpawnEggItem.byId(type).ifPresent(egg -> recipes.add(new EggstractingRecipe(type, books, new ItemStack(egg))));
            }
        });
        registration.addRecipes(RECIPE_TYPE, recipes);
        registration.addItemStackInfo(books, sourceInfo());
    }

    // Runs after JER filled its mob entries and before JEI indexes them as recipes
    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        if (ModList.get().isLoaded("jeresources")) {
            try {
                JerCompat.addSpawnEggs();
            } catch (LinkageError e) {
                // JerCompat uses JER internals, which can change between versions
                Eggstract.LOGGER.warn("Incompatible Just Enough Resources version, spawn eggs are missing from its mob pages", e);
            }
        }
    }

    private static Component[] sourceInfo() {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("jei.eggstract.info.intro"));
        addSource(lines, Config.ENCHANTING_TABLE, "enchanting_table");
        addSource(lines, Config.TRADING, "trading");
        addSource(lines, Config.BARTERING, "bartering");
        addSource(lines, Config.FISHING, "fishing");
        addSource(lines, Config.GENERATED_LOOT, "generated_loot");
        lines.add(1, Component.translatable(lines.size() > 1 ? "jei.eggstract.info.sources" : "jei.eggstract.info.none"));
        return lines.toArray(Component[]::new);
    }

    private static void addSource(List<Component> lines, Config.Source source, String name) {
        if (source.isEnabled()) {
            lines.add(Component.translatable("jei.eggstract.source." + name, percent(source.percent())));
        }
    }
}
