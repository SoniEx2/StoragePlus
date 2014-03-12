package com.github.soniex2.storageplus;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class StoragePlusCreativeTab extends CreativeTabs {

	public StoragePlusCreativeTab() {
		super("storageplus.tab");
	}

	@Override
	public Item getTabIconItem() {
		return StoragePlus.items.chestception;
	}

	// This might get changed to getIconItemDamage
	@Override
	public int func_151243_f() {
		return 0;
	}

}
