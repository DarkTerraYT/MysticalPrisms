package io.lerpmcgerk.mysticalprisms.block.custom;

import com.mojang.serialization.MapCodec;
import io.lerpmcgerk.mysticalprisms.block.entity.CrystalGrowerBlockEntity;
import io.lerpmcgerk.mysticalprisms.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CrystalGrowerBlock extends BaseEntityBlock {
    public static final MapCodec<CrystalGrowerBlock> CODEC = simpleCodec(CrystalGrowerBlock::new);

    public CrystalGrowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CrystalGrowerBlockEntity(blockPos, blockState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CrystalGrowerBlockEntity crystalGrowerBlockEntity) {
                crystalGrowerBlockEntity.drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!level.isClientSide())
        {
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof CrystalGrowerBlockEntity crystalGrowerBlockEntity) {
                ((ServerPlayer)player).openMenu(new SimpleMenuProvider(crystalGrowerBlockEntity, Component.literal("Crystal Grower")), pos);
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }



    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide())
        {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.CRYSTAL_GROWER_BE.get(), (level, blockPos, blockState, blockEntity) -> blockEntity.tick(level, blockPos, blockState));
    }
}
