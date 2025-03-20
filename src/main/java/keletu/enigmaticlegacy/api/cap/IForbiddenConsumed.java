package keletu.enigmaticlegacy.api.cap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IForbiddenConsumed extends INBTSerializable<NBTTagCompound> {

	public static IForbiddenConsumed get(EntityPlayer player) {
		return player.getCapability(EnigmaticCapabilities.FORBIDDEN_FRUIT_CONSUME, null);
	}

	public boolean isConsumed();

	public void setConsumed(boolean bool);

	public int getSpellstoneCooldown();

	public void setSpellstoneCooldown(int cooldown);
	public void decreaseCooldown();

	@Override
	public NBTTagCompound serializeNBT();

	@Override
	public void deserializeNBT(NBTTagCompound nbt);

}