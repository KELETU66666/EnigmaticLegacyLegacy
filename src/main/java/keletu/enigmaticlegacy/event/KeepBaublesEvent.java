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

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber
public class KeepBaublesEvent {

    private static Map<UUID, List<ItemStack>> baublesMap = new HashMap<>();
    private static Map<UUID, List<Integer>> slotMap = new HashMap<>();
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
        List<ItemStack> stacks = getBaubleStacks(player);
        List<Integer> slots = getSlotShouldKept(player);
        if (getPersistentBoolean(player, "dropEldritchAmulet", true)) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            NonNullList<ItemStack> list = NonNullList.create();
            for (int i = 0; i < baubles.getSlots(); ++i) {
                list.add(i, baubles.getStackInSlot(i));
                baubles.setStackInSlot(i, ItemStack.EMPTY);
            }
            worthyMap.put(player.getUniqueID(), list);
        } else {
            for (int i = 0; i < slots.size(); i++)
                if (stacks.get(i).getItem() instanceof IKeptBauble) {
                    IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
                    handler.setStackInSlot(slots.get(i), ItemStack.EMPTY);
                    baublesMap.put(player.getUniqueID(), stacks);
                    slotMap.put(player.getUniqueID(), slots);
                }
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
            for (int i = 0; i < slotMap.get(player.getUniqueID()).size(); i++) {
                ItemStack stack = baublesMap.get(player.getUniqueID()).get(i);
                int slot = slotMap.get(player.getUniqueID()).get(i);

                if (!EnigmaticEvents.dropCursedStone) {
                    IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
                    handler.setStackInSlot(slot, stack);
                } else {
                    EnigmaticEvents.dropCursedStone = false;
                }
            }
            baublesMap.remove(player.getUniqueID());
            slotMap.remove(player.getUniqueID());
        }
    }

    public static List<Integer> getSlotShouldKept(EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        List<Integer> slots = new ArrayList<>();
        for (int x = 0; x < handler.getSlots(); x++) {
            ItemStack stack = handler.getStackInSlot(BaubleType.TRINKET.getValidSlots()[x]);
            if (!stack.isEmpty() && stack.getItem() instanceof IKeptBauble) {
                slots.add(x);
            }
        }
        return slots;
    }

    public static List<ItemStack> getBaubleStack(EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        List<ItemStack> stacks = new ArrayList<>();
        for (int x = 0; x < handler.getSlots(); x++) {
            ItemStack stack = handler.getStackInSlot(BaubleType.TRINKET.getValidSlots()[x]);
            if (!stack.isEmpty() && stack.getItem() instanceof IKeptBauble) {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    public static List<ItemStack> getBaubleStacks(EntityPlayer player) {
        AtomicReference<List<ItemStack>> backpack = new AtomicReference<>(new ArrayList<>());
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
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); ++i) {
            ItemStack remainder = baubles.insertItem(i, stack.copy(), true);
            if (remainder.getCount() < stack.getCount()) {
                baubles.insertItem(i, stack.copy(), false);
            }
        }
    }
}