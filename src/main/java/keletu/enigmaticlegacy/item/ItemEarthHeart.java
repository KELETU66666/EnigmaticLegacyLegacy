package keletu.enigmaticlegacy.item;

import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasBlessed;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasCursed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemEarthHeart extends ItemBase {

    public ItemEarthHeart() {
        super("earth_heart", EnumRarity.UNCOMMON);
        this.setHasSubtypes(true);
        this.maxStackSize = 1;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof EntityPlayer && !entityIn.world.isRemote) {
            if (hasCursed((EntityPlayer) entityIn) || hasBlessed((EntityPlayer) entityIn))
                this.setDamage(stack, 1);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (this.getDamage(stack) != 0) {
            if (Minecraft.getMinecraft().player != null) {
                if (hasBlessed(Minecraft.getMinecraft().player))
                    tooltip.add(I18n.format("tooltip.enigmaticlegacy.blessed1"));
                else
                    tooltip.add(I18n.format("tooltip.enigmaticlegacy.tainted1"));
            }
        }
    }
}