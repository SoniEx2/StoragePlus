package com.github.soniex2.storageplus.event;

import com.github.soniex2.storageplus.StoragePlus;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventListener {

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (event.block == Blocks.chest) {
			TileEntity t = event.world.getTileEntity(event.x, event.y, event.z);
			if (t != null && t instanceof TileEntityChest) {
				TileEntityChest chest = (TileEntityChest) t;
				for (int i = 0; i < chest.getSizeInventory(); i++) {
					ItemStack is = chest.getStackInSlot(i);
					if (is == null
							|| is.stackSize < 64
							|| is.getItem() != Item
									.getItemFromBlock(Blocks.chest)) {
						return;
					}
				}
				ItemStack stack = new ItemStack(StoragePlus.items.chestception,
						1, 0);
				ChestceptionCraftEvent craftEvent = new ChestceptionCraftEvent(
						stack, event.world, event.x, event.y, event.z, chest);
				MinecraftForge.EVENT_BUS.post(craftEvent);
				if (craftEvent.isCanceled()) {
					return;
				}
				for (int i = 0; i < chest.getSizeInventory(); i++) {
					chest.setInventorySlotContents(i, null);
				}
				event.world.setTileEntity(event.x, event.y, event.z, null);
				event.world.setBlockToAir(event.x, event.y, event.z);
				EntityItem item = new EntityItem(event.world, event.x + 0.5,
						event.y + 0.5, event.z + 0.5, stack);
				item.forceSpawn = true;
				item.lifespan = Integer.MAX_VALUE;
				event.world.spawnEntityInWorld(item);
				event.setCanceled(true);
				event.setResult(Result.DENY);
			}
		}
	}
}
