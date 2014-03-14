package com.github.soniex2.storageplus.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

// TODO implement this
public class TileEntityCrate extends TileEntity implements IInventory {

	private ItemStack[] inventory = new ItemStack[18];

	public TileEntityCrate() {
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	private boolean isOutOfBounds(int slot) {
		return slot >= inventory.length || slot < 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (isOutOfBounds(slot)) {
			return null;
		}
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int count) {
		if (isOutOfBounds(slot)) {
			return null;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (isOutOfBounds(slot)) {
			return null;
		}
		inventory[slot] = null;
		this.markDirty();
		return inventory[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (isOutOfBounds(slot)) {
			return;
		}
		inventory[slot] = stack;
		this.markDirty();
	}

	@Override
	public String getInventoryName() {
		// Same name as block/item
		return "tile.storageplus.crate.name";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord,
				this.zCoord) != this ? false : player.getDistanceSq(
				(double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D,
				(double) this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (isOutOfBounds(slot)) {
			return false;
		}
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}

}
