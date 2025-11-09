package io.lerpmcgerk.mysticalprisms.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

public record CrystallizerRecipe(Ingredient input, SizedFluidIngredient fluid, int energy, int time, ItemStack output) implements Recipe<CrystallizerRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);

        return list;
    }

    @Override
    public boolean matches(CrystallizerRecipeInput crystallizerRecipeInput, Level level) {
        if(level.isClientSide())
        {
            return false;
        }

        return input.test(crystallizerRecipeInput.getItem(0)) && fluid.test(crystallizerRecipeInput.getFluid());
    }

    @Override
    public ItemStack assemble(CrystallizerRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public int time() {
        return time;
    }

    public SizedFluidIngredient getFluidIngredient()
    {
        return fluid;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CRYSTALLIZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CRYSTAL_GROWER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CrystallizerRecipe>
    {
        private static final StreamCodec<RegistryFriendlyByteBuf, Integer> STREAM_INT =  new StreamCodec<RegistryFriendlyByteBuf, Integer>() {
            @Override
            public @NotNull Integer decode(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
                return registryFriendlyByteBuf.readInt();
            }

            @Override
            public void encode(RegistryFriendlyByteBuf o, Integer integer) {
                o.writeInt(integer);
            }
        };

        public static final MapCodec<CrystallizerRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
          Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(CrystallizerRecipe::input),
          SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").orElse(SizedFluidIngredient.of(Fluids.WATER, 1)).forGetter(CrystallizerRecipe::fluid),
          Codec.INT.fieldOf("energy").forGetter(CrystallizerRecipe::energy),
          Codec.INT.fieldOf("time").forGetter(CrystallizerRecipe::time),
          ItemStack.CODEC.fieldOf("result").forGetter(CrystallizerRecipe::output)
        ).apply(inst, CrystallizerRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CrystallizerRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CrystallizerRecipe::input,
                        SizedFluidIngredient.STREAM_CODEC, CrystallizerRecipe::fluid,
                        STREAM_INT, CrystallizerRecipe::energy,
                        STREAM_INT, CrystallizerRecipe::time,
                        ItemStack.STREAM_CODEC, CrystallizerRecipe::output,
                        CrystallizerRecipe::new);

        @Override
        public MapCodec<CrystallizerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrystallizerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
