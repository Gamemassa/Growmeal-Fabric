package com.builtbroken.sbmgrowmeal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class GrowmealItem
extends Item {

    public GrowmealItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(context.getSide());
        boolean isGrown = false;
        for(int i = 0; i < 1000; i++) {
            if (GrowmealItem.useOnFertilizable(context.getStack(), world, blockPos)) {
                if (!world.isClient) {
                    world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 0);
                    isGrown = true;
                }
            } else {
                break;
            }
        }
        if(isGrown) return ActionResult.success(world.isClient);
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = blockState.isSideSolidFullSquare(world, blockPos, context.getSide());
        if (bl && GrowmealItem.useOnGround(context.getStack(), world, blockPos2, context.getSide())) {
            if (!world.isClient) {
                world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos2, 0);
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    public static boolean useOnFertilizable(ItemStack stack, World world, BlockPos pos) {
        Fertilizable fertilizable;
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof Fertilizable && (fertilizable = (Fertilizable)(blockState.getBlock())).isFertilizable(world, pos, blockState, world.isClient)) {
            for(int i = 0;i < 1000; i++) {
                if (world instanceof ServerWorld) {
                    if (fertilizable.canGrow(world, world.random, pos, blockState)) {
                        fertilizable.grow((ServerWorld) world, world.random, pos, blockState);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static boolean useOnGround(ItemStack stack, World world, BlockPos blockPos, @Nullable Direction facing) {
        if (!world.getBlockState(blockPos).isOf(Blocks.WATER) || world.getFluidState(blockPos).getLevel() != 8) {
            return false;
        }
        if (!(world instanceof ServerWorld)) {
            return true;
        }
        Random random = world.getRandom();
        block0: for (int i = 0; i < 128; ++i) {
            BlockPos blockPos2 = blockPos;
            BlockState blockState = Blocks.SEAGRASS.getDefaultState();
            for (int j = 0; j < i / 16; ++j) {
                if (world.getBlockState(blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1)).isFullCube(world, blockPos2)) continue block0;
            }
            RegistryEntry<Biome> registryEntry = world.getBiome(blockPos2);
            if (registryEntry.isIn(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL)) {
                if (i == 0 && facing != null && facing.getAxis().isHorizontal()) {
                    blockState = Registries.BLOCK.getEntryList(BlockTags.WALL_CORALS).flatMap(blocks -> blocks.getRandom(world.random)).map(blockEntry -> ((Block)blockEntry.value()).getDefaultState()).orElse(blockState);
                    if (blockState.contains(DeadCoralWallFanBlock.FACING)) {
                        blockState = (BlockState)blockState.with(DeadCoralWallFanBlock.FACING, facing);
                    }
                } else if (random.nextInt(4) == 0) {
                    blockState = Registries.BLOCK.getEntryList(BlockTags.UNDERWATER_BONEMEALS).flatMap(blocks -> blocks.getRandom(world.random)).map(blockEntry -> ((Block)blockEntry.value()).getDefaultState()).orElse(blockState);
                }
            }
            if (blockState.isIn(BlockTags.WALL_CORALS, state -> state.contains(DeadCoralWallFanBlock.FACING))) {
                for (int k = 0; !blockState.canPlaceAt(world, blockPos2) && k < 4; ++k) {
                    blockState = (BlockState)blockState.with(DeadCoralWallFanBlock.FACING, Direction.Type.HORIZONTAL.random(random));
                }
            }
            if (!blockState.canPlaceAt(world, blockPos2)) continue;
            BlockState blockState2 = world.getBlockState(blockPos2);
            if (blockState2.isOf(Blocks.WATER) && world.getFluidState(blockPos2).getLevel() == 8) {
                world.setBlockState(blockPos2, blockState, Block.NOTIFY_ALL);
                continue;
            }
            if (!blockState2.isOf(Blocks.SEAGRASS) || random.nextInt(10) != 0) continue;
            ((Fertilizable)((Object)Blocks.SEAGRASS)).grow((ServerWorld)world, random, blockPos2, blockState2);
        }
        stack.decrement(1);
        return true;
    }

}

