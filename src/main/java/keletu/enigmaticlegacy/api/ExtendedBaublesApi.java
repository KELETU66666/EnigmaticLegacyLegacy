package keletu.enigmaticlegacy.api;

import keletu.enigmaticlegacy.api.cap.EnigmaticCapabilities;
import keletu.enigmaticlegacy.api.cap.IExtendedBaublesItemHandler;
import keletu.enigmaticlegacy.api.cap.IPlaytimeCounter;
import keletu.enigmaticlegacy.api.inv.ExtendedBaublesInventoryWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;

/**
 * @author Azanor
 */
public class ExtendedBaublesApi
{
	/**
	 * Retrieves the baubles inventory capability handler for the supplied player
	 */
	public static IExtendedBaublesItemHandler getBaublesHandler(EntityPlayer player)
	{
		IExtendedBaublesItemHandler handler = player.getCapability(EnigmaticCapabilities.CAPABILITY_BAUBLES, null);
		handler.setPlayer(player);
		return handler;
	}

	/**
	 * Retrieves the baubles capability handler wrapped as a IInventory for the supplied player
	 */
	@Deprecated
	public static IInventory getBaubles(EntityPlayer player)
	{
		IExtendedBaublesItemHandler handler = player.getCapability(EnigmaticCapabilities.CAPABILITY_BAUBLES, null);
		handler.setPlayer(player);
		return new ExtendedBaublesInventoryWrapper(handler, player);
	}
	
	/**
	 * Returns if the passed in item is equipped in a bauble slot. Will return the first slot found
	 * @return -1 if not found and slot number if it is found 
	 */
	public static int isBaubleEquipped(EntityPlayer player, Item bauble) {
		IExtendedBaublesItemHandler handler = getBaublesHandler(player);
		for (int a=0;a<handler.getSlots();a++) {
			if (!handler.getStackInSlot(a).isEmpty() && handler.getStackInSlot(a).getItem()==bauble) return a;
		}
		return -1;
	}

	public static IPlaytimeCounter getPlayerPlayTime(EntityPlayer player)
	{
		IPlaytimeCounter handler = player.getCapability(EnigmaticCapabilities.PLAYTIME_COUNTER, null);
		IPlaytimeCounter.get(player);
		return handler;
	}

}
