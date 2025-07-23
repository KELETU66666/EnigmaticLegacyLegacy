package keletu.enigmaticlegacy.util.compat;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.item.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CompatBubbles {

    public static Item ichorBottle = new ItemIchorBottle();
    public static Item astralFruit = new ItemAstralFruit();

    public static void onEquippedBubbles(ItemStack stack, EntityLivingBase player) {
        if (player instanceof EntityPlayer) {
            BaublesContainer container = BaublesApi.getBaublesContainer((EntityPlayer) player);
            container.grow(BaubleType.CHARM, 1);
        }
    }

    public static void onUnequippedBubbles(ItemStack stack, EntityLivingBase living) {
        if (living instanceof EntityPlayer) {
            BaublesContainer container = BaublesApi.getBaublesContainer((EntityPlayer) living);
            container.shrink(BaubleType.CHARM, 1);
        }
    }


    public static void insetBubblesRing(EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int x = 0; x < baubles.getSlots(); x++) {
            IBaubleType type = baubles.getSlotType(x);
            if (type == BaubleType.RING) {
                if(baubles.getStackInSlot(x).isEmpty()) {
                    baubles.setStackInSlot(x, new ItemStack(EnigmaticLegacy.cursedRing));
                    break;
                }
            }
        }
    }


    public static int LayerAmuletItems(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getSlotType(i) != type) continue;
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.getItem() instanceof ItemEnigmaticAmulet) return i;
            if (stack.getItem() instanceof ItemEldritchAmulet) return i;
            if (stack.getItem() instanceof ItemAscensionAmulet) return i;
        }
        return -1;
    }

    public static int LayerCharmItems(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getSlotType(i) != type) continue;
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.getItem() instanceof ItemMonsterCharm) return i;
            if (stack.getItem() instanceof ItemMiningCharm) return i;
            if (stack.getItem() instanceof ItemBerserkEmblem) return i;
            if (stack.getItem() instanceof ItemEnigmaticEye) return i;
        }
        return -1;
    }


}