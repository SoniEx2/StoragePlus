package com.github.soniex2.storageplus.api.crate;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackStorage {

	private static final int DEFAULT_MAX_STORAGE = 1152;
	private HashMap<Item, HashMap<NBTTagCompound, TIntIntHashMap>> itemToData = new HashMap<Item, HashMap<NBTTagCompound, TIntIntHashMap>>();
	private long itemsInCrate = 0;
	private final long maxItemsInCrate;
	
	public ItemStackStorage() {
		this(DEFAULT_MAX_STORAGE);
	}
	
	public ItemStackStorage(int max) {
		this.maxItemsInCrate = max;
	}

	public boolean isFull() {
		return itemsInCrate >= maxItemsInCrate;
	}

	public int getStored(ItemStack id) {
		if (id == null)
			return 0;
		HashMap<NBTTagCompound, TIntIntHashMap> nbtToData = itemToData.get(id
				.getItem());
		if (nbtToData == null)
			return 0;
		TIntIntHashMap damageToData = nbtToData.get(id.getTagCompound());
		if (damageToData == null)
			return 0;
		// Use hax so this works "properly"... (Things can still break it, but
		// it takes more effort)
		return damageToData.get(Items.emerald.getDamage(id) & 0x7FFF);
	}

	protected int setStored(ItemStack id, int i) {
		if (id == null)
			return 0;
		HashMap<NBTTagCompound, TIntIntHashMap> nbtToData = itemToData.get(id
				.getItem());
		if (nbtToData == null)
			itemToData.put(id.getItem(),
					new HashMap<NBTTagCompound, TIntIntHashMap>());
		TIntIntHashMap damageToData = nbtToData.get(id.getTagCompound());
		if (damageToData == null)
			nbtToData.put((NBTTagCompound) id.getTagCompound().copy(),
					new TIntIntHashMap(10, 0.5F, -1, 0));
		// Use hax so this works "properly"... (Things can still break it, but
		// it takes more effort)
		int n = damageToData.put(Items.emerald.getDamage(id) & 0x7FFF, i);
		itemsInCrate += i - n;
		return n;
	}

	/**
	 * Remove {@code i} of {@code id} from the stack.
	 * 
	 * @param id
	 *            The key.
	 * @param i
	 *            How much to remove.
	 * @return How much was actually removed.
	 */
	protected int takeStored(ItemStack id, int i) {
		if (id == null)
			return 0;
		HashMap<NBTTagCompound, TIntIntHashMap> nbtToData = itemToData.get(id
				.getItem());
		if (nbtToData == null)
			return 0;
		TIntIntHashMap damageToData = nbtToData.get(id.getTagCompound());
		if (damageToData == null)
			return 0;
		// Use hax so this works "properly"... (Things can still break it, but
		// it takes more effort)
		int stored = damageToData.get(Items.emerald.getDamage(id) & 0x7FFF);
		if (stored == 0) {
			return 0;
		} else if (stored < 0) {
			damageToData.put(Items.emerald.getDamage(id), 0);
			return 0;
		} else if (stored <= i) {
			damageToData.put(Items.emerald.getDamage(id), 0);
			itemsInCrate -= stored;
			return stored;
		}
		damageToData.put(Items.emerald.getDamage(id) & 0x7FFF, stored - i);
		itemsInCrate -= i;
		return i;
	}

	protected int store(ItemStack id, int i) {
		if (id == null)
			return 0;
		HashMap<NBTTagCompound, TIntIntHashMap> nbtToData = itemToData.get(id
				.getItem());
		if (nbtToData == null)
			itemToData.put(id.getItem(),
					new HashMap<NBTTagCompound, TIntIntHashMap>());
		TIntIntHashMap damageToData = nbtToData.get(id.getTagCompound());
		if (damageToData == null)
			nbtToData.put((NBTTagCompound) id.getTagCompound().copy(),
					new TIntIntHashMap(10, 0.5F, -1, 0));
		// Use hax so this works "properly"... (Things can still break it, but
		// it takes more effort)
		int stored = damageToData.get(Items.emerald.getDamage(id) & 0x7FFF);
		itemsInCrate += i;
		return damageToData.put(Items.emerald.getDamage(id) & 0x7FFF, stored
				+ i);
	}

	/**
	 * Get an ItemStack from this stack storage.
	 * 
	 * @param id
	 *            The key.
	 * @param amount
	 *            The max stack size.
	 * @return An ItemStack with stackSize no greater than the stored amount,
	 *         {@code amount}, or the stack's max size.
	 */
	public ItemStack get(ItemStack id, int amount) {
		int stored = getStored(id);
		if (stored <= 0 || amount <= 0 || id == null)
			return null;
		int sSize = Math.min(stored, Math.min(id.getMaxStackSize(), amount));
		ItemStack is = ItemStack.copyItemStack(id);
		is.stackSize = sSize;
		return is;
	}

	/**
	 * Pull an ItemStack from this stack storage.
	 * 
	 * @param id
	 *            The key.
	 * @param amount
	 *            The max stack size.
	 * @return An ItemStack with stackSize no greater than the stored amount,
	 *         {@code amount}, or the stack's max size.
	 */
	public ItemStack pull(ItemStack id, int amount) {
		if (amount <= 0 || id == null)
			return null;
		int sSize = takeStored(id, Math.min(id.getMaxStackSize(), amount));
		if (sSize <= 0)
			return null;
		ItemStack is = ItemStack.copyItemStack(id);
		is.stackSize = sSize;
		return is;
	}

	public void forcePush(ItemStack id) {
		if (id == null)
			return;
		itemsInCrate += (long) id.stackSize;
		store(id, id.stackSize);
	}

	public boolean push(ItemStack id) {
		if (id == null)
			return true;
		if (isFull()) {
			return false;
		}
		if (itemsInCrate + id.stackSize > maxItemsInCrate) {
			// This shouldn't overflow an int
			int overflow = (int) ((itemsInCrate + id.stackSize)
					- maxItemsInCrate);
			store(id, id.stackSize - overflow);
			id.stackSize = overflow;
			return false;
		}
		store(id, id.stackSize);
		return true;
	}
	
	public ItemStack getRandomStack() {
		// TODO
		return null;
	}

	public ItemStack[] toBuffer() {
		ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
		for (Entry<Item, HashMap<NBTTagCompound, TIntIntHashMap>> item : itemToData
				.entrySet()) {
			for (Entry<NBTTagCompound, TIntIntHashMap> nbt : item.getValue()
					.entrySet()) {
				TIntIterator iterator = nbt.getValue().keySet().iterator();
				while (iterator.hasNext()) {
					int damage = iterator.next();
					int count = nbt.getValue().get(damage);
					ItemStack is = new ItemStack(item.getKey(), damage, count);
					while (is.stackSize >= is.getMaxStackSize()) {
						ItemStack out = is.splitStack(is.getMaxStackSize());
						arrayList.add(out);
					}
					if (is.stackSize > 0) {
						arrayList.add(is);
					}
				}
			}
		}
		return (ItemStack[]) arrayList.toArray();
	}

}
