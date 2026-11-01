package com.woulvee.eggstract.compat.jei;

import com.woulvee.eggstract.Config;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class EggstractingCategory extends AbstractRecipeCategory<EggstractingRecipe> {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 58;

    public EggstractingCategory(IGuiHelper guiHelper) {
        super(EggstractJeiPlugin.RECIPE_TYPE, Component.translatable("jei.eggstract.category"), guiHelper.createDrawableItemLike(Items.ENCHANTED_BOOK), WIDTH, HEIGHT);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EggstractingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(38, 1).setStandardSlotBackground().addItemStacks(recipe.books());
        builder.addOutputSlot(94, 1).setOutputSlotBackground().add(recipe.egg());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, EggstractingRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrowWidget().setPosition(62, 1);
        builder.addText(recipe.entity().getDescription(), WIDTH, 10).setPosition(0, 26);
        builder.addText(Component.translatable("jei.eggstract.chances",
                        EggstractJeiPlugin.percent(Config.dropChance(1)),
                        EggstractJeiPlugin.percent(Config.dropChance(2)),
                        EggstractJeiPlugin.percent(Config.dropChance(3))), WIDTH, 20)
                .setPosition(0, 38);
    }

    @Override
    public Identifier getIdentifier(EggstractingRecipe recipe) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(recipe.entity());
    }
}
