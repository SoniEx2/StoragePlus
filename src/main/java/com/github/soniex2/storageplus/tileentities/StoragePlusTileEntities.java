package com.github.soniex2.storageplus.tileentities;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class StoragePlusTileEntities {

	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerTileEntity(TileEntityCrate.class, "storageplus:crate");
	}

	public void init(FMLInitializationEvent event) {
	}

	public void postInit(FMLPostInitializationEvent event) {
	}
}
