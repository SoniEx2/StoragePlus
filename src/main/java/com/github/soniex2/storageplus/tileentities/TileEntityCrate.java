package com.github.soniex2.storageplus.tileentities;

import com.github.soniex2.storageplus.StoragePlus;
import com.github.soniex2.storageplus.api.CratePile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

// TODO implement this
// How this'll work:
// Each connected TileEntity stores random items
// You have to put them all together to get the full inv
// etc...
public class TileEntityCrate extends TileEntity implements IInventory {

	private CratePile crateStack;

	/** IInventory access */
	private ItemStack[] inventory = new ItemStack[5];
	private ItemStack[] originalInventory = new ItemStack[5];

	private int ticksSinceLastUpdate = 0;

	public TileEntityCrate() {
	}

	public CratePile getCratePile() {
		return crateStack;
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
		ItemStack is = null;

		this.markDirty();
		return is;
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

	@Override
	public void updateEntity() {
		super.updateEntity();
		// Run 2 times per second because ItemStack construction is expensive
		if (ticksSinceLastUpdate >= 10) {
			for (int i = 0; i < Math.min(inventory.length,
					originalInventory.length); i++) {
				if (!ItemStack.areItemStacksEqual(inventory[i],
						originalInventory[i])) {
					this.markDirty();
					StoragePlus.log.warn("Please forward the following code to SoniEx2: mkdirt");
				}
			}
		} else {
			ticksSinceLastUpdate++;
		}
	}

	public void setupCrate(EntityPlayer player, World world, int x, int y,
			int z, int side, int metadata) {
		// TODO write this.
		ForgeDirection sideClicked = ForgeDirection.getOrientation(side).getOpposite();
		ForgeDirection oppositeSide = sideClicked.getOpposite();
	}

}
