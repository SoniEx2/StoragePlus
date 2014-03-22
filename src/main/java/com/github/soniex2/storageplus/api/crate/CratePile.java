package com.github.soniex2.storageplus.api.crate;

import net.minecraft.item.ItemStack;

public class CratePile {

	public CratePile() {

	}

	public ItemStack getRandomStack() {
		// TODO implement this
		// Use int i = Math.min(stack.getMaxStackSize(),
		// something.getItemCount()) or something, then add i to a counter to
		// keep track of how many stacks you can still give out, while still
		// being able to display items on the GUI. then use crazy shit to suck
		// items back or something.
		return null;
	}

	public void add(ICrate crate) {
		// TODO Auto-generated method stub

	}

	public boolean isFull() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Gets a buffer for a crate. This doesn't remove items from the CratePile.
	 * 
	 * @param crate
	 *            The crate
	 * @return The buffer.
	 */
	public ItemStack[] getRandomStackBuffer(ICrate crate) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addFromBuffer(ItemStack[] buffer) {
		// TODO Auto-generated method stub

	}

}
