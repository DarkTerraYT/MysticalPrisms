package io.lerpmcgerk.mysticalprisms.datagen;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import io.lerpmcgerk.mysticalprisms.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MysticalPrisms.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.JADE_BLOCK.get())
                .add(ModBlocks.JADE_ORE.get())
                .add(ModBlocks.JADE_DEEPSLATE_ORE.get())
                .add(ModBlocks.SAPPHIRE_BLOCK.get())
                .add(ModBlocks.SAPPHIRE_ORE.get())
                .add(ModBlocks.SAPPHIRE_DEEPSLATE_ORE.get())
                .add(ModBlocks.AMBER_BLOCK.get())
                .add(ModBlocks.AMBER_ORE.get())
                .add(ModBlocks.AMBER_DEEPSLATE_ORE.get())
                .add(ModBlocks.LAVA_CRYSTAL_BLOCK.get())
                .add(ModBlocks.LAVA_CRYSTAL_ORE.get())
                .add(ModBlocks.CRYSTAL_GROWER.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.JADE_BLOCK.get())
                .add(ModBlocks.JADE_ORE.get())
                .add(ModBlocks.JADE_DEEPSLATE_ORE.get())
                .add(ModBlocks.SAPPHIRE_BLOCK.get())
                .add(ModBlocks.SAPPHIRE_ORE.get())
                .add(ModBlocks.SAPPHIRE_DEEPSLATE_ORE.get())
                .add(ModBlocks.AMBER_BLOCK.get())
                .add(ModBlocks.AMBER_ORE.get())
                .add(ModBlocks.AMBER_DEEPSLATE_ORE.get());
    }
}
