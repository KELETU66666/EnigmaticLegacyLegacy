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
    @CapabilityInject(IRelicsItemHandler.class)
    public static Capability<IRelicsItemHandler> CAPABILITY_BAUBLES;
    @CapabilityInject(IEnigmaticRelic.class)
    public static Capability<IEnigmaticRelic> CAPABILITY_ITEM_RELIC;

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

    public static class CapabilityRelics<T extends IRelicsItemHandler> implements IStorage<IRelicsItemHandler> {

        @Override
        public NBTBase writeNBT(Capability<IRelicsItemHandler> capability, IRelicsItemHandler instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IRelicsItemHandler> capability, IRelicsItemHandler instance, EnumFacing side, NBTBase nbt) {
        }
    }

    public static class CapabilityItemRelicStorage implements IStorage<IEnigmaticRelic> {

        @Override
        public NBTBase writeNBT(Capability<IEnigmaticRelic> capability, IEnigmaticRelic instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IEnigmaticRelic> capability, IEnigmaticRelic instance, EnumFacing side, NBTBase nbt) {

        }
    }
}
