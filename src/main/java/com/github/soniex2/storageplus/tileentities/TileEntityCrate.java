package com.github.soniex2.storageplus.tileentities;

import java.util.List;

import com.github.soniex2.storageplus.StoragePlus;
import com.github.soniex2.storageplus.api.CratePile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
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

	private boolean[] sideConnected = new boolean[ForgeDirection.VALID_DIRECTIONS.length];

	private int ticksSinceLastUpdate = 0;

	public TileEntityCrate() {
	}

	/**
	 * Returns the internal {@link CratePile}.
	 * 
	 * @return the internal CratePile
	 */
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
		for (int i = 0; i < sideConnected.length; i++) {
			nbt.setBoolean("connected" + i, sideConnected[i]);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		for (int i = 0; i < sideConnected.length; i++) {
			sideConnected[i] = nbt.getBoolean("connected" + i);
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound nbt = pkt.func_148857_g();
		readFromNBT(nbt);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote)
			return;
		// if (this.crateStack == null) {
		// TODO update connected crates
		// return;
		// }
		// Run 2 times per second because ItemStack construction is expensive
		if (ticksSinceLastUpdate >= 10) {
			for (int i = 0; i < Math.min(inventory.length,
					originalInventory.length); i++) {
				if (!ItemStack.areItemStacksEqual(inventory[i],
						originalInventory[i])) {
					this.markDirty();
					StoragePlus.log
							.warn(String
									.format("Some stupid machine is interacting with crate at %s,%s,%s on dimension %s.",
											this.xCoord, this.yCoord,
											this.zCoord,
											this.worldObj.provider.dimensionId));
				}
			}
		} else {
			ticksSinceLastUpdate++;
		}
		boolean update = false;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (this.worldObj.getBlockMetadata(this.xCoord + dir.offsetX,
					this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ) == this.worldObj
					.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord)) {
				TileEntity tileEntity = this.worldObj.getTileEntity(this.xCoord
						+ dir.offsetX, this.yCoord + dir.offsetY, this.zCoord
						+ dir.offsetZ);
				if (tileEntity != null && tileEntity instanceof TileEntityCrate) {
					if (this.crateStack == ((TileEntityCrate) tileEntity).crateStack
							&& !sideConnected[dir.ordinal()]) {
						sideConnected[dir.ordinal()] = true;
						update = true;
					} else if (this.crateStack != ((TileEntityCrate) tileEntity).crateStack
							&& sideConnected[dir.ordinal()]) {
						sideConnected[dir.ordinal()] = false;
						update = true;
					}
				}
			}
		}
		if (update) {
			this.markDirty();
		}
	}

	public void setupCrate(EntityPlayer player, World world, int x, int y,
			int z, int side, int metadata) {
		if (world.isRemote)
			return;
		ForgeDirection sideClicked = ForgeDirection.getOrientation(side);
		ForgeDirection oppositeSide = sideClicked.getOpposite();
		TileEntity te = world.getTileEntity(this.xCoord + oppositeSide.offsetX,
				this.yCoord + oppositeSide.offsetY, this.zCoord
						+ oppositeSide.offsetZ);
		// Check crate
		if (te != null && te instanceof TileEntityCrate) {
			((TileEntityCrate) te).crateStack.add(this);
			this.crateStack = ((TileEntityCrate) te).crateStack;
			// Update connections
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				if (this.worldObj.getBlockMetadata(this.xCoord + dir.offsetX,
						this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ) == metadata) {
					TileEntity tileEntity = this.worldObj.getTileEntity(
							this.xCoord + dir.offsetX, this.yCoord
									+ dir.offsetY, this.zCoord + dir.offsetZ);
					if (tileEntity != null
							&& tileEntity instanceof TileEntityCrate) {
						if (this.crateStack == ((TileEntityCrate) tileEntity).crateStack) {
							sideConnected[dir.ordinal()] = true;
						}
					}
				}
			}
		}
	}
}
