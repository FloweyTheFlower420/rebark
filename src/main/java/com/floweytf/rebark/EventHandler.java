package com.floweytf.rebark;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {
    @SubscribeEvent
    public static void onFurnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
        if(event.getItemStack().getItem() == RebarkMain.BARK.get())
            event.setBurnTime(100);
        Fluid
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (BarkItem.UNSTRIP == null) {
            BarkItem.UNSTRIP = AxeItem.STRIPABLES.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        }
    }

    @SubscribeEvent
    public static void onToolUse(BlockEvent.BlockToolInteractEvent event) {
        ItemStack heldItem = event.getHeldItemStack();
        PlayerEntity player = event.getPlayer();
        // no fucking clue olTypes(heldItem).contains(ToolType.AXE) &&
            AxeItem.STRIPABLES.containsKey(event.getState().getBlock())) {
            World world = event.getPlayer().level;
            ItemStack itemStack = new ItemStack(RebarkMain.BARK.get());
            BlockPos pos = rayTrace(world, player, event.getPos());

            world.addFreshEntity(new ItemEntity(
                world,
                pos.getX() + .5f,
                pos.getY() + .3f,
                pos.getZ() + .5f,
                itemStack
            ));
        }
    }

    private static BlockPos rayTrace(World world, PlayerEntity player, BlockPos pos) {
        Vector3d eyePos = player.getEyePosition(1);
        Vector3d lookPos = player.getViewVector(1);
        double length = player.getAttributeValue(ForgeMod.REACH_DISTANCE.get()) + 1;
        Vector3d endPos = eyePos.add(lookPos.x * length, lookPos.y * length, lookPos.z * length);
        RayTraceContext context = new RayTraceContext(eyePos, endPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player);
        BlockRayTraceResult result = world.clip(context);
        Direction side = result.getDirection();
        return pos.relative(side);
    }
}