package io.lerpmcgerk.mysticalprisms.block.entity;

import io.lerpmcgerk.mysticalprisms.item.ModItems;
import io.lerpmcgerk.mysticalprisms.screen.custom.CrystalGrowerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class CrystalGrowerBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide())
            {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int LIQUID_IN_SLOT = 0;
    private static final int LIQUID_OUT_SLOT = 1;
    private static final int INPUT_SLOT_1 = 2;
    private static final int INPUT_SLOT_2 = 3;
    private static final int OUTPUT_SLOT = 4;

    private static final int energy = 0;

    protected final ContainerData data;

    private int progress;
    private int maxProgress = 100;

    public CrystalGrowerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CRYSTAL_GROWER_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i)
                {
                    case 0 -> CrystalGrowerBlockEntity.this.progress;
                    case 1 -> CrystalGrowerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i)
                {
                    case 0:
                        CrystalGrowerBlockEntity.this.progress = value;
                        break;
                    case 1:
                        CrystalGrowerBlockEntity.this.maxProgress = value;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.mysticalprisms.crystal_grower");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CrystalGrowerMenu(i, inventory, this, this.data);
    }

    public void drops()
    {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++)
        {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("crystal_grower.progress", progress);
        tag.putInt("crystal_grower.max_progress", maxProgress);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("crystal_grower.progress");
        maxProgress = tag.getInt("crystal_grower.max_progress");
    }

    public void tick(Level level, BlockPos pos, BlockState state)
    {
        if(hasRecipe() && hasRequiredEnergy())
        {
            increaseCraftingProgress();
            setChanged(level, pos, state);

            if(hasCraftingFinished())
            {
                craftItem();
                resetProgress();
            }
        }
        else
        {
            resetProgress();
        }
    }

    private void craftItem()
    {
        ItemStack output = new ItemStack(ModItems.JADE.get(), 2);

        itemHandler.extractItem(INPUT_SLOT_1, 1, false);
        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(), itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount()));
    }

    private void resetProgress()
    {
        progress = 0;
        maxProgress = 100;
    }

    private void increaseCraftingProgress()
    {
        progress++;
    }


    private boolean hasRecipe()
    {
        ItemStack output = new ItemStack(ModItems.JADE.get(), 2);
        return itemHandler.getStackInSlot(INPUT_SLOT_1).is(ModItems.SAPPHIRE) &&
                canInsertAmountIntoOutputSlot(output.getCount()) &&
                canInsertItemIntoOutputSlot(output);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem() || itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxStack = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentStack = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

        return currentStack + count <= maxStack;
    }
    private boolean hasRequiredEnergy()
    {
        return true;
    }

    private boolean hasCraftingFinished()
    {
        return progress >= maxProgress;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return super.getUpdatePacket();
    }
}
