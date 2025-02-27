package keletu.enigmaticlegacy.api.cap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class EnigmaticRelicsContainerProvider implements INBTSerializable<NBTTagCompound>, ICapabilityProvider {

	private final EnigmaticRelicsContainer container;

	public EnigmaticRelicsContainerProvider(EnigmaticRelicsContainer container) {
		this.container = container;
	}

	@Override
	public boolean hasCapability (Capability<?> capability, EnumFacing facing) {
		return capability == EnigmaticCapabilities.CAPABILITY_BAUBLES;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability (Capability<T> capability, EnumFacing facing) {
		if (capability == EnigmaticCapabilities.CAPABILITY_BAUBLES) return (T) this.container;
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT () {
		return this.container.serializeNBT();
	}

	@Override
	public void deserializeNBT (NBTTagCompound nbt) {
		this.container.deserializeNBT(nbt);
	}
}