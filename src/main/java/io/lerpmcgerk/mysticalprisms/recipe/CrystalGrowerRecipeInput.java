package io.lerpmcgerk.mysticalprisms.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public record CrystalGrowerRecipeInput(ItemStack inputTop, ItemStack inputBottom, FluidStack fluid) implements RecipeInput {

    @Override
    public @NotNull ItemStack getItem(int i) {
        return switch (i)
        {
            case 0 -> inputTop;
            case 1 -> inputBottom;
            default -> inputTop;
        };
    }

    public FluidStack getFluid()
    {
        return fluid;
    }

    @Override
    public int size() {
        return 2;
    }
}
