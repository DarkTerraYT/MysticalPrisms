package io.lerpmcgerk.mysticalprisms.datagen;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import io.lerpmcgerk.mysticalprisms.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MysticalPrisms.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.JADE.get());
        basicItem(ModItems.SAPPHIRE.get());
        basicItem(ModItems.AMBER.get());
        basicItem(ModItems.LAVA_CRYSTAL.get());
    }
}
