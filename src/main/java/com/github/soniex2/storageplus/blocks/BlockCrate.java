package com.github.soniex2.storageplus.blocks;

import com.github.soniex2.storageplus.StoragePlus;
import com.github.soniex2.storageplus.tileentities.TileEntityCrate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

// TODO implement this
public class BlockCrate extends BlockContainer {

	private final IIcon[] icons = new IIcon[6];

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

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		icons[0] = register.registerIcon("storageplus:crate_oak");
		icons[1] = register.registerIcon("storageplus:crate_spruce");
		icons[2] = register.registerIcon("storageplus:crate_brich");
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
}
