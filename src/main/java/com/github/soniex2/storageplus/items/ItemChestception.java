package com.github.soniex2.storageplus.items;

import com.github.soniex2.storageplus.StoragePlus;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemChestception extends Item {

	public ItemChestception() {
		this.setCreativeTab(StoragePlus.creativeTab);
		this.setTextureName("storageplus:chestception");
		this.setUnlocalizedName("storageplus.chestception");
		this.setNoRepair();
		this.setMaxStackSize(1);
		this.setMaxDamage(255);
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack is) {
		return false;
	}

	@Override
	public boolean hasContainerItem(ItemStack is) {
		if (is.getItemDamage() < is.getMaxDamage())
			return true;
		return false;
	}

	@Override
	public ItemStack getContainerItem(ItemStack is) {
		if (!hasContainerItem(is)) {
			return null;
		}
		return new ItemStack(this, 1, is.getItemDamage() - 1);
	}

}
