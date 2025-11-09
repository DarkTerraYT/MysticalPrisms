package io.lerpmcgerk.mysticalprisms.datagen;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import io.lerpmcgerk.mysticalprisms.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MysticalPrisms.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.JADE_BLOCK);
        blockWithItem(ModBlocks.JADE_ORE);
        blockWithItem(ModBlocks.JADE_DEEPSLATE_ORE);
        blockWithItem(ModBlocks.SAPPHIRE_BLOCK);
        blockWithItem(ModBlocks.SAPPHIRE_ORE);
        blockWithItem(ModBlocks.SAPPHIRE_DEEPSLATE_ORE);
        blockWithItem(ModBlocks.AMBER_BLOCK);
        blockWithItem(ModBlocks.AMBER_ORE);
        blockWithItem(ModBlocks.AMBER_DEEPSLATE_ORE);
        blockWithItem(ModBlocks.LAVA_CRYSTAL_BLOCK);
        blockWithItem(ModBlocks.LAVA_CRYSTAL_ORE);
        blockWithItem(ModBlocks.ENDER_CRYSTAL_BLOCK);
        blockWithItem(ModBlocks.ENDER_CRYSTAL_ORE);
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
