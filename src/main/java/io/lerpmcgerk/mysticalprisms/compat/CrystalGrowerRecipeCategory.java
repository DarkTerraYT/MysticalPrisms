package io.lerpmcgerk.mysticalprisms.compat;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import io.lerpmcgerk.mysticalprisms.block.ModBlocks;
import io.lerpmcgerk.mysticalprisms.recipe.CrystalGrowerRecipe;
import io.lerpmcgerk.mysticalprisms.recipe.ModRecipes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CrystalGrowerRecipeCategory implements IRecipeCategory<CrystalGrowerRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MysticalPrisms.MODID, "crystal_grower");
    public static final ResourceLocation FLUID_TANK_TEXTURE = ResourceLocation.fromNamespaceAndPath(MysticalPrisms.MODID, "textures/gui/fluid_tank.png");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MysticalPrisms.MODID, "textures/gui/crystal_grower/crystal_grower_gui_jei.png");

    public static final RecipeType<CrystalGrowerRecipe> CRYSTAL_GROWER_RECIPE_TYPE = new RecipeType<>(ModRecipes.CRYSTAL_GROWER_TYPE.getId(), CrystalGrowerRecipe.class);


    private final int x = 176 / 2;
    private final int y = 83 / 2;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable fluid_tank;

    public CrystalGrowerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 83);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CRYSTAL_GROWER));
        this.fluid_tank = helper.createDrawable(FLUID_TANK_TEXTURE, 0, 0, 18, 66);
    }

    @Override
    public RecipeType<CrystalGrowerRecipe> getRecipeType() {
        return CRYSTAL_GROWER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.mysticalprisms.crystal_grower");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrystalGrowerRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 67, 16).addIngredients(recipe.getIngredients().get(0));
        if(!recipe.getIngredients().get(1).test(ItemStack.EMPTY))
        {
            builder.addSlot(RecipeIngredientRole.INPUT, 67, 52).addIngredients(recipe.getIngredients().get(1));
        }
        if(!recipe.getFluidIngredient().ingredient().isEmpty())
        {
            builder.addSlot(RecipeIngredientRole.INPUT, 9, 10)
                    .setFluidRenderer(4000, false, 16, 64)
                    //.setOverlay(fluid_tank, 0, 0)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, Arrays.asList(recipe.getFluidIngredient().getFluids()));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 34).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }
    @Override
    public void draw(CrystalGrowerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        background.draw(guiGraphics);
    }
}
