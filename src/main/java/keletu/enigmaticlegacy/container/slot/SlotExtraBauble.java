package keletu.enigmaticlegacy.container.slot;

import keletu.enigmaticlegacy.api.IExtendedBauble;
import keletu.enigmaticlegacy.api.cap.ExtendedBaublesCapabilities;
import keletu.enigmaticlegacy.api.cap.IExtendedBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotExtraBauble extends SlotItemHandler
{
	int baubleSlot;
	EntityPlayer player;

	public SlotExtraBauble(EntityPlayer player, IExtendedBaublesItemHandler itemHandler, int slot, int par4, int par5)
	{
		super(itemHandler, slot, par4, par5);
		this.baubleSlot = slot;
		this.player = player;
	}

	/**
	 * Check if the stack is a valid item for this slot.
	 */
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return ((IExtendedBaublesItemHandler)getItemHandler()).isItemValidForSlot(baubleSlot, stack, player);
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		ItemStack stack = getStack();
		if(stack.isEmpty())
			return false;

		IExtendedBauble bauble = stack.getCapability(ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
		return bauble.canUnequip(stack, player);
	}

	@Override
	public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
		if (!getHasStack() && !((IExtendedBaublesItemHandler)getItemHandler()).isEventBlocked() &&
				stack.hasCapability(ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			stack.getCapability(ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(stack, playerIn);
		}
		super.onTake(playerIn, stack);
		return stack;
	}

	@Override
	public void putStack(ItemStack stack) {
		if (getHasStack() && !ItemStack.areItemStacksEqual(stack,getStack()) &&
				!((IExtendedBaublesItemHandler)getItemHandler()).isEventBlocked() &&
				getStack().hasCapability(ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			getStack().getCapability(ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(getStack(), player);
		}

		ItemStack oldstack = getStack().copy();
		super.putStack(stack);

		if (getHasStack() && !ItemStack.areItemStacksEqual(oldstack,getStack())
				&& !((IExtendedBaublesItemHandler)getItemHandler()).isEventBlocked() &&
				getStack().hasCapability(ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			getStack().getCapability(ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onEquipped(getStack(), player);
		}
	}

	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}