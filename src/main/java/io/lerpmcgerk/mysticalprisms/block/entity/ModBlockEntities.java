package io.lerpmcgerk.mysticalprisms.block.entity;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import io.lerpmcgerk.mysticalprisms.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MysticalPrisms.MODID);

    public static final Supplier<BlockEntityType<CrystalGrowerBlockEntity>> CRYSTAL_GROWER_BE = BLOCK_ENTITIES.register("crystal_grower_be", () -> BlockEntityType.Builder.of(CrystalGrowerBlockEntity::new, ModBlocks.CRYSTAL_GROWER.get()).build(null));
}
