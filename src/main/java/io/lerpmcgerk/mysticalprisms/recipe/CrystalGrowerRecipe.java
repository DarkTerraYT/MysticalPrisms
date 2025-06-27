package io.lerpmcgerk.mysticalprisms.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public record CrystalGrowerRecipe(Ingredient inputTop, Ingredient inputBottom, /*FluidIngredient fluidInput,*/ int time, ItemStack output) implements Recipe<CrystalGrowerRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputTop);
        list.add(inputBottom);

        return list;
    }

    @Override
    public boolean matches(CrystalGrowerRecipeInput crystalGrowerRecipeInput, Level level) {
        if(level.isClientSide())
        {
            return false;
        }

        if(inputBottom.hasNoItems())
        {
            MysticalPrisms.LOGGER.warn("Bottom slot has no item!");
        }

        return inputTop.test(crystalGrowerRecipeInput.getItem(0)) && inputBottom.test(crystalGrowerRecipeInput.getItem(1));
    }

    @Override
    public ItemStack assemble(CrystalGrowerRecipeInput crystalGrowerRecipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public int time() {
        return time;
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
        return ModRecipes.CRYSTAL_GROWER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CRYSTAL_GROWER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CrystalGrowerRecipe>
    {
        private static final StreamCodec<RegistryFriendlyByteBuf, Integer> STREAM_INT =  new StreamCodec<RegistryFriendlyByteBuf, Integer>() {
            @Override
            public Integer decode(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
                return registryFriendlyByteBuf.readInt();
            }

            @Override
            public void encode(RegistryFriendlyByteBuf o, Integer integer) {
                o.writeInt(integer);
            }
        };

        public static final MapCodec<CrystalGrowerRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("top").forGetter(CrystalGrowerRecipe::inputTop),
                Ingredient.CODEC.fieldOf("bottom").orElse(Ingredient.EMPTY).forGetter(CrystalGrowerRecipe::inputBottom),
                /*FluidIngredient.CODEC.orElse(FluidIngredient.empty()).fieldOf("fluid").forGetter(CrystalGrowerRecipe::fluidInput),*/
                Codec.INT.fieldOf("time").forGetter(CrystalGrowerRecipe::time),
                ItemStack.CODEC.fieldOf("result").forGetter(CrystalGrowerRecipe::output)
        ).apply(inst, CrystalGrowerRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CrystalGrowerRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CrystalGrowerRecipe::inputTop,
                        Ingredient.CONTENTS_STREAM_CODEC, CrystalGrowerRecipe::inputBottom,
                        /*FluidIngredient.STREAM_CODEC, CrystalGrowerRecipe::fluidInput,*/
                        STREAM_INT, CrystalGrowerRecipe::time,
                        ItemStack.STREAM_CODEC, CrystalGrowerRecipe::output,
                        CrystalGrowerRecipe::new);

        @Override
        public MapCodec<CrystalGrowerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrystalGrowerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
