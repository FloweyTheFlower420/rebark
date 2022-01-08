package com.floweytf.rebark;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class BarkItem extends Item {
    public static Map<Block, Block> UNSTRIP = null;

    public BarkItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = world.getBlockState(blockpos);

        Block block = blockstate.getBlock();
        if(!UNSTRIP.containsKey(block))
            return ActionResultType.PASS;
        Block output = UNSTRIP.get(block);
        if(!DataReloadListener.getInstance().testRebark(output))
            return ActionResultType.FAIL;
        BlockState outputState = output.defaultBlockState().setValue(
            RotatedPillarBlock.AXIS,
            blockstate.getValue(RotatedPillarBlock.AXIS)
        );

        PlayerEntity playerEntity = context.getPlayer();

        world.playSound(playerEntity, blockpos, SoundEvents.AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (!world.isClientSide) {
            world.setBlock(blockpos, outputState, 11);
        }

        int count = playerEntity.getItemInHand(context.getHand()).getCount();
        playerEntity.getItemInHand(context.getHand()).setCount(count - 1);

        return ActionResultType.sidedSuccess(world.isClientSide);
    }
}
