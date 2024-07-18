package keletu.enigmaticlegacy.api.cap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlaytimeCounter extends INBTSerializable<NBTTagCompound> {

	public static IPlaytimeCounter get(EntityPlayer player) {
		return player.getCapability(EnigmaticCapabilities.PLAYTIME_COUNTER, null);
	}

	public long getTimeWithoutCurses();

	public long getTimeWithCurses();

	public void setTimeWithoutCurses(long time);

	public void setTimeWithCurses(long time);

	public void incrementTimeWithoutCurses();

	public void incrementTimeWithCurses();

	public void matchStats();

	@Override
	public NBTTagCompound serializeNBT();

	@Override
	public void deserializeNBT(NBTTagCompound nbt);

}