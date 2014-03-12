package com.github.soniex2.storageplus;

import net.minecraftforge.common.MinecraftForge;

import com.github.soniex2.storageplus.blocks.StoragePlusBlocks;
import com.github.soniex2.storageplus.event.EventListener;
import com.github.soniex2.storageplus.items.StoragePlusItems;
import com.github.soniex2.storageplus.proxy.CommonProxy;
import com.github.soniex2.storageplus.tileentities.StoragePlusTileEntities;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "storageplus", name = "Storage+", version = Version.MOD_VERSION, dependencies = "required-after:Forge")
public class StoragePlus {

	@Instance("storageplus")
	public static StoragePlus instance;

	public static StoragePlusItems items;
	public static StoragePlusBlocks blocks;
	public static StoragePlusTileEntities tileEntities;

	@SidedProxy(modId = "storageplus", clientSide = "com.github.soniex2.storageplus.proxy.CommonProxy", serverSide = "com.github.soniex2.storageplus.proxy.ClientProxy")
	public static CommonProxy proxy;

	public static StoragePlusCreativeTab creativeTab;

	@SubscribeEvent
	public void preInit(FMLPreInitializationEvent event) {
		FMLLog.info("Loading Storage+");
		creativeTab = new StoragePlusCreativeTab();
		blocks = new StoragePlusBlocks();
		tileEntities = new StoragePlusTileEntities();
		items = new StoragePlusItems();

		blocks.preInit(event);
		tileEntities.preInit(event);
		items.preInit(event);
		proxy.preInit(event);

		MinecraftForge.EVENT_BUS.register(new EventListener());
	}

	@SubscribeEvent
	public void init(FMLInitializationEvent event) {
		blocks.init(event);
		tileEntities.init(event);
		items.init(event);
		proxy.init(event);
	}

	@SubscribeEvent
	public void postInit(FMLPostInitializationEvent event) {
		blocks.postInit(event);
		tileEntities.postInit(event);
		items.postInit(event);
		proxy.postInit(event);
		FMLLog.info("Done!");
	}
}
