package keletu.enigmaticlegacy.event;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import keletu.enigmaticlegacy.item.ItemCursedRing;
import keletu.enigmaticlegacy.item.ItemDesolationRing;
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
public class KeepBaubles {
    private static Map<UUID, NonNullList<ItemStack>> baublesMap = new HashMap<>();
    private static Map<UUID, NonNullList<ItemStack>> worthyMap = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer oldPlayer = event.getOriginal();
        if (ELEvents.dropEldritchAmulet) {
            KeepBaubles.setBaubleStackAll(event.getEntityPlayer(), KeepBaubles.getBaubleStackAll(oldPlayer));
        }
        KeepBaubles.setBaubleStack(event.getEntityPlayer(), KeepBaubles.getBaubleStack(oldPlayer));

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void playerDeathHigh(PlayerDropsEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        baublesMap.remove(player.getUniqueID());
        worthyMap.remove(player.getUniqueID());
        ItemStack stack = getBaubleStacks(player);
        if (ELEvents.dropEldritchAmulet) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            NonNullList<ItemStack> list = NonNullList.create();
            for (int i = 0; i < baubles.getSlots(); ++i) {
                list.add(i, baubles.getStackInSlot(i));
                baubles.setStackInSlot(i, ItemStack.EMPTY);
            }
            worthyMap.put(player.getUniqueID(), list);
        }
        if (stack.getItem() instanceof ItemCursedRing || stack.getItem() instanceof ItemDesolationRing) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            NonNullList<ItemStack> list = NonNullList.create();

            for (int i = 0; i < BaubleType.RING.getValidSlots().length; i++) {
                list.add(i, handler.getStackInSlot(BaubleType.RING.getValidSlots()[i]));
                handler.setStackInSlot(BaubleType.RING.getValidSlots()[i], ItemStack.EMPTY);
            }
            baublesMap.put(player.getUniqueID(), list);
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
        }
        if (baublesMap.containsKey(player.getUniqueID())) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);

            for (int i = 0; i < BaubleType.RING.getValidSlots().length; i++) {
                handler.setStackInSlot(BaubleType.RING.getValidSlots()[i], baublesMap.get(player.getUniqueID()).get(i));
            }
            baublesMap.remove(player.getUniqueID());
        }
    }

    public static ItemStack getBaubleStack(EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        for (int index : BaubleType.RING.getValidSlots()) {
            ItemStack stack = handler.getStackInSlot(BaubleType.RING.getValidSlots()[index]);
            if (stack.getItem() instanceof ItemCursedRing || stack.getItem() instanceof ItemDesolationRing) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
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

    public static void setBaubleStack(EntityPlayer player, ItemStack stack) {
        if (stack.getItem() instanceof ItemCursedRing || stack.getItem() instanceof ItemDesolationRing) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < BaubleType.RING.getValidSlots().length; i++) {
                if (baubles.isItemValidForSlot(BaubleType.RING.getValidSlots()[i], stack, player)) {
                    ItemStack remainder = baubles.insertItem(BaubleType.RING.getValidSlots()[i], stack.copy(), true);
                    if (remainder.getCount() < stack.getCount()) {
                        baubles.insertItem(BaubleType.RING.getValidSlots()[i], stack.copy(), false);
                    }
                }
            }
        }
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

    public static ItemStack getBaubleStacks(EntityPlayer player) {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        backpack.set(KeepBaubles.getBaubleStack(player));
        return backpack.get();
    }
}