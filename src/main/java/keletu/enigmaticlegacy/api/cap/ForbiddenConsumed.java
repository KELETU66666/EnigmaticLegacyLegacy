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
	public static final ResourceLocation SPELLSTONE_COOLDOWN = new ResourceLocation(EnigmaticLegacy.MODID, "spellstone_cooldown");

	private final EntityPlayer player;
	private boolean bool = false;
	private int cooldown = 0;

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

	@Override
	public int getSpellstoneCooldown() {
		return this.cooldown;
	}

	@Override
	public void setSpellstoneCooldown(int cooldown) {
		if (cooldown != this.cooldown) {
			this.updateStat(SPELLSTONE_COOLDOWN, cooldown);
		}

		this.cooldown = cooldown;
	}

	private void updateStat(ResourceLocation stat, boolean value) {
		if (this.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) this.player;
			player.addStat(new StatBase(stat.getNamespace(), new TextComponentString(stat.getPath())), value ? 1 : 0);
		}
	}

	private void updateStat(ResourceLocation stat, int value) {
		if (this.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) this.player;
			player.addStat(new StatBase(stat.getNamespace(), new TextComponentString(stat.getPath())), value);
		}
	}

	@Override
	public void decreaseCooldown() {
		this.updateStat(SPELLSTONE_COOLDOWN, cooldown--);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("isForbiddenConsumed", this.bool);
		tag.setInteger("spellstoneCooldown", this.cooldown);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		this.bool = tag.getBoolean("isForbiddenConsumed");
		this.cooldown = tag.getInteger("spellstoneCooldown");
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