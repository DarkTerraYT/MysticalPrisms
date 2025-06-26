package io.lerpmcgerk.mysticalprisms.datagen;

import io.lerpmcgerk.mysticalprisms.block.ModBlocks;
import io.lerpmcgerk.mysticalprisms.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.JADE_BLOCK.get());
        dropSelf(ModBlocks.SAPPHIRE_BLOCK.get());
        dropSelf(ModBlocks.AMBER_BLOCK.get());
        dropSelf(ModBlocks.LAVA_CRYSTAL_BLOCK.get());
        dropSelf(ModBlocks.CRYSTAL_GROWER.get());

        add(ModBlocks.JADE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.JADE_ORE.get(), ModItems.JADE.get(), 1, 3));
        add(ModBlocks.JADE_DEEPSLATE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.JADE_DEEPSLATE_ORE.get(), ModItems.JADE.get(), 2, 4));
        add(ModBlocks.SAPPHIRE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.SAPPHIRE_ORE.get(), ModItems.SAPPHIRE.get(), 1, 3));
        add(ModBlocks.SAPPHIRE_DEEPSLATE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.SAPPHIRE_DEEPSLATE_ORE.get(), ModItems.SAPPHIRE.get(), 2, 4));
        add(ModBlocks.AMBER_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.AMBER_ORE.get(), ModItems.AMBER.get(), 1, 3));
        add(ModBlocks.AMBER_DEEPSLATE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.AMBER_DEEPSLATE_ORE.get(), ModItems.AMBER.get(), 2, 4));
        add(ModBlocks.LAVA_CRYSTAL_ORE.get(), createOreDrop(ModBlocks.LAVA_CRYSTAL_ORE.get(), ModItems.LAVA_CRYSTAL.get()));

    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}