package keletu.enigmaticlegacy.event;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.api.cap.*;
import keletu.enigmaticlegacy.packet.PacketSyncCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventHandlerEntity {

    @SubscribeEvent
    public void tickHandler(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.END || event.player.world.isRemote) return;

        IPlaytimeCounter ipc = IPlaytimeCounter.get(event.player);
        if(ipc != null) {
            syncToClientPlayTime(event.player);
        }

        IForbiddenConsumed ifc = IForbiddenConsumed.get(event.player);
        if(ifc != null) {
            syncToClientFruitConsumed(event.player);
        }
    }

    @SubscribeEvent
    public void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(EnigmaticLegacy.MODID, "playtime_counter"), new PlayerPlaytimeCounter.Provider((EntityPlayer) event.getObject()));
            event.addCapability(new ResourceLocation(EnigmaticLegacy.MODID, "isForbidden_consumed"), new ForbiddenConsumed.Provider((EntityPlayer) event.getObject()));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.player.world.isRemote) {
            syncToClientPlayTime(event.player);
            syncToClientFruitConsumed(event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            IPlaytimeCounter counter = player.getCapability(EnigmaticCapabilities.PLAYTIME_COUNTER, null);
            if (counter != null) {
                NBTTagCompound data = counter.serializeNBT();
                player.getEntityData().setTag("PlaytimeData", data);
            }

            IForbiddenConsumed consumer = player.getCapability(EnigmaticCapabilities.FORBIDDEN_FRUIT_CONSUME, null);
            if(consumer != null){
                NBTTagCompound data = consumer.serializeNBT();
                player.getEntityData().setTag("ForbiddenConsumedData", data);

            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer original = event.getOriginal();
        EntityPlayer player = event.getEntityPlayer();

        if (player.world.isRemote) return;

        IPlaytimeCounter oldCounter = original.getCapability(EnigmaticCapabilities.PLAYTIME_COUNTER, null);
        IPlaytimeCounter newCounter = player.getCapability(EnigmaticCapabilities.PLAYTIME_COUNTER, null);
        if (oldCounter != null && newCounter != null) {
            if (event.isWasDeath()) {
                NBTTagCompound data = original.getEntityData().getCompoundTag("PlaytimeData");
                newCounter.deserializeNBT(data);
            } else {
                NBTTagCompound data = oldCounter.serializeNBT();
                newCounter.deserializeNBT(data);
            }
            syncToClientPlayTime(player);
        }

        IForbiddenConsumed oldDetector = original.getCapability(EnigmaticCapabilities.FORBIDDEN_FRUIT_CONSUME, null);
        IForbiddenConsumed newDetector = player.getCapability(EnigmaticCapabilities.FORBIDDEN_FRUIT_CONSUME, null);

        if (oldDetector != null && newDetector != null) {
            if (event.isWasDeath()) {
                NBTTagCompound data = original.getEntityData().getCompoundTag("ForbiddenConsumedData");
                newDetector.deserializeNBT(data);
            } else {
                NBTTagCompound data = oldDetector.serializeNBT();
                newDetector.deserializeNBT(data);
            }
            syncToClientFruitConsumed(player);
        }
    }

    private void syncToClientPlayTime(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            IPlaytimeCounter capability = player.getCapability(EnigmaticCapabilities.PLAYTIME_COUNTER, null);
            if (capability != null) {
                NBTTagCompound data = capability.serializeNBT();
                EnigmaticLegacy.packetInstance.sendTo(new PacketSyncCapability(data, 1), (EntityPlayerMP) player);
            }
        }
    }

    private void syncToClientFruitConsumed(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            IForbiddenConsumed capability = player.getCapability(EnigmaticCapabilities.FORBIDDEN_FRUIT_CONSUME, null);
            if (capability != null) {
                NBTTagCompound data = capability.serializeNBT();
                EnigmaticLegacy.packetInstance.sendTo(new PacketSyncCapability(data, 2), (EntityPlayerMP) player);
            }
        }
    }
}