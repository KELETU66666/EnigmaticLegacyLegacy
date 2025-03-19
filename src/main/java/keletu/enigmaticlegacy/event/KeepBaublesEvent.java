package keletu.enigmaticlegacy.event;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.getPersistentBoolean;
import keletu.enigmaticlegacy.util.interfaces.IKeptBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber
public class KeepBaublesEvent {

    private static Map<UUID, ItemStack> baublesMap = new HashMap<>();
    private static Map<UUID, Integer> slotMap = new HashMap<>();
    private static Map<UUID, NonNullList<ItemStack>> worthyMap = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer oldPlayer = event.getOriginal();
        if (getPersistentBoolean(oldPlayer, "dropEldritchAmulet", true)) {
            KeepBaublesEvent.setBaubleStackAll(event.getEntityPlayer(), KeepBaublesEvent.getBaubleStackAll(oldPlayer));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void playerDeathHigh(PlayerDropsEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        baublesMap.remove(player.getUniqueID());
        slotMap.remove(player.getUniqueID());
        worthyMap.remove(player.getUniqueID());
        ItemStack stack = getBaubleStacks(player);
        int slot = getSlotShouldKept(player);
        if (getPersistentBoolean(player, "dropEldritchAmulet", true)) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            NonNullList<ItemStack> list = NonNullList.create();
            for (int i = 0; i < baubles.getSlots(); ++i) {
                list.add(i, baubles.getStackInSlot(i));
                baubles.setStackInSlot(i, ItemStack.EMPTY);
            }
            worthyMap.put(player.getUniqueID(), list);
        } else if (stack.getItem() instanceof IKeptBauble) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            handler.setStackInSlot(slot, ItemStack.EMPTY);
            baublesMap.put(player.getUniqueID(), stack);
            slotMap.put(player.getUniqueID(), slot);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerDeathLow(PlayerDropsEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (worthyMap.containsKey(player.getUniqueID())) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); ++i) {
                baubles.setStackInSlot(i, worthyMap.get(player.getUniqueID()).get(i));
            }
            worthyMap.remove(player.getUniqueID());
        } else if (baublesMap.containsKey(player.getUniqueID()) && slotMap.containsKey(player.getUniqueID())) {
            if (!(EnigmaticEvents.dropCursedStone)) {
                IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
                handler.setStackInSlot(slotMap.get(player.getUniqueID()), baublesMap.get(player.getUniqueID()));
            } else {
                EnigmaticEvents.dropCursedStone = false;
            }
            baublesMap.remove(player.getUniqueID());
            slotMap.remove(player.getUniqueID());
        }
    }

    public static int getSlotShouldKept(EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        for (int x = 0; x < handler.getSlots(); x++) {
            ItemStack stack = handler.getStackInSlot(BaubleType.TRINKET.getValidSlots()[x]);
            if (!stack.isEmpty() && stack.getItem() instanceof IKeptBauble) {
                return x;
            }
        }
        return -1;
    }

    public static ItemStack getBaubleStack(EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        for (int x = 0; x < handler.getSlots(); x++) {
            ItemStack stack = handler.getStackInSlot(BaubleType.TRINKET.getValidSlots()[x]);
            if (!stack.isEmpty() && stack.getItem() instanceof IKeptBauble) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getBaubleStacks(EntityPlayer player) {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        backpack.set(KeepBaublesEvent.getBaubleStack(player));
        return backpack.get();
    }


    public static ItemStack getBaubleStackAll(EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); ++i) {
            ItemStack stack = baubles.getStackInSlot(i);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void setBaubleStackAll(EntityPlayer player, ItemStack stack) {
        //if (stack.getItem() instanceof ItemCursedRing) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); ++i) {
            //if (baubles.isItemValidForSlot(i, stack, player)) {
            ItemStack remainder = baubles.insertItem(i, stack.copy(), true);
            if (remainder.getCount() < stack.getCount()) {
                baubles.insertItem(i, stack.copy(), false);
            }
            //}
        }
        //}
    }
}
