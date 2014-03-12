package com.github.soniex2.storageplus.event;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class ChestceptionCraftEvent extends Event {

	public final ItemStack chestception;
	public final World world;
	public final int x, y, z;
	public final TileEntityChest chest;

	public ChestceptionCraftEvent(ItemStack chestception, World world, int x,
			int y, int z, TileEntityChest chest) {
		super();
		this.chestception = chestception;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.chest = chest;
	}
}
