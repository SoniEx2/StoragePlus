package com.github.soniex2.storageplus.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockCrate extends ItemBlock {

	public ItemBlockCrate(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int par1) {
		return par1;
	}

}
