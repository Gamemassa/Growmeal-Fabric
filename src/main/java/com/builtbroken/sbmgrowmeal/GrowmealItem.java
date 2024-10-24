package com.builtbroken.sbmgrowmeal;

import net.minecraft.block.BlockState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

/**
 * @see {@link net.minecraft.item.BoneMealItem BoneMealItem}
 * @see {@link net.minecraft.block.SaplingBlock SaplingBlock}
 * @see {@link net.minecraft.block.CropBlock CropBlock}
 */
public class GrowmealItem extends Item {
    public static final int MAX_TRIES = 1000;

    public GrowmealItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(context.getSide());

        boolean isGrown = false;

        for (int i = 0; i < MAX_TRIES; ++i) {
            if (!BoneMealItem.useOnFertilizable(context.getStack(), world, blockPos)) {
                break;
            }

            if (!world.isClient) {
                world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 15);
                isGrown = true;
            }
        }

        if (isGrown) {
            if (!world.isClient) {
                context.getPlayer().emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
            }
            return ActionResult.SUCCESS;
        }

        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = blockState.isSideSolidFullSquare(world, blockPos, context.getSide());

        if (bl && BoneMealItem.useOnGround(context.getStack(), world, blockPos2, context.getSide())) {
            if (!world.isClient) {
                context.getPlayer().emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
                world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos2, 15);
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
