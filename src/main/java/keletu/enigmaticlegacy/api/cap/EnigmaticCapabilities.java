package keletu.enigmaticlegacy.api.cap;

import keletu.enigmaticlegacy.api.IExtendedBauble;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class EnigmaticCapabilities {
	/**
	 * Access to the baubles capability. 
	 */
	@CapabilityInject(IExtendedBaublesItemHandler.class)
	public static final Capability<IExtendedBaublesItemHandler> CAPABILITY_BAUBLES = null;

	@CapabilityInject(IExtendedBauble.class)
	public static final Capability<IExtendedBauble> CAPABILITY_ITEM_BAUBLE = null;

	@CapabilityInject(IPlaytimeCounter.class)
	public static final Capability<IPlaytimeCounter> PLAYTIME_COUNTER = null;

	public static class CapabilityBaubles<T extends IExtendedBaublesItemHandler> implements IStorage<IExtendedBaublesItemHandler> {

		@Override
		public NBTBase writeNBT (Capability<IExtendedBaublesItemHandler> capability, IExtendedBaublesItemHandler instance, EnumFacing side) {
			return null;
		}

		@Override
		public void readNBT (Capability<IExtendedBaublesItemHandler> capability, IExtendedBaublesItemHandler instance, EnumFacing side, NBTBase nbt){ }
	}
	
	public static class CapabilityItemBaubleStorage implements IStorage<IExtendedBauble> {

		@Override
		public NBTBase writeNBT (Capability<IExtendedBauble> capability, IExtendedBauble instance, EnumFacing side) {
			return null;
		}

		@Override
		public void readNBT (Capability<IExtendedBauble> capability, IExtendedBauble instance, EnumFacing side, NBTBase nbt) {

		}
	}

	public static class CapabilityPlayerPlayTime implements IStorage<IPlaytimeCounter> {

		@Override
		public NBTBase writeNBT (Capability<IPlaytimeCounter> capability, IPlaytimeCounter instance, EnumFacing side) {
			return null;
		}

		@Override
		public void readNBT (Capability<IPlaytimeCounter> capability, IPlaytimeCounter instance, EnumFacing side, NBTBase nbt) {

		}
	}
}
