package io.lerpmcgerk.mysticalprisms.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public record CrystallizerRecipeInput(ItemStack input, FluidStack fluid) implements RecipeInput {
    @Override
    public @NotNull ItemStack getItem(int i) {
        return input;
    }

    public FluidStack getFluid()
    {
        return fluid;
    }

    @Override
    public int size() {
        return 1;
    }
}
