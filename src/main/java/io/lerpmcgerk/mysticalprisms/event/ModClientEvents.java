package io.lerpmcgerk.mysticalprisms.event;

import io.lerpmcgerk.mysticalprisms.screen.ModMenuTypes;
import io.lerpmcgerk.mysticalprisms.screen.custom.CrystalGrowerScreen;
import io.lerpmcgerk.mysticalprisms.screen.custom.CrystallizerScreen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;

// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = MysticalPrisms.MODID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void onClientSetup(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.CRYSTAL_GROWER_MENU.get(), CrystalGrowerScreen::new);
        event.register(ModMenuTypes.CRYSTALLIZER_MENU.get(), CrystallizerScreen::new);
    }
}