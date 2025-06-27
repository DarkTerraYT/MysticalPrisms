package io.lerpmcgerk.mysticalprisms.block.entity;

import io.lerpmcgerk.mysticalprisms.item.ModItems;
import io.lerpmcgerk.mysticalprisms.recipe.CrystalGrowerRecipe;
import io.lerpmcgerk.mysticalprisms.recipe.CrystalGrowerRecipeInput;
import io.lerpmcgerk.mysticalprisms.recipe.ModRecipes;
import io.lerpmcgerk.mysticalprisms.screen.custom.CrystalGrowerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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

    private FluidTank createFluidTank() {
        return new FluidTank(64000) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if (!level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return true;
            }
        };
    }

    private final FluidTank FLUID_TANK = createFluidTank();

    protected final ContainerData data;

    private int progress;
    private int maxProgress = 200;

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
    public @NotNull Component getDisplayName() {
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

        tag = FLUID_TANK.writeToNBT(registries, tag);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("crystal_grower.progress");
        maxProgress = tag.getInt("crystal_grower.max_progress");

        FLUID_TANK.readFromNBT(registries, tag);
    }

    public void tick(Level level, BlockPos pos, BlockState state)
    {
        checkFluidSlots();

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

    // Fluid Slots
    private void checkFluidSlots()
    {
        if (hasFluidHandlerInSlot(LIQUID_IN_SLOT) && canInsertAmountIntoSlot(1, LIQUID_OUT_SLOT)) {
            if(hasEmptyFluidHandlerInSlot(LIQUID_IN_SLOT))
            {
                transferFluidFromTankToHandler();
            }
            else
            {
                transferFluidToTank();
            }

            if(canInsertItemIntoSlot(itemHandler.getStackInSlot(LIQUID_IN_SLOT), LIQUID_OUT_SLOT))
            {
                itemHandler.setStackInSlot(LIQUID_OUT_SLOT, itemHandler.getStackInSlot(LIQUID_IN_SLOT));
                itemHandler.setStackInSlot(LIQUID_IN_SLOT, ItemStack.EMPTY);
            }
        }
    }

    private void transferFluidToTank() {
        FluidActionResult result = FluidUtil.tryEmptyContainer(itemHandler.getStackInSlot(LIQUID_IN_SLOT), this.FLUID_TANK, Integer.MAX_VALUE, null, true);
        if(result.result != ItemStack.EMPTY) {
            itemHandler.setStackInSlot(0, result.result);
        }
    }

    private void transferFluidFromTankToHandler() {
        FluidActionResult result = FluidUtil.tryFillContainer(itemHandler.getStackInSlot(LIQUID_IN_SLOT), this.FLUID_TANK, Integer.MAX_VALUE, null, true);
        if(result.result != ItemStack.EMPTY) {
            itemHandler.setStackInSlot(LIQUID_OUT_SLOT, result.result);
        }
    }

    private boolean hasFluidHandlerInSlot(int slot) {
        return !itemHandler.getStackInSlot(slot).isEmpty()
                && itemHandler.getStackInSlot(slot).getCapability(Capabilities.FluidHandler.ITEM, null) != null;
    }

    private boolean hasEmptyFluidHandlerInSlot(int slot)
    {        return !itemHandler.getStackInSlot(slot).isEmpty()
            && itemHandler.getStackInSlot(slot).getCapability(Capabilities.FluidHandler.ITEM, null) != null
            && !itemHandler.getStackInSlot(slot).getCapability(Capabilities.FluidHandler.ITEM, null).getFluidInTank(slot).isEmpty();
    }

    public FluidStack getFluid() {
        return FLUID_TANK.getFluid();
    }

    // Recipe stuff
    private void craftItem()
    {
        Optional<RecipeHolder<CrystalGrowerRecipe>> recipeOptional = getCurrentRecipe();

        if(recipeOptional.isEmpty())
        {
            return;
        }
        CrystalGrowerRecipe recipe = recipeOptional.get().value();
        ItemStack output = recipe.output();

        itemHandler.extractItem(INPUT_SLOT_1, 1, false);
        if(!recipe.getIngredients().get(1).test(ItemStack.EMPTY))
        {
            itemHandler.extractItem(INPUT_SLOT_2, 1, false);
        }
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

    public boolean hasRecipe()
    {
        Optional<RecipeHolder<CrystalGrowerRecipe>> recipeOptional = getCurrentRecipe();

        if(recipeOptional.isEmpty())
        {
            return false;
        }

        CrystalGrowerRecipe recipe = recipeOptional.get().value();

        ItemStack output = recipe.output();
        return canInsertAmountIntoSlot(output.getCount(), OUTPUT_SLOT) &&
                canInsertItemIntoSlot(output, OUTPUT_SLOT);
    }

    private Optional<RecipeHolder<CrystalGrowerRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.CRYSTAL_GROWER_TYPE.get(), new CrystalGrowerRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT_1), itemHandler.getStackInSlot(INPUT_SLOT_2)), level);
    }

    private boolean canInsertItemIntoSlot(ItemStack output, int slot)
    {
        return itemHandler.getStackInSlot(slot).getItem() == output.getItem() || itemHandler.getStackInSlot(slot).isEmpty();
    }

    private boolean canInsertAmountIntoSlot(int count, int slot)
    {
        int maxStack = itemHandler.getStackInSlot(slot).isEmpty() ? 64 : itemHandler.getStackInSlot(slot).getMaxStackSize();
        int currentStack = itemHandler.getStackInSlot(slot).getCount();

        return currentStack + count <= maxStack;
    }
    public boolean hasRequiredEnergy()
    {
        return true;
    }

    private boolean hasCraftingFinished()
    {
        return progress >= maxProgress;
    }

    public int getProgress()
    {
        return progress;
    }
    public int getMaxProgress()
    {
        return maxProgress;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return super.getUpdatePacket();
    }

    public IFluidHandler getTank(@Nullable Direction direction)
    {
        return FLUID_TANK;
    }
}
