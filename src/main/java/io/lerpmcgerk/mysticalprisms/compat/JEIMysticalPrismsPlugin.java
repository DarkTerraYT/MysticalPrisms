package io.lerpmcgerk.mysticalprisms.compat;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import io.lerpmcgerk.mysticalprisms.block.ModBlocks;
import io.lerpmcgerk.mysticalprisms.recipe.CrystalGrowerRecipe;
import io.lerpmcgerk.mysticalprisms.recipe.ModRecipes;
import io.lerpmcgerk.mysticalprisms.screen.custom.CrystalGrowerScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIMysticalPrismsPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MysticalPrisms.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CrystalGrowerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if(Minecraft.getInstance().level == null)
        {
            return;
        }
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<CrystalGrowerRecipe> crystalGrowerRecipes = recipeManager.getAllRecipesFor(ModRecipes.CRYSTAL_GROWER_TYPE.get()).stream().map(RecipeHolder::value).toList();

        registration.addRecipes(CrystalGrowerRecipeCategory.CRYSTAL_GROWER_RECIPE_TYPE, crystalGrowerRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CrystalGrowerScreen.class, 88, 40, 31, 4, CrystalGrowerRecipeCategory.CRYSTAL_GROWER_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.CRYSTAL_GROWER.get(), CrystalGrowerRecipeCategory.CRYSTAL_GROWER_RECIPE_TYPE);
    }
}
