package keletu.enigmaticlegacy.api.cap;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ForbiddenConsumed implements IForbiddenConsumed {
	public static final ResourceLocation FORBIDDEN_CONSUMED = new ResourceLocation(EnigmaticLegacy.MODID, "is_forbidden_consumed");

	private final EntityPlayer player;
	private boolean bool = false;

	public ForbiddenConsumed(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public boolean isConsumed() {
		return this.bool;
	}

	@Override
	public void setConsumed(boolean bool) {
		if (bool != this.bool) {
			this.updateStat(FORBIDDEN_CONSUMED, bool);
		}

		this.bool = bool;
	}

	private void updateStat(ResourceLocation stat, boolean value) {
		if (this.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) this.player;
			player.addStat(new StatBase(stat.getNamespace(), new TextComponentString(stat.getPath())), value ? 1 : 0);
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("isForbiddenConsumed", this.bool);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		this.bool = tag.getBoolean("isForbiddenConsumed");
	}

	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
		private final IForbiddenConsumed counter;

		public Provider(EntityPlayer player) {
			this.counter = new ForbiddenConsumed(player);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == EnigmaticCapabilities.FORBIDDEN_FRUIT_CONSUME;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			return capability == EnigmaticCapabilities.FORBIDDEN_FRUIT_CONSUME ? (T) this.counter : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound) EnigmaticCapabilities.FORBIDDEN_FRUIT_CONSUME.getStorage().writeNBT(EnigmaticCapabilities.FORBIDDEN_FRUIT_CONSUME, this.counter, null);
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			EnigmaticCapabilities.FORBIDDEN_FRUIT_CONSUME.getStorage().readNBT(EnigmaticCapabilities.FORBIDDEN_FRUIT_CONSUME, this.counter, null, nbt);
		}
	}

}