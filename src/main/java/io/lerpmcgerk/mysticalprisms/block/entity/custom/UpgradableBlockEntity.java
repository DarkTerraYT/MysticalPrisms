package io.lerpmcgerk.mysticalprisms.block.entity.custom;

import io.lerpmcgerk.mysticalprisms.item.custom.IUpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class UpgradableBlockEntity extends BlockEntity {
    public UpgradableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public boolean tryAddUpgrade()
    {
        return false;
    }

    protected void addUpgrade()
    {
    }

    public abstract ItemStackHandler getUpgradeInventory();

    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker();
}
