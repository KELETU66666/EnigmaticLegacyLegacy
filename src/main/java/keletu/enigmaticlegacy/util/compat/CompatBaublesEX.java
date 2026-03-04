package keletu.enigmaticlegacy.util.compat;

import baubles.api.BaubleTypeEx;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import keletu.enigmaticlegacy.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CompatBaublesEX {

    public static Item ichorBottle = new ItemIchorBottle();
    public static Item astralFruit = new ItemAstralFruit();

    public static int LayerAmuletItems(BaubleTypeEx type, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getTypeInSlot(i) != type) continue;
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.getItem() instanceof ItemEnigmaticAmulet) return i;
            if (stack.getItem() instanceof ItemEldritchAmulet) return i;
            if (stack.getItem() instanceof ItemAscensionAmulet) return i;
        }
        return -1;
    }

    public static int LayerCharmItems(BaubleTypeEx type, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getTypeInSlot(i) != type) continue;
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.getItem() instanceof ItemMonsterCharm) return i;
            if (stack.getItem() instanceof ItemMiningCharm) return i;
            if (stack.getItem() instanceof ItemBerserkEmblem) return i;
            if (stack.getItem() instanceof ItemEnigmaticEye) return i;
        }
        return -1;
    }
}