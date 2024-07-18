package keletu.enigmaticlegacy.event;

import baubles.common.Baubles;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.api.ExtendedBaublesApi;
import keletu.enigmaticlegacy.api.IExtendedBauble;
import keletu.enigmaticlegacy.api.cap.*;
import keletu.enigmaticlegacy.packet.PacketSyncExtended;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class EventHandlerEntity {

	private final HashMap<UUID,ItemStack[]> ExtendedBaublesSync = new HashMap<UUID,ItemStack[]>();

	@SubscribeEvent
	public void cloneCapabilitiesEvent(PlayerEvent.Clone event)
	{
		try {
			ExtendedBaublesContainer bco = (ExtendedBaublesContainer) ExtendedBaublesApi.getBaublesHandler(event.getOriginal());
			NBTTagCompound nbt = bco.serializeNBT();
			ExtendedBaublesContainer bcn = (ExtendedBaublesContainer) ExtendedBaublesApi.getBaublesHandler(event.getEntityPlayer());
			bcn.deserializeNBT(nbt);
		} catch (Exception e) {
			Baubles.log.error("Could not clone player ["+event.getOriginal().getName()+"] ExtendedBaubles when changing dimensions");
		}
	}

	@SubscribeEvent
	public void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(EnigmaticLegacy.MODID,"container"), new ExtendedBaublesContainerProvider(new ExtendedBaublesContainer()));
			event.addCapability(new ResourceLocation(EnigmaticLegacy.MODID,"playtime_counter"), new PlayerPlaytimeCounter.Provider(new PlayerPlaytimeCounter((EntityPlayer) event.getObject())));
		}
	}

	@SubscribeEvent
	public void playerJoin(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			syncSlots(player, Collections.singletonList(player));
		}
	}

	@SubscribeEvent
	public void onStartTracking(PlayerEvent.StartTracking event) {
		Entity target = event.getTarget();
		if (target instanceof EntityPlayerMP) {
			syncSlots((EntityPlayer) target, Collections.singletonList(event.getEntityPlayer()));
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		ExtendedBaublesSync.remove(event.player.getUniqueID());
	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		// player events
		if (event.phase == TickEvent.Phase.END) {
			EntityPlayer player = event.player;
			IExtendedBaublesItemHandler ExtendedBaubles = ExtendedBaublesApi.getBaublesHandler(player);
			for (int i = 0; i < ExtendedBaubles.getSlots(); i++) {
				ItemStack stack = ExtendedBaubles.getStackInSlot(i);
				IExtendedBauble ExtendedBauble = stack.getCapability(EnigmaticCapabilities.CAPABILITY_ITEM_BAUBLE, null);
				if (ExtendedBauble != null) {
					ExtendedBauble.onWornTick(stack, player);
				}
			}
			if (!player.world.isRemote) {
				syncExtendedBaubles(player, ExtendedBaubles);
			}
		}
	}

	private void syncExtendedBaubles(EntityPlayer player, IExtendedBaublesItemHandler ExtendedBaubles) {
		ItemStack[] items = ExtendedBaublesSync.get(player.getUniqueID());
		if (items == null) {
			items = new ItemStack[ExtendedBaubles.getSlots()];
			Arrays.fill(items, ItemStack.EMPTY);
			ExtendedBaublesSync.put(player.getUniqueID(), items);
		}
		if (items.length != ExtendedBaubles.getSlots()) {
			ItemStack[] old = items;
			items = new ItemStack[ExtendedBaubles.getSlots()];
			System.arraycopy(old, 0, items, 0, Math.min(old.length, items.length));
			ExtendedBaublesSync.put(player.getUniqueID(), items);
		}
		Set<EntityPlayer> receivers = null;
		for (int i = 0; i < ExtendedBaubles.getSlots(); i++) {
			ItemStack stack = ExtendedBaubles.getStackInSlot(i);
			IExtendedBauble ExtendedBauble = stack.getCapability(EnigmaticCapabilities.CAPABILITY_ITEM_BAUBLE, null);
			if (ExtendedBaubles.isChanged(i) || ExtendedBauble != null && ExtendedBauble.willAutoSync(stack, player) && !ItemStack.areItemStacksEqual(stack, items[i])) {
				if (receivers == null) {
					receivers = new HashSet<>(((WorldServer) player.world).getEntityTracker().getTrackingPlayers(player));
					receivers.add(player);
				}
				syncSlot(player, i, stack, receivers);
				ExtendedBaubles.setChanged(i,false);
				items[i] = stack == null ? ItemStack.EMPTY : stack.copy();
			}
		}
	}

	private void syncSlots(EntityPlayer player, Collection<? extends EntityPlayer> receivers) {
		IExtendedBaublesItemHandler ExtendedBaubles = ExtendedBaublesApi.getBaublesHandler(player);
		for (int i = 0; i < ExtendedBaubles.getSlots(); i++) {
			syncSlot(player, i, ExtendedBaubles.getStackInSlot(i), receivers);
		}
	}

	private void syncSlot(EntityPlayer player, int slot, ItemStack stack, Collection<? extends EntityPlayer> receivers) {
		PacketSyncExtended pkt = new PacketSyncExtended(player, slot, stack);
		for (EntityPlayer receiver : receivers) {
			EnigmaticLegacy.packetInstance.sendTo(pkt, (EntityPlayerMP) receiver);
		}
	}

	@SubscribeEvent
	public void playerDeath(PlayerDropsEvent event) {
		if (event.getEntity() instanceof EntityPlayer
				&& !event.getEntity().world.isRemote
				&& !event.getEntity().world.getGameRules().getBoolean("keepInventory")) {
			dropItemsAt(event.getEntityPlayer(),event.getDrops(),event.getEntityPlayer());
		}
	}

	public void dropItemsAt(EntityPlayer player, List<EntityItem> drops, Entity e) {
		IExtendedBaublesItemHandler ExtendedBaubles = ExtendedBaublesApi.getBaublesHandler(player);
		for (int i = 0; i < ExtendedBaubles.getSlots(); ++i) {
			if (ExtendedBaubles.getStackInSlot(i) != null && !ExtendedBaubles.getStackInSlot(i).isEmpty()) {
				EntityItem ei = new EntityItem(e.world,
						e.posX, e.posY + e.getEyeHeight(), e.posZ,
						ExtendedBaubles.getStackInSlot(i).copy());
				ei.setPickupDelay(40);
				float f1 = e.world.rand.nextFloat() * 0.5F;
				float f2 = e.world.rand.nextFloat() * (float) Math.PI * 2.0F;
				ei.motionX = -MathHelper.sin(f2) * f1;
				ei.motionZ = MathHelper.cos(f2) * f1;
				ei.motionY = 0.20000000298023224D;
				drops.add(ei);
				ExtendedBaubles.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}
}