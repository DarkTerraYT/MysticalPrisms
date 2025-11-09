package io.lerpmcgerk.mysticalprisms.item;

import io.lerpmcgerk.mysticalprisms.MysticalPrisms;
import io.lerpmcgerk.mysticalprisms.block.ModBlocks;
import io.lerpmcgerk.mysticalprisms.item.custom.FuelItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MysticalPrisms.MODID);

    public static final DeferredItem<Item> JADE = ITEMS.register("jade", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SAPPHIRE = ITEMS.register("sapphire", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LAVA_CRYSTAL = ITEMS.register("lava_crystal", () -> new FuelItem(6400, new Item.Properties()));
    public static final DeferredItem<Item> AMBER = ITEMS.register("amber", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENDER_CRYSTAL = ITEMS.register("ender_crystal", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CRYSTALLIZED_GOLD = ITEMS.register("crystallized_gold", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> UPGRADE_BASE = registerTooltipItem("upgrade_base", new Item.Properties(), 1);
    public static final DeferredItem<Item> SPEED_UPGRADE = registerTooltipItem("speed_upgrade", new Item.Properties(), 1);

    private static DeferredItem<Item> registerTooltipItem(String name, Item.Properties properties, int tooltips)
    {
        DeferredItem<Item> item = ITEMS.register(name, () -> new Item(properties) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                for(int i = 0; i < tooltips; i++)
                {
                    tooltipComponents.add(Component.translatable("mysticalprisms.tooltip." + name + "." + i));
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        });

        return item;
    }

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
    }
}
