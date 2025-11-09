package io.lerpmcgerk.mysticalprisms.event;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import io.lerpmcgerk.mysticalprisms.block.entity.CrystalGrowerBlockEntity;
import io.lerpmcgerk.mysticalprisms.block.entity.ModBlockEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = MysticalPrisms.MODID)
public class ModBusEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.CRYSTAL_GROWER_BE.get(), CrystalGrowerBlockEntity::getIFluidTank);
    }
}
