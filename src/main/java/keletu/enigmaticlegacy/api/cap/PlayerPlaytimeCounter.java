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

public class PlayerPlaytimeCounter implements IPlaytimeCounter {
	public static final ResourceLocation TIME_WITH_CURSES_STAT = new ResourceLocation(EnigmaticLegacy.MODID, "play_time_with_seven_curses");
	public static final ResourceLocation TIME_WITHOUT_CURSES_STAT = new ResourceLocation(EnigmaticLegacy.MODID, "play_time_without_seven_curses");

	private final EntityPlayer player;
	private long timeWithCurses, timeWithoutCurses;

	public PlayerPlaytimeCounter(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public long getTimeWithoutCurses() {
		return this.timeWithoutCurses;
	}

	@Override
	public long getTimeWithCurses() {
		return this.timeWithCurses;
	}

	@Override
	public void setTimeWithoutCurses(long time) {
		if (time != this.timeWithoutCurses) {
			this.updateStat(TIME_WITHOUT_CURSES_STAT, (int) time);
		}

		this.timeWithoutCurses = time;
	}

	@Override
	public void setTimeWithCurses(long time) {
		if (time != this.timeWithCurses) {
			this.updateStat(TIME_WITH_CURSES_STAT, (int) time);
		}

		this.timeWithCurses = time;
	}

	@Override
	public void incrementTimeWithoutCurses() {
		this.updateStat(TIME_WITHOUT_CURSES_STAT, (int) ++this.timeWithoutCurses);
	}

	@Override
	public void incrementTimeWithCurses() {
		this.updateStat(TIME_WITH_CURSES_STAT, (int) ++this.timeWithCurses);
	}

	@Override
	public void matchStats() {
		this.updateStat(TIME_WITH_CURSES_STAT, (int) this.timeWithCurses);
		this.updateStat(TIME_WITHOUT_CURSES_STAT, (int) this.timeWithoutCurses);
	}

	private void updateStat(ResourceLocation stat, int value) {
		if (this.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) this.player;
			player.addStat(new StatBase(stat.getNamespace(), new TextComponentString(stat.getPath())), value);
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setLong("timeWithCurses", this.timeWithCurses);
		tag.setLong("timeWithoutCurses", this.timeWithoutCurses);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		this.timeWithCurses = tag.getLong("timeWithCurses");
		this.timeWithoutCurses = tag.getLong("timeWithoutCurses");
	}

	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
		private final PlayerPlaytimeCounter counter;

		public Provider(EntityPlayer player) {
			this(new PlayerPlaytimeCounter(player));
		}

		public Provider(PlayerPlaytimeCounter counter) {
			this.counter = counter;
		}

		@Override
		public boolean hasCapability (Capability<?> capability, EnumFacing facing) {
			return capability == EnigmaticCapabilities.PLAYTIME_COUNTER;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability (Capability<T> capability, EnumFacing facing) {
			if (capability == EnigmaticCapabilities.PLAYTIME_COUNTER) return (T) this.counter;
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT () {
			return this.counter.serializeNBT();
		}

		@Override
		public void deserializeNBT (NBTTagCompound nbt) {
			this.counter.deserializeNBT(nbt);
		}
	}

}