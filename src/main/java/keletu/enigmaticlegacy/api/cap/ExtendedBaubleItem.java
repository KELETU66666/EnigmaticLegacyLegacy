package keletu.enigmaticlegacy.api.cap;

import keletu.enigmaticlegacy.api.ExtendedBaubleType;
import keletu.enigmaticlegacy.api.IExtendedBauble;
import net.minecraft.item.ItemStack;

public class ExtendedBaubleItem implements IExtendedBauble
{
	private ExtendedBaubleType baubleType;

	public ExtendedBaubleItem(ExtendedBaubleType type) {
		baubleType = type;
	}

	@Override
	public ExtendedBaubleType getBaubleType(ItemStack itemstack) {
		return baubleType;
	}
}
