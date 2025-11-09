package io.lerpmcgerk.mysticalprisms.datagen;

import io.lerpmcgerk.mysticalprisms.block.ModBlocks;
import io.lerpmcgerk.mysticalprisms.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.JADE_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.JADE.get())
                .unlockedBy("has_jade", has(ModItems.JADE)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SAPPHIRE_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.SAPPHIRE.get())
                .unlockedBy("has_sapphire", has(ModItems.SAPPHIRE)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.AMBER_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.AMBER.get())
                .unlockedBy("has_amber", has(ModItems.AMBER)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LAVA_CRYSTAL_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.LAVA_CRYSTAL.get())
                .unlockedBy("has_lava_crystal", has(ModItems.LAVA_CRYSTAL)).save(recipeOutput);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CRYSTAL_GROWER.get())
                .pattern("AAA")
                .pattern("BCB")
                .pattern("AAA")
                .define('A', Items.COBBLESTONE)
                .define('B', Items.QUARTZ)
                .define('C', Items.GOLD_BLOCK)
                .unlockedBy("has_jade", has(ModItems.JADE)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.JADE.get(), 9)
                .requires(ModBlocks.JADE_BLOCK)
                .unlockedBy("has_jade_block", has(ModBlocks.JADE_BLOCK)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SAPPHIRE.get(), 9)
                .requires(ModBlocks.SAPPHIRE_BLOCK)
                .unlockedBy("has_sapphire_block", has(ModBlocks.SAPPHIRE_BLOCK)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.AMBER.get(), 9)
                .requires(ModBlocks.AMBER_BLOCK)
                .unlockedBy("has_amber_block", has(ModBlocks.AMBER_BLOCK)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LAVA_CRYSTAL.get(), 9)
                .requires(ModBlocks.LAVA_CRYSTAL_BLOCK)
                .unlockedBy("has_lava_crystal_block", has(ModBlocks.LAVA_CRYSTAL_BLOCK)).save(recipeOutput);


        List<ItemLike> JADE_SMELTABLES = List.of(ModBlocks.JADE_ORE.asItem(), ModBlocks.JADE_DEEPSLATE_ORE.asItem());
        List<ItemLike> SAPPHIRE_SMELTABLES = List.of(ModBlocks.SAPPHIRE_ORE.asItem(), ModBlocks.SAPPHIRE_DEEPSLATE_ORE.asItem());
        List<ItemLike> AMBER_SMELTABLES = List.of(ModBlocks.AMBER_ORE.asItem(), ModBlocks.AMBER_DEEPSLATE_ORE.asItem());
        List<ItemLike> LAVA_CRYSTAL_SMELTABLES = List.of(ModBlocks.LAVA_CRYSTAL_ORE.asItem());

        oreProcessing(recipeOutput, JADE_SMELTABLES, RecipeCategory.MISC, ModItems.JADE, 0.5f, 200, "jade");
        oreProcessing(recipeOutput, SAPPHIRE_SMELTABLES, RecipeCategory.MISC, ModItems.SAPPHIRE, 0.55f, 200, "sapphire");
        oreProcessing(recipeOutput, AMBER_SMELTABLES, RecipeCategory.MISC, ModItems.AMBER, 0.6f, 200, "amber");
        oreProcessing(recipeOutput, LAVA_CRYSTAL_SMELTABLES, RecipeCategory.MISC, ModItems.LAVA_CRYSTAL, 0.65f, 200, "lava_crystal");
    }

    private static void oreProcessing(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group)
    {
        oreSmelting(recipeOutput, ingredients, category, result, experience, cookingTime, group);
        oreBlasting(recipeOutput, ingredients, category, result, experience, cookingTime / 100, group);
    }
}
