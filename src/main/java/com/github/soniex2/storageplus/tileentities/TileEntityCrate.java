package com.github.soniex2.storageplus.tileentities;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.github.soniex2.storageplus.StoragePlus;
import com.github.soniex2.storageplus.api.crate.CratePile;
import com.github.soniex2.storageplus.api.crate.ICrate;

// TODO implement this
// How this'll work:
// Each connected TileEntity stores random items
// You have to put them all together to get the full inv
// etc...
public class TileEntityCrate extends TileEntity implements IInventory, ICrate {

	private CratePile crateStack;

	/** IInventory access */
	private ItemStack[] inventory = new ItemStack[5];
	private ItemStack[] originalInventory = new ItemStack[5];

	private ItemStack[] buffer;

	private boolean[] sideConnected = new boolean[ForgeDirection.VALID_DIRECTIONS.length];

	private int ticksSinceLastUpdate = 0;

	private boolean read = false;

	public TileEntityCrate() {
	}

	/**
	 * Returns the internal {@link CratePile}.
	 * 
	 * @return the internal CratePile
	 */
	@Override
	public CratePile getCratePile() {
		return crateStack;
	}

	@Override
	public int getSizeInventory() {
		return crateStack.isFull() ? inventory.length - 1 : inventory.length;
	}

	private boolean isOutOfBounds(int slot) {
		return slot >= getSizeInventory() || slot < 0;
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
		if (inventory[slot] != null) {
			if (inventory[slot].stackSize > count) {
				is = inventory[slot];
				inventory[slot] = null;
			} else if (inventory[slot].stackSize <= count) {
				is = inventory[slot].splitStack(count);
				if (inventory[slot].stackSize <= 0) {
					inventory[slot] = null;
				}
			}
		}
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

		// TODO save crateStack stuff
		buffer = crateStack.getBufferForCrate(this);
		if (buffer == null)
			return;
		NBTTagList buf = new NBTTagList();
		nbt.setTag("buffer", buf);
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] != null) {
				buf.appendTag(buffer[i].writeToNBT(new NBTTagCompound()));
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		read = true;
		super.readFromNBT(nbt);
		for (int i = 0; i < sideConnected.length; i++) {
			sideConnected[i] = nbt.getBoolean("connected" + i);
		}

		// TODO read buffer
		int tagId = 0;
		for (int i = 0; i < NBTBase.NBTTypes.length; i++) {
			if (NBTBase.NBTTypes[i] == "COMPOUND") {
				tagId = i;
			}
		}
		NBTTagList buf = nbt.getTagList("buffer", tagId);
		if (buf == null || buf.tagCount() == 0) {
			return; // no buffer?
		}
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < buf.tagCount(); i++) {
			ItemStack is = ItemStack.loadItemStackFromNBT(buf
					.getCompoundTagAt(i));
			if (is != null) {
				list.add(is);
			}
		}
		buffer = list.toArray(new ItemStack[list.size()]); // Wait until next
															// update
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
	public void markDirty() {
		// TODO update crateStack
		super.markDirty();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote)
			return;
		// Run 2 times per second because ItemStack construction is expensive
		if (ticksSinceLastUpdate >= 10) {
			ticksSinceLastUpdate = 0;
			if (this.crateStack != null) {
				if (buffer != null) {
					crateStack.insertFromBuffer(buffer);
					buffer = null;
				}
				for (int i = 0; i < Math.min(inventory.length,
						originalInventory.length); i++) {
					if (!ItemStack.areItemStacksEqual(inventory[i],
							originalInventory[i])) {
						this.markDirty();
						StoragePlus.log
								.warn(String
										.format("Some stupid machine is interacting with crate at %s,%s,%s on dimension %s.",
												this.xCoord,
												this.yCoord,
												this.zCoord,
												this.worldObj.provider.dimensionId));
						break;
					}
				}
				// TODO update inv
			} else {
				updateContainingBlockInfo();
			}
		} else {
			ticksSinceLastUpdate++;
		}
	}

	private int failCounter = 0;

	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		if (!read) {
			if (failCounter < 4) {
				failCounter++;
				return;
			}
			read = true; // "fuck it all"
		}
		boolean update = false;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (sideConnected[dir.ordinal()]) {
				if (this.worldObj.getBlockMetadata(this.xCoord + dir.offsetX,
						this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ) != this.worldObj
						.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord)) {
					sideConnected[dir.ordinal()] = false;
					update = true;
				} else {
					TileEntity tileEntity = this.worldObj.getTileEntity(
							this.xCoord + dir.offsetX, this.yCoord
									+ dir.offsetY, this.zCoord + dir.offsetZ);
					if (tileEntity == null
							|| !(tileEntity instanceof TileEntityCrate)) {
						sideConnected[dir.ordinal()] = false;
						update = true;
					} else if (this.crateStack == null
							&& ((TileEntityCrate) tileEntity).crateStack == null) {
						this.crateStack = new CratePile();
						((TileEntityCrate) tileEntity).crateStack = this.crateStack;
						tileEntity.updateContainingBlockInfo();
						update = true;
					} else if (((TileEntityCrate) tileEntity).crateStack == null) {
						((TileEntityCrate) tileEntity).crateStack = this.crateStack;
						update = true;
					} else if (this.crateStack == null) {
						this.crateStack = ((TileEntityCrate) tileEntity).crateStack;
						tileEntity.updateContainingBlockInfo();
						update = true;
					} else if (this.crateStack != ((TileEntityCrate) tileEntity).crateStack) {
						sideConnected[dir.ordinal()] = false;
						tileEntity.updateContainingBlockInfo();
						update = true;
					}
				}
			} else {
				if (this.worldObj.getBlockMetadata(this.xCoord + dir.offsetX,
						this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ) == this.worldObj
						.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord)) {
					TileEntity tileEntity = this.worldObj.getTileEntity(
							this.xCoord + dir.offsetX, this.yCoord
									+ dir.offsetY, this.zCoord + dir.offsetZ);
					if (tileEntity != null
							&& tileEntity instanceof TileEntityCrate) {
						if (this.crateStack == ((TileEntityCrate) tileEntity).crateStack) {
							if (this.crateStack != null) { // they're equal,
															// just check
															// for null
								sideConnected[dir.ordinal()] = true;
								tileEntity.updateContainingBlockInfo();
								update = true;
							}
						}
					}
				}
			}
		}
		if (crateStack == null) { // We couldn't find a crate stack
			this.crateStack = new CratePile(); // Just make it
			update = true;
		}
		if (update) {
			// TODO sync TileEntity data
		}
	}

	public void setupCrate(EntityPlayer player, World world, int x, int y,
			int z, int side, int metadata) {
		if (world.isRemote)
			return;
		read = true; // Skip the whole update check thing
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
		} else {
			this.crateStack = new CratePile();
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		for (int i = 0; i < Math
				.min(inventory.length, originalInventory.length); i++) {
			if (!ItemStack.areItemStacksEqual(inventory[i],
					originalInventory[i])) {
				// Chunk saves only after onChunkUnload so it's safe to
				// markDirty
				this.markDirty();
				StoragePlus.log
						.warn(String
								.format("Some stupid machine is interacting with crate at %s,%s,%s on dimension %s.",
										this.xCoord, this.yCoord, this.zCoord,
										this.worldObj.provider.dimensionId));
				break;
			}
		}
	}
}
