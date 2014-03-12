package com.github.soniex2.storageplus.blocks;

import net.minecraft.item.ItemBlock;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class StoragePlusBlocks {

	public BlockCrate crate;

	public void preInit(FMLPreInitializationEvent event) {
		crate = new BlockCrate();
		GameRegistry.registerBlock(crate, ItemBlock.class, "crate", "storageplus");
	}

	public void init(FMLInitializationEvent event) {
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

}
