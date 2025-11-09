package io.lerpmcgerk.mysticalprisms.block.entity;

import io.lerpmcgerk.mysticalprisms.block.entity.custom.UpgradableBlockEntity;
import io.lerpmcgerk.mysticalprisms.block.entity.energy.ModEnergyStorage;
import io.lerpmcgerk.mysticalprisms.recipe.*;
import io.lerpmcgerk.mysticalprisms.screen.custom.CrystalGrowerMenu;
import io.lerpmcgerk.mysticalprisms.screen.custom.CrystallizerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CrystallizerBlockEntity extends BlockEntity implements MenuProvider  {

    private final FluidTank fluidTank = createFluidTank();
    protected final ContainerData data;

    private FluidTank createFluidTank() {
        return new FluidTank(8000) {
            @Override
            protected void onContentsChanged() {
                sendUpdate();
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return true;
            }

            @Override
            public int getCapacity() {
                return capacity;
            }

            @Override
            public int getFluidAmount() {
                return fluid.getAmount();
            }
        };
    }

    public FluidTank getFluidTank()
    {
        return fluidTank;
    }

    private final ModEnergyStorage battery = createEnergyStorage();

    private ModEnergyStorage createEnergyStorage() {
        return new ModEnergyStorage(32000, 1000)
        {
            @Override
            public void onEnergyChanged() {
                sendUpdate();
            }
        };
    }

    public ModEnergyStorage getBattery()
    {
        return battery;
    }


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

    public CrystallizerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CRYSTALLIZER_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i)
                {
                    case 0 -> CrystallizerBlockEntity.this.progress;
                    case 1 -> CrystallizerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i)
                {
                    case 0:
                        CrystallizerBlockEntity.this.progress = value;
                        break;
                    case 1:
                        CrystallizerBlockEntity.this.maxProgress = value;
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

    private static final int FLUID_INPUT_SLOT = 0;
    private static final int FLUID_HANDLER_OUT_SLOT = 1;
    private static final int FLUID_OUTPUT_SLOT = 2;
    private static final int ITEM_INPUT_SLOT = 3;
    private static final int OUTPUT_SLOT = 4;

    private void sendUpdate()
    {
        setChanged();

        if(this.level != null)
        {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
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
    
    
    public void tick(Level level, BlockPos pos, BlockState state)
    {
        if(hasRecipe() && hasEnoughLiquid())
        {
            if(hasRequiredEnergy()) {
                maxProgress = getCurrentRecipe().get().value().time();
                increaseCraftingProgress();
                decreaseEnergy();
                setChanged(level, pos, state);

                if (hasCraftingFinished()) {
                    drainForRecipe();
                    craftItem();
                    resetProgress();
                }
            }
        }
        else
        {
            resetProgress();
        }

        manageLiquidSlots();
    }

    private void decreaseEnergy() {
        battery.extractEnergy(getCurrentRecipe().get().value().energy(), false);
    }

    private void manageLiquidInSlot()
    {
        ItemStack stack = this.itemHandler.getStackInSlot(FLUID_INPUT_SLOT);
        if (stack.isEmpty()) {
            return;
        }

        Optional<IFluidHandlerItem> fluidHandler = Optional.ofNullable(stack.getCapability(Capabilities.FluidHandler.ITEM, null));
        if (fluidHandler.isEmpty()) {
            return;
        }
        IFluidHandlerItem fluidHandlerItem = fluidHandler.get();

        if(!fluidTank.getFluid().isEmpty() && !fluidHandlerItem.getFluidInTank(0).is(fluidTank.getFluid().getFluid()))
        {
            return;
        }

        int capacity = fluidTank.getCapacity();
        int fluidStackAmount = fluidTank.getFluidAmount();
        int amountToDrain = capacity - fluidStackAmount;
        int amount = fluidHandlerItem.drain(amountToDrain, IFluidHandler.FluidAction.SIMULATE).getAmount();
        if (amount > 0) {

            ItemStack slot = this.itemHandler.getStackInSlot(FLUID_HANDLER_OUT_SLOT);
            if(slot.isEmpty() || slot.getCount() < slot.getMaxStackSize() && slot.is(fluidHandlerItem.getContainer().getItem())) {
                this.fluidTank.fill(fluidHandlerItem.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                this.itemHandler.setStackInSlot(FLUID_INPUT_SLOT, ItemStack.EMPTY);

                if(slot.isEmpty())
                {
                    this.itemHandler.setStackInSlot(FLUID_OUTPUT_SLOT, fluidHandlerItem.getContainer());
                    return;
                }
                this.itemHandler.getStackInSlot(FLUID_HANDLER_OUT_SLOT).grow(1);
            }
        }
    }
    private void manageLiquidOutSlot()
    {
        ItemStack stack = this.itemHandler.getStackInSlot(FLUID_OUTPUT_SLOT);
        if (stack.isEmpty()) {
            return;
        }

        Optional<IFluidHandlerItem> fluidHandler = Optional.ofNullable(stack.getCapability(Capabilities.FluidHandler.ITEM, null));
        if (fluidHandler.isEmpty()) {
            return;
        }
        IFluidHandlerItem fluidHandlerItem = fluidHandler.get();

        if(fluidTank.getFluid().isEmpty() && !fluidHandlerItem.getFluidInTank(0).is(fluidTank.getFluid().getFluid()))
        {
            return;
        }

        int capacity = fluidHandlerItem.getTankCapacity(0);
        int fluidStackAmount = fluidHandlerItem.getFluidInTank(0).getAmount();
        int amountToDrain = capacity - fluidStackAmount;
        int amount = fluidTank.drain(amountToDrain, IFluidHandler.FluidAction.SIMULATE).getAmount();
        if (amount > 0) {

            ItemStack slot = this.itemHandler.getStackInSlot(FLUID_HANDLER_OUT_SLOT);
            if(slot.isEmpty() || slot.getCount() < slot.getMaxStackSize() && slot.is(fluidHandlerItem.getContainer().getItem())) {
                fluidHandlerItem.fill(fluidTank.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                this.itemHandler.setStackInSlot(FLUID_OUTPUT_SLOT, ItemStack.EMPTY);

                if(slot.isEmpty())
                {
                    this.itemHandler.setStackInSlot(FLUID_HANDLER_OUT_SLOT, fluidHandlerItem.getContainer());
                    return;
                }
                this.itemHandler.getStackInSlot(FLUID_HANDLER_OUT_SLOT).grow(1);
            }
        }
    }

    private void manageLiquidSlots() {
        manageLiquidInSlot();
        manageLiquidOutSlot();
    }
    private void resetProgress() {
        maxProgress = 300;
        progress = 0;
    }

    private void craftItem() {
        Optional<RecipeHolder<CrystallizerRecipe>> recipeOptional = getCurrentRecipe();

        if(recipeOptional.isEmpty())
        {
            return;
        }
        CrystallizerRecipe recipe = recipeOptional.get().value();
        ItemStack output = recipe.output();

        itemHandler.extractItem(ITEM_INPUT_SLOT, 1, false);
        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(), itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount()));
    }

    private void drainForRecipe() {
        if (!hasRecipe() || getCurrentRecipe().get().value().getFluidIngredient().test(FluidStack.EMPTY)) {
            return;
        }

        CrystallizerRecipe recipe = getCurrentRecipe().get().value();
        int amountToDrain = recipe.getFluidIngredient().amount();

        fluidTank.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE);
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private Optional<RecipeHolder<CrystallizerRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.CRYSTALLIZER_TYPE.get(), new CrystallizerRecipeInput(itemHandler.getStackInSlot(ITEM_INPUT_SLOT), fluidTank.getFluid()), level);
    }

    private boolean hasEnoughLiquid() {
        if(!getCurrentRecipe().get().value().getFluidIngredient().test(FluidStack.EMPTY))
    {
        return true;
    }

        CrystallizerRecipe recipe = getCurrentRecipe().get().value();
        SizedFluidIngredient ingredient = recipe.getFluidIngredient();
        return ingredient.test(fluidTank.getFluid());
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<CrystallizerRecipe>> recipeOptional = getCurrentRecipe();

        if(recipeOptional.isEmpty())
        {
            return false;
        }

        CrystallizerRecipe recipe = recipeOptional.get().value();

        ItemStack output = recipe.output();
        return canInsertAmountIntoSlot(output.getCount(), OUTPUT_SLOT) &&
                canInsertItemIntoSlot(output, OUTPUT_SLOT);
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

    private boolean hasRequiredEnergy() {
        return battery.extractEnergy(getCurrentRecipe().get().value().energy(), true) == getCurrentRecipe().get().value().energy();
    }

    int progress;
    int maxProgress;

    public int getProgress()
    {
        return progress;
    }
    public int getMaxProgress()
    {
        return maxProgress;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("crystallizer.progress", progress);
        tag.putInt("crystallizer.max_progress", maxProgress);

        tag.putInt("crystallizer.energy", battery.getEnergyStored());
        tag = fluidTank.writeToNBT(registries, tag);

        super.saveAdditional(tag, registries);
    }
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("crystallizer.progress");
        maxProgress = tag.getInt("crystallizer.max_progress");

        battery.setEnergy(tag.getInt("crystallizer.energy"));
        fluidTank.readFromNBT(registries, tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

   @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    /*@Override
    public ItemStackHandler getUpgradeInventory() {
        return upgrades;
    }

    @Override
    public BlockEntityTicker<CrystallizerBlockEntity> getTicker() {
        return getBlockState().getTicker(level, ModBlockEntities.CRYSTALLIZER_BE.get());
    }*/

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.mysticalprisms.crystallizer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CrystallizerMenu(containerId, playerInventory, this, this.data);
    }
}
