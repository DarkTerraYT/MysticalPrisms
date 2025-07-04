package io.lerpmcgerk.mysticalprisms.recipe;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MysticalPrisms.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MysticalPrisms.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrystalGrowerRecipe>> CRYSTAL_GROWER_SERIALIZER =
            SERIALIZERS.register("crystal_grower", CrystalGrowerRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CrystalGrowerRecipe>> CRYSTAL_GROWER_TYPE = RECIPE_TYPES.register("crystal_grower", () -> new RecipeType<CrystalGrowerRecipe>() {
        @Override
        public String toString() {
            return "crystal_grower";
        }
    });

    public static void register(IEventBus bus)
    {
        SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
    }
}
