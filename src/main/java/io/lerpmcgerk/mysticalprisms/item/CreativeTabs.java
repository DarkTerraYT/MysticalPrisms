package io.lerpmcgerk.mysticalprisms.item;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import io.lerpmcgerk.mysticalprisms.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MysticalPrisms.MODID);

    public static final Supplier<CreativeModeTab> CRYSTAL_RESOURCES = CREATIVE_MODE_TABS.register("crystal_resources", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.JADE.get()))
            .title(Component.translatable("creativetab.mysticalprisms.crystal_resources"))
            .displayItems((itemDisplayParameters, output) ->
            {
                output.accept(ModItems.JADE);
                output.accept(ModBlocks.JADE_BLOCK);
                output.accept(ModItems.SAPPHIRE);
                output.accept(ModBlocks.SAPPHIRE_BLOCK);
                output.accept(ModItems.LAVA_CRYSTAL);
                output.accept(ModBlocks.LAVA_CRYSTAL_BLOCK);
            }).build());

    public static final Supplier<CreativeModeTab> CRYSTAL_MISC = CREATIVE_MODE_TABS.register("crystal_misc", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModBlocks.JADE_ORE))
            .title(Component.translatable("creativetab.mysticalprisms.crystal_misc"))
            .displayItems(((itemDisplayParameters, output) ->
            {
                output.accept(ModBlocks.JADE_ORE);
                output.accept(ModBlocks.SAPPHIRE_ORE);
                output.accept(ModBlocks.JADE_DEEPSLATE_ORE);
                output.accept(ModBlocks.SAPPHIRE_DEEPSLATE_ORE);
            }))
            .build());

    public static final void register(IEventBus bus)
    {
        CREATIVE_MODE_TABS.register(bus);
    }
}
