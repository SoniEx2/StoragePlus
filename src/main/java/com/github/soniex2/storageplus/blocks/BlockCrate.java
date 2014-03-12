package com.github.soniex2.storageplus.blocks;

import com.github.soniex2.storageplus.StoragePlus;
import com.github.soniex2.storageplus.tileentities.TileEntityCrate;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

// TODO implement this
public class BlockCrate extends BlockContainer {

	public BlockCrate() {
		super(Material.wood);
		this.setBlockName("storageplus.crate");
		this.setBlockTextureName("storageplus:crate");
		this.setResistance(0.5F);
		this.setHardness(1.5F);
		this.setCreativeTab(StoragePlus.creativeTab);
		this.setStepSound(soundTypeWood);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityCrate();
	}

}
