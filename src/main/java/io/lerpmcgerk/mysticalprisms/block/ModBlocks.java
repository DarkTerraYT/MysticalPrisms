package io.lerpmcgerk.mysticalprisms.block;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import io.lerpmcgerk.mysticalprisms.block.custom.CrystalGrowerBlock;
import io.lerpmcgerk.mysticalprisms.block.custom.CrystallizerBlock;
import io.lerpmcgerk.mysticalprisms.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MysticalPrisms.MODID);

    // Compacted Blocks
    public static final DeferredBlock<Block> JADE_BLOCK = registerBlock("jade_block", () -> new Block(BlockBehaviour.Properties.of()
            .strength(1.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.AMETHYST)
    ));
    public static final DeferredBlock<Block> SAPPHIRE_BLOCK = registerBlock("sapphire_block", () -> new Block(BlockBehaviour.Properties.of()
            .strength(1.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.AMETHYST)
    ));
    public static final DeferredBlock<Block> AMBER_BLOCK = registerBlock("amber_block", () -> new Block(BlockBehaviour.Properties.of()
            .strength(1.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.AMETHYST)
    ));
    public static final DeferredBlock<Block> LAVA_CRYSTAL_BLOCK = registerBlock("lava_crystal_block", () -> new Block(BlockBehaviour.Properties.of()
            .strength(2f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.GLASS)));
    public static final DeferredBlock<Block> ENDER_CRYSTAL_BLOCK = registerBlock("ender_crystal_block", () -> new Block(BlockBehaviour.Properties.of()
            .strength(2f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.GLASS)));

    // Ores
    public static final DeferredBlock<Block> JADE_ORE = registerBlock("jade_ore", () -> new DropExperienceBlock(UniformInt.of(1, 3), BlockBehaviour.Properties.of()
            .strength(3f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
    ));
    public static final DeferredBlock<Block> JADE_DEEPSLATE_ORE = registerBlock("jade_deepslate_ore", () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of()
            .strength(4.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
    ));
    public static final DeferredBlock<Block> SAPPHIRE_ORE = registerBlock("sapphire_ore", () -> new DropExperienceBlock(UniformInt.of(2, 3), BlockBehaviour.Properties.of()
            .strength(3.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
    ));
    public static final DeferredBlock<Block>  SAPPHIRE_DEEPSLATE_ORE = registerBlock("sapphire_deepslate_ore", () -> new DropExperienceBlock(UniformInt.of(3, 5), BlockBehaviour.Properties.of()
            .strength(5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
    ));
    public static final DeferredBlock<Block> AMBER_ORE = registerBlock("amber_ore", () -> new DropExperienceBlock(UniformInt.of(3, 5), BlockBehaviour.Properties.of()
            .strength(4f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
    ));
    public static final DeferredBlock<Block>  AMBER_DEEPSLATE_ORE = registerBlock("amber_deepslate_ore", () -> new DropExperienceBlock(UniformInt.of(4, 7), BlockBehaviour.Properties.of()
            .strength(5.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
    ));
    public static final DeferredBlock<Block> LAVA_CRYSTAL_ORE = registerBlock("lava_crystal_ore", () -> new DropExperienceBlock(UniformInt.of(3, 6), BlockBehaviour.Properties.of()
            .strength(1f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.NETHERRACK)
    ));
    public static final DeferredBlock<Block> ENDER_CRYSTAL_ORE = registerBlock("ender_crystal_ore", () -> new DropExperienceBlock(UniformInt.of(3, 6), BlockBehaviour.Properties.of()
            .strength(4.5f)
            .requiresCorrectToolForDrops()
    ));

    // Block Entities
    public static final DeferredBlock<Block> CRYSTAL_GROWER = registerBlock("crystal_grower", () ->
            new CrystalGrowerBlock(BlockBehaviour.Properties.of()
            .strength(2f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final DeferredBlock<Block> CRYSTALLIZER = registerBlock("crystallizer", () ->
            new CrystallizerBlock(BlockBehaviour.Properties.of()
                    .strength(3f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> blockSupplier)
    {
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        registerBlockItem(name, block);
        return block;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block)
    {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus bus)
    {
        BLOCKS.register(bus);
    }
}
