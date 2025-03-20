package keletu.enigmaticlegacy.api.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class EnigmaticCapabilities {

    @CapabilityInject(IPlaytimeCounter.class)
    public static Capability<IPlaytimeCounter> PLAYTIME_COUNTER;

    @CapabilityInject(IForbiddenConsumed.class)
    public static Capability<IForbiddenConsumed> FORBIDDEN_FRUIT_CONSUME;

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

    public static class CapabilityForbiddenConsumed implements IStorage<IForbiddenConsumed> {

        @Override
        public NBTBase writeNBT(Capability<IForbiddenConsumed> capability, IForbiddenConsumed instance, EnumFacing side) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("isForbiddenConsumed", instance.isConsumed());
            tag.setInteger("spellstoneCooldown", instance.getSpellstoneCooldown());
            return tag;
        }

        @Override
        public void readNBT(Capability<IForbiddenConsumed> capability, IForbiddenConsumed instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound tag = (NBTTagCompound) nbt;
            instance.setConsumed(tag.getBoolean("isForbiddenConsumed"));
            instance.setSpellstoneCooldown(tag.getInteger("spellstoneCooldown"));
        }
    }
}
