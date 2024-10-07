package keletu.enigmaticlegacy.api.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class EnigmaticCapabilities {

	@CapabilityInject(IPlaytimeCounter.class)
	public static final Capability<IPlaytimeCounter> PLAYTIME_COUNTER = null;

	public static class CapabilityPlayerPlayTime implements IStorage<IPlaytimeCounter> {

		@Override
		public NBTBase writeNBT(Capability<IPlaytimeCounter> capability, IPlaytimeCounter instance, EnumFacing side) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("timeWithCurses", instance.getTimeWithCurses());
			tag.setLong("timeWithoutCurses", instance.getTimeWithoutCurses());
			return tag;
		}

		@Override
		public void readNBT(Capability<IPlaytimeCounter> capability, IPlaytimeCounter instance, EnumFacing side, NBTBase nbt) {
			NBTTagCompound tag = (NBTTagCompound) nbt;
			instance.setTimeWithCurses(tag.getLong("timeWithCurses"));
			instance.setTimeWithoutCurses(tag.getLong("timeWithoutCurses"));
		}
	}
}
