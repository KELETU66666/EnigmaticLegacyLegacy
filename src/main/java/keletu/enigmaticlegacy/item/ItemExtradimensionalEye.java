package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.util.helper.ItemNBTHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemExtradimensionalEye extends ItemBase {

    public float range = 3.0F;

    public ItemExtradimensionalEye() {
        super("extradimensional_eye", EnumRarity.UNCOMMON);
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEye1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEye2"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEye3"));
            list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEye4"));
            list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEye5"));
            list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEye6"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEye7"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
        if (ItemNBTHelper.verifyExistance(stack, "BoundDimension")) {

            String boundDimensionName = null;
            int dimensionID = ItemNBTHelper.getInt(stack, "BoundDimension", 0);

            if (dimensionID == 0) {
                boundDimensionName = "tooltip.enigmaticlegacy.overworld";
            } else if (dimensionID == -1) {
                boundDimensionName = "tooltip.enigmaticlegacy.nether";
            } else if (dimensionID == 1) {
                boundDimensionName = "tooltip.enigmaticlegacy.end";
            }

            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEyeLocation"));
            list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEyeX") + TextFormatting.GOLD + ItemNBTHelper.getInt(stack, "BoundX", 0));
            list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEyeY") + TextFormatting.GOLD + ItemNBTHelper.getInt(stack, "BoundY", 0));
            list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEyeZ") + TextFormatting.GOLD + ItemNBTHelper.getInt(stack, "BoundZ", 0));
            //if (boundDimensionName != null) {
            //    list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEyeDimension") + new TextComponentTranslation(boundDimensionName));
            //} else {
            //    list.add(I18n.format("tooltip.enigmaticlegacy.extradimensionalEyeDimension") + TextFormatting.GOLD + dimensionID);
            //}
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (playerIn.isSneaking() && ItemNBTHelper.getString(itemstack, "BoundDimension", null) == null) {
            ItemNBTHelper.setDouble(itemstack, "BoundX", playerIn.posX);
            ItemNBTHelper.setDouble(itemstack, "BoundY", playerIn.posY);
            ItemNBTHelper.setDouble(itemstack, "BoundZ", playerIn.posZ);

            ItemNBTHelper.setInt(itemstack, "BoundDimension", playerIn.world.provider.getDimension());
            playerIn.swingArm(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
        }

        return new ActionResult<>(EnumActionResult.FAIL, itemstack);
    }

}