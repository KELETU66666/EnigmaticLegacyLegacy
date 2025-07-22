package keletu.enigmaticlegacy.util.compat;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import keletu.enigmaticlegacy.item.ItemAstralFruit;
import keletu.enigmaticlegacy.item.ItemIchorBottle;
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
}