package io.lerpmcgerk.mysticalprisms.block.entity;

import io.lerpmcgerk.mysticalprisms.block.entity.custom.UpgradableBlockEntity;
import io.lerpmcgerk.mysticalprisms.item.custom.IUpgradeItem;
import io.lerpmcgerk.mysticalprisms.recipe.CrystalGrowerRecipe;
import io.lerpmcgerk.mysticalprisms.recipe.CrystalGrowerRecipeInput;
import io.lerpmcgerk.mysticalprisms.recipe.ModRecipes;
import io.lerpmcgerk.mysticalprisms.screen.custom.CrystalGrowerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CrystalGrowerBlockEntity extends UpgradableBlockEntity implements MenuProvider {

    public final ItemStackHandler itemHandler = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide())
            {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final ItemStackHandler upgrades = new ItemStackHandler(7)
    {
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
    private static final int LIQUID_HANDLER_OUT_SLOT = 1;
    private static final int LIQUID_OUTPUT_SLOT = 2;
    private static final int INPUT_SLOT_1 = 3;
    private static final int INPUT_SLOT_2 = 4;
    private static final int OUTPUT_SLOT = 5;

    protected final ContainerData data;

    private final FluidTank fluidTank = new FluidTank(4000)
    {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            CrystalGrowerBlockEntity.this.sendUpdate();
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

    public IFluidHandler  getIFluidTank(Direction direction)
    {
        return fluidTank;
    }


    public FluidTank getFluidTank() {
        return fluidTank;
    }

    private int progress;
    private int maxProgress = 200;

    public int getModifiedMaxProgress()
    {
        return maxProgress;
    }

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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("crystal_grower.progress", progress);
        tag.putInt("crystal_grower.max_progress", maxProgress);
        tag.put("fluid_tank", this.fluidTank.writeToNBT(registries, new CompoundTag()));
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("crystal_grower.progress");
        maxProgress = tag.getInt("crystal_grower.max_progress");
        this.fluidTank.readFromNBT(registries, tag.getCompound("fluid_tank"));
    }

    public void tick(Level level, BlockPos pos, BlockState state)
    {
        if(hasRecipe() && hasEnoughLiquid())
        {
            maxProgress = getCurrentRecipe().get().value().time();
            increaseCraftingProgress();
            setChanged(level, pos, state);

            if(hasCraftingFinished())
            {
                drainForRecipe();
                craftItem();
                resetProgress();
            }
        }
        else
        {
            resetProgress();
        }

        manageLiquidSlots();
    }

    public int getFluidAmount()
    {
        return this.fluidTank.getFluidAmount();
    }

    public int getCapacity()
    {
        return this.fluidTank.getCapacity();
    }

    private void manageLiquidInSlot()
    {
        ItemStack stack = this.itemHandler.getStackInSlot(LIQUID_IN_SLOT);
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

        int capacity = getCapacity();
        int fluidStackAmount = getFluidAmount();
        int amountToDrain = capacity - fluidStackAmount;
        int amount = fluidHandlerItem.drain(amountToDrain, IFluidHandler.FluidAction.SIMULATE).getAmount();
        if (amount > 0) {

            ItemStack slot = this.itemHandler.getStackInSlot(LIQUID_HANDLER_OUT_SLOT);
            if(slot.isEmpty() || slot.getCount() < slot.getMaxStackSize() && slot.is(fluidHandlerItem.getContainer().getItem())) {
                this.fluidTank.fill(fluidHandlerItem.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                this.itemHandler.setStackInSlot(LIQUID_IN_SLOT, ItemStack.EMPTY);

                if(slot.isEmpty())
                {
                    this.itemHandler.setStackInSlot(LIQUID_HANDLER_OUT_SLOT, fluidHandlerItem.getContainer());
                    return;
                }
                this.itemHandler.getStackInSlot(LIQUID_HANDLER_OUT_SLOT).grow(1);
            }
        }
    }
    private void manageLiquidOutSlot()
    {
        ItemStack stack = this.itemHandler.getStackInSlot(LIQUID_OUTPUT_SLOT);
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

            ItemStack slot = this.itemHandler.getStackInSlot(LIQUID_HANDLER_OUT_SLOT);
            if(slot.isEmpty() || slot.getCount() < slot.getMaxStackSize() && slot.is(fluidHandlerItem.getContainer().getItem())) {
                fluidHandlerItem.fill(fluidTank.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                this.itemHandler.setStackInSlot(LIQUID_OUTPUT_SLOT, ItemStack.EMPTY);

                if(slot.isEmpty())
                {
                    this.itemHandler.setStackInSlot(LIQUID_HANDLER_OUT_SLOT, fluidHandlerItem.getContainer());
                    return;
                }
                this.itemHandler.getStackInSlot(LIQUID_HANDLER_OUT_SLOT).grow(1);
            }
        }
    }

    private void manageLiquidSlots() {
       manageLiquidInSlot();
       manageLiquidOutSlot();
    }

    private void drainForRecipe()
    {
        if(!hasRecipe() || getCurrentRecipe().get().value().getFluidIngredient().test(FluidStack.EMPTY))
        {
            return;
        }

        CrystalGrowerRecipe recipe = getCurrentRecipe().get().value();
        int amountToDrain = recipe.getFluidIngredient().amount();

        fluidTank.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE);
    }

    private boolean hasEnoughLiquid()
    {
        if(!getCurrentRecipe().get().value().getFluidIngredient().test(FluidStack.EMPTY))
        {
            return true;
        }

        CrystalGrowerRecipe recipe = getCurrentRecipe().get().value();
        SizedFluidIngredient ingredient = recipe.getFluidIngredient();
        return ingredient.test(fluidTank.getFluid());
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
                .getRecipeFor(ModRecipes.CRYSTAL_GROWER_TYPE.get(), new CrystalGrowerRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT_1), itemHandler.getStackInSlot(INPUT_SLOT_2), fluidTank.getFluid()), level);
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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public ItemStackHandler getUpgradeInventory() {
        return upgrades;
    }

    @Override
    public BlockEntityTicker<CrystalGrowerBlockEntity> getTicker() {
        return getBlockState().getTicker(level, ModBlockEntities.CRYSTAL_GROWER_BE.get());
    }
}
