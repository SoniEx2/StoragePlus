package com.github.soniex2.storageplus.blocks;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.github.soniex2.storageplus.StoragePlus;
import com.github.soniex2.storageplus.tileentities.TileEntityCrate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// TODO implement this
public class BlockCrate extends BlockContainer {

	private final IIcon[] icons = new IIcon[6];

	public BlockCrate() {
		super(Material.wood);
		this.setBlockName("storageplus.crate");
		this.setBlockTextureName("storageplus:crate");
		this.setResistance(5.0F);
		this.setHardness(2.0F);
		this.setCreativeTab(StoragePlus.creativeTab);
		this.setStepSound(soundTypeWood);
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityCrate();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z,
			int tileX, int tileY, int tileZ) {
		int offX = tileX - x;
		int offY = tileY - y;
		int offZ = tileZ - z;
		ForgeDirection dir = ForgeDirection.UNKNOWN;
		for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
			if (d.offsetX == offX && d.offsetY == offY && d.offsetZ == offZ) {
				dir = d;
				break;
			}
		}
		if (dir == ForgeDirection.UNKNOWN) {
			// ???
			return;
		}
		TileEntityCrate thisCrate = (TileEntityCrate) world.getTileEntity(x, y,
				z);
		TileEntity another = world.getTileEntity(tileX, tileY, tileZ);
		if (thisCrate.sideConnected[dir.ordinal()]) {
			if (another == null
					&& (world.getBlock(tileX, tileY, tileZ) instanceof BlockCrate)) {
				thisCrate.updateContainingBlockInfo();
				thisCrate.markDirty();
			} else if (another instanceof TileEntityCrate) {
				if (((TileEntityCrate) another).getCratePile() == null) {
					thisCrate.updateContainingBlockInfo();
					thisCrate.markDirty();
				}
			}

		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		icons[0] = register.registerIcon("storageplus:crate_oak");
		icons[1] = register.registerIcon("storageplus:crate_spruce");
		icons[2] = register.registerIcon("storageplus:crate_birch");
		icons[3] = register.registerIcon("storageplus:crate_jungle");
		icons[4] = register.registerIcon("storageplus:crate_acacia");
		icons[5] = register.registerIcon("storageplus:crate_darkoak");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		if (metadata >= icons.length || metadata < 0) {
			return icons[0];
		}
		return icons[metadata];
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List itemStacks) {
		for (int i = 0; i < icons.length; i++) {
			itemStacks.add(new ItemStack(item, 1, i));
		}
	}
}
