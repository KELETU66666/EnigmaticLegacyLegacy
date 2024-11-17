package keletu.enigmaticlegacy.api.bmtr;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.common.container.ContainerPlayerExpanded;
import baubles.common.container.SlotBauble;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBaubles extends ContainerPlayerExpanded {

	private EntityPlayer thePlayer;

	public ContainerBaubles(InventoryPlayer playerInv, boolean par2, EntityPlayer player) {
		super(playerInv, par2, player);
		this.thePlayer = player;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if ((slot != null) && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
			int slotShift = this.baubles.getSlots();
			if (index == 0) { //Slot crafting result --> inv
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, true)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(itemstack1, itemstack);
			} else if ((index >= 1) && (index < 5)) { //Crafting grid --> inv
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			} else if ((index >= 5) && (index < 9)) { //Armor --> inv
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}
			// baubles -> inv
			else if ((index >= 9) && (index < (9 + slotShift))) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}

			// inv -> armor
			else if ((entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) && !this.inventorySlots.get(8 - entityequipmentslot.getIndex()).getHasStack()) {
				int i = 8 - entityequipmentslot.getIndex();

				if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
					return ItemStack.EMPTY;
				}
			}

			// inv -> offhand
			else if ((entityequipmentslot == EntityEquipmentSlot.OFFHAND) && !this.inventorySlots.get(45 + slotShift).getHasStack()) {
				if (!this.mergeItemStack(itemstack1, 45 + slotShift, 46 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}
			// inv -> bauble
			else if (itemstack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
				IBauble bauble = itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
				for (int baubleSlot : bauble.getBaubleType(itemstack).getValidSlots()) {
					if (bauble.canEquip(itemstack1, this.thePlayer) && !this.inventorySlots.get(baubleSlot + 9).getHasStack() && !this.mergeItemStack(itemstack1, baubleSlot + 9, baubleSlot + 10, false)) {
						return ItemStack.EMPTY;
					}
					if (itemstack1.getCount() == 0) {
						break;
					}
				}
			} else if ((index >= (9 + slotShift)) && (index < (36 + slotShift))) {
				if (!this.mergeItemStack(itemstack1, 36 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			} else if ((index >= (36 + slotShift)) && (index < (45 + slotShift))) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 36 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty() && !this.baubles.isEventBlocked() && (slot instanceof SlotBauble) && itemstack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
				itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(itemstack, playerIn);
			}

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

			if (index == 0) {
				playerIn.dropItem(itemstack2, false);
			}
		}

		return itemstack;
	}

//	@Override
	public ItemStack atransferStackInSlot(EntityPlayer playerIn, int index) {

		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if ((slot != null) && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);

			int slotShift = this.baubles.getSlots();

			if (index == 0) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if ((index >= 1) && (index < 5)) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			} else if ((index >= 5) && (index < 9)) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}

			// baubles -> inv
			else if ((index >= 9) && (index < (9 + slotShift))) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}

			// inv -> armor
			else if ((entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) && !this.inventorySlots.get(8 - entityequipmentslot.getIndex()).getHasStack()) {
				int i = 8 - entityequipmentslot.getIndex();

				if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
					return ItemStack.EMPTY;
				}
			}

			// inv -> offhand
			else if ((entityequipmentslot == EntityEquipmentSlot.OFFHAND) && !this.inventorySlots.get(45 + slotShift).getHasStack()) {
				if (!this.mergeItemStack(itemstack1, 45 + slotShift, 46 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}
			// inv -> bauble
			else if (itemstack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
				IBauble bauble = itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
				for (int baubleSlot : bauble.getBaubleType(itemstack).getValidSlots()) {
					if (bauble.canEquip(itemstack1, this.thePlayer) && !this.inventorySlots.get(baubleSlot + 9).getHasStack() && !this.mergeItemStack(itemstack1, baubleSlot + 9, baubleSlot + 10, false)) {
						return ItemStack.EMPTY;
					}
					if (itemstack1.getCount() == 0) {
						break;
					}
				}
			} else if ((index >= (9 + slotShift)) && (index < (36 + slotShift))) {
				if (!this.mergeItemStack(itemstack1, 36 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			} else if ((index >= (36 + slotShift)) && (index < (45 + slotShift))) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 36 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty() && !this.baubles.isEventBlocked() && (slot instanceof SlotBauble) && itemstack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
				itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(itemstack, playerIn);
			}

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

			if (index == 0) {
				playerIn.dropItem(itemstack2, false);
			}
		}

		return itemstack;
	}

}