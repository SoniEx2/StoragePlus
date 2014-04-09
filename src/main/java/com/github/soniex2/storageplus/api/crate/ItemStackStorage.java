package com.github.soniex2.storageplus.api.crate;

import gnu.trove.map.hash.TIntIntHashMap;

import java.util.HashMap;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackStorage {

	public HashMap<Item, HashMap<NBTTagCompound, TIntIntHashMap>> itemToData = new HashMap<Item, HashMap<NBTTagCompound, TIntIntHashMap>>();

	public boolean isFull() {
		// TODO Auto-generated method stub
		return false;
	}

	private int getStored(ItemStack id) {
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

	public ItemStack get(ItemStack id, int amount) {
		int stored = getStored(id);
		// TODO
		return null;
	}

	public ItemStack pull(ItemStack id, int amount) {
		// TODO
		return null;
	}

	public void forcePush(ItemStack id) {
		// TODO
	}

	public boolean push(ItemStack id) {
		// TODO
		return false;
	}

	public ItemStack[] toBuffer() {
		// TODO Auto-generated method stub
		return null;
	}

}
