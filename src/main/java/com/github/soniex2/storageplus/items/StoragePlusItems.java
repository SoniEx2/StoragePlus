package com.github.soniex2.storageplus.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.github.soniex2.storageplus.StoragePlus;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class StoragePlusItems {

	public ItemChestception chestception;

	public void preInit(FMLPreInitializationEvent event) {
		chestception = new ItemChestception();
		GameRegistry.registerItem(chestception, "chestception", "storageplus");
	}

	public void init(FMLInitializationEvent event) {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				StoragePlus.blocks.crate), "#/#", "/@/", "#/#", '#',
				"plankWood", '/', "stickWood", '@', new ItemStack(chestception,
						1, OreDictionary.WILDCARD_VALUE)));
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

}
