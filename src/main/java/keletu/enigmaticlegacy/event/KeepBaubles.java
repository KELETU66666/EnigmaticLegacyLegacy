package keletu.enigmaticlegacy.event;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.item.ItemCursedRing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber
public class KeepBaubles
{
    private static Map<UUID, ItemStack> backpackMap = new HashMap<>();


    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        EntityPlayer oldPlayer = event.getOriginal();
        KeepBaubles.setBackpackStack(event.getEntityPlayer(), KeepBaubles.getBackpackStack(oldPlayer));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void playerDeathHigh(PlayerDropsEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        backpackMap.remove(player.getUniqueID());
        ItemStack stack = EnigmaticLegacy.getBackpackStack(player);
        if(stack.getItem() instanceof ItemCursedRing)
        {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int index = BaubleType.RING.getValidSlots()[1];
            handler.setStackInSlot(index, ItemStack.EMPTY);
            backpackMap.put(player.getUniqueID(), stack);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerDeathLow(PlayerDropsEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if(backpackMap.containsKey(player.getUniqueID()))
        {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int index = BaubleType.RING.getValidSlots()[1];
            handler.setStackInSlot(index, backpackMap.get(player.getUniqueID()));
            backpackMap.remove(player.getUniqueID());
        }
    }

    public static ItemStack getBackpackStack(EntityPlayer player)
    {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        ItemStack stack = handler.getStackInSlot(BaubleType.RING.getValidSlots()[1]);
        if(!stack.isEmpty() && stack.getItem() instanceof ItemCursedRing)
        {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static void setBackpackStack(EntityPlayer player, ItemStack stack)
    {
        if(stack.getItem() instanceof ItemCursedRing)
        {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int index = BaubleType.RING.getValidSlots()[1];
            ItemStack remainder = handler.insertItem(index, stack.copy(), true);
            if(remainder.getCount() < stack.getCount())
            {
                handler.insertItem(index, stack.copy(), false);
            }
        }
    }

    public static Item getBaubleBackpackItem()
    {
        return new ItemCursedRing();
    }
}