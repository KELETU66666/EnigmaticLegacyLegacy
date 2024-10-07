package keletu.enigmaticlegacy.event;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.api.cap.EnigmaticCapabilities;
import keletu.enigmaticlegacy.api.cap.IPlaytimeCounter;
import keletu.enigmaticlegacy.api.cap.PlayerPlaytimeCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerEntity {

	@SubscribeEvent
	public void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(EnigmaticLegacy.MODID,"playtime_counter"), new PlayerPlaytimeCounter.Provider((EntityPlayer) event.getObject()));
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
		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		if (event.isWasDeath()) {
			EntityPlayer original = event.getOriginal();
			EntityPlayer player = event.getEntityPlayer();
			IPlaytimeCounter oldCounter = original.getCapability(EnigmaticCapabilities.PLAYTIME_COUNTER, null);
			IPlaytimeCounter newCounter = player.getCapability(EnigmaticCapabilities.PLAYTIME_COUNTER, null);
			if (oldCounter != null && newCounter != null) {
				NBTTagCompound data = original.getEntityData().getCompoundTag("PlaytimeData");
				newCounter.deserializeNBT(data);
			}
		}
	}
}