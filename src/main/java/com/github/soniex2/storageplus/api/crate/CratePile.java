package com.github.soniex2.storageplus.api.crate;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.github.soniex2.storageplus.StoragePlus;

import net.minecraft.item.ItemStack;

public class CratePile {

	private LinkedHashMap<ICrate, ItemStackStorage> map;

	public CratePile() {
	}

	public ItemStack getRandomStack(ICrate crate) {
		// TODO implement this
		// Use int i = Math.min(stack.getMaxStackSize(),
		// something.getItemCount()) or something, then add i to a counter to
		// keep track of how many stacks you can still give out, while still
		// being able to display items on the GUI. then use crazy shit to suck
		// items back or something.
		return null;
	}

	public void add(ICrate crate) {
		map.put(crate, new ItemStackStorage());
	}

	/**
	 * Returns true if this CratePile is full.
	 * 
	 * @return {@code true} if this CratePile is full, {@code false} otherwise
	 */
	public boolean isFull() {
		Iterator<Map.Entry<ICrate, ItemStackStorage>> i = map.entrySet()
				.iterator();
		for (ItemStackStorage v = null; i.hasNext(); v = i.next().getValue()) {
			if (!v.isFull())
				return false;
		}
		return true;
	}

	/**
	 * Gets the buffer for a crate. This doesn't remove items from the
	 * CratePile.
	 * 
	 * @param crate
	 *            The crate
	 * @return The buffer.
	 */
	public ItemStack[] getBufferForCrate(ICrate crate) {
		return map.get(crate).toBuffer();
	}

	public enum InsertStatus {
		FAIL, PARTIAL, SUCCESS;
	}

	/**
	 * Insert an ItemStack into this CratePile.
	 * 
	 * @param is
	 *            The ItemStack to insert.
	 * @param force
	 *            Insert even if doing so would overflow a crate.
	 * @return Status. <b>In case of a partial insertion, the ItemStack's
	 *         stackSize will be modified.</b>
	 */
	public InsertStatus insert(ItemStack is, boolean force) {
		if (is == null || is.stackSize == 0)
			// Inserting null or an empty stack should change nothing
			return InsertStatus.SUCCESS;
		Iterator<Map.Entry<ICrate, ItemStackStorage>> i = map.entrySet()
				.iterator();
		for (ItemStackStorage v = null; i.hasNext(); v = i.next().getValue()) {
			if (v == null) { // shouldn't happen but who knows
				continue;
			}
			if (!v.isFull()) {

			}
		}
		return InsertStatus.SUCCESS;
	}

	/**
	 * Inserts a buffer into this CratePile. The buffer is randomly distributed
	 * to this pile's crates.
	 * 
	 * @param buffer
	 *            The buffer
	 * @return Status. <b>In case of a partial insertion, the buffer will be
	 *         modified. This includes stackSizes</b>
	 */
	public InsertStatus insertFromBuffer(ItemStack[] buffer) {
		if (isFull())
			return InsertStatus.FAIL;
		for (ItemStack is : buffer) {
			if (is == null || is.stackSize == 0)
				continue;
			InsertStatus i = insert(is, false);
			switch (i) {
			case FAIL:
			case PARTIAL:
				return InsertStatus.PARTIAL;
			case SUCCESS:
			default:
				break;
			}
		}
		return InsertStatus.SUCCESS;
	}

	public void remove(ICrate crate) {
		if (map.containsKey(crate)) {
			map.remove(crate);
			crate.setCratePile(null);
		} else {
			StoragePlus.log.log(Level.ERROR, "Crate not in stack: %s", crate);
		}
	}

}
