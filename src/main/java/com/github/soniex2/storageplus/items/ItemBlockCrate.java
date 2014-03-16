package com.github.soniex2.storageplus.items;

import com.github.soniex2.storageplus.tileentities.TileEntityCrate;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemBlockCrate extends ItemBlock {

	public ItemBlockCrate(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int par1) {
		return par1;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ, int metadata) {
		if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY,
				hitZ, metadata)) {
			if (world.getBlock(x, y, z) == field_150939_a) {
				TileEntity te = world.getTileEntity(x, y, z);
				if (te != null && te instanceof TileEntityCrate) {
					((TileEntityCrate) te).setupCrate(player, world, x, y, z,
							side, metadata);
				}
			}
			return true;
		} else {
			return false;
		}
	}
}
