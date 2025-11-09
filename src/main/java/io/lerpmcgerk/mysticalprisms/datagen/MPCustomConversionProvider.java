package io.lerpmcgerk.mysticalprisms.datagen;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import moze_intel.projecte.api.data.CustomConversionProvider;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class MPCustomConversionProvider extends CustomConversionProvider {
    protected MPCustomConversionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MysticalPrisms.MODID);
    }

    private static NormalizedSimpleStack getItem(String itemId)
    {
        return NSSItem.createItem(ResourceLocation.fromNamespaceAndPath(MysticalPrisms.MODID, itemId));
    }

    @Override
    protected void addCustomConversions(HolderLookup.Provider provider) {
        createConversionBuilder(ResourceLocation.fromNamespaceAndPath(MysticalPrisms.MODID, "emc_crystals"))
                .before(getItem("jade"), 100)
                .before(getItem("sapphire"), 200)
                .before(getItem("amber"), 400)
                .before(getItem("lava_crystal"), 408);
    }
}
