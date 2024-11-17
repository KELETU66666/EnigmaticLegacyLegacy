package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import keletu.enigmaticlegacy.util.Quote;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemEnigmaticEye extends ItemBaseBauble {

    public ItemEnigmaticEye() {
        super("enigmatic_eye", EnumRarity.EPIC);
        this.hasSubtypes = true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        return stack.getMetadata() == 0 ? I18n.format("item.enigmatic_eye_dormant.name") : I18n.format("item.enigmatic_eye_active.name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            if (stack.getMetadata() == 0) {
                list.add(I18n.format("tooltip.enigmaticlegacy.enigmaticEye1"));
                list.add(I18n.format("tooltip.enigmaticlegacy.enigmaticEye2"));
                list.add(I18n.format("tooltip.enigmaticlegacy.enigmaticEye3"));
            } else {
                list.add(I18n.format("tooltip.enigmaticlegacy.enigmaticEyeAwakened1"));
                list.add(I18n.format("tooltip.enigmaticlegacy.enigmaticEyeAwakened2"));
                list.add(I18n.format("tooltip.enigmaticlegacy.enigmaticEyeAwakened3"));
                list.add(I18n.format("tooltip.enigmaticlegacy.enigmaticEyeAwakened4"));
                list.add(I18n.format("tooltip.enigmaticlegacy.enigmaticEyeAwakened5"));
                list.add("");
                list.add(I18n.format("tooltip.enigmaticlegacy.enigmaticEyeAwakened6"));
            }
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return super.canEquip(itemstack, player) && itemstack.getMetadata() != 0;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase living) {
        super.onWornTick(stack, living);
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase living) {
        super.onUnequipped(stack, living);
        if(!BaublesApi.getBaubles((EntityPlayer) living).getStackInSlot(9).isEmpty()){
            ((EntityPlayer) living).dropItem(BaublesApi.getBaubles((EntityPlayer) living).getStackInSlot(9), false);
            BaublesApi.getBaubles((EntityPlayer) living).setInventorySlotContents(9, ItemStack.EMPTY);
        }
    }

    public void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        if (stack.getMetadata() != 0)
            attributes.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(UUID.fromString("08c3c83d-7137-4b42-880f-b146bcb64d2e"), "Reach bonus", 3, 0).setSaved(false));
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.CHARM;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        EntityItemIndestructible item = new EntityItemIndestructible(world, location.posX, location.posY, location.posZ, stack);
        item.setDefaultPickupDelay();
        item.motionX = location.motionX;
        item.motionY = location.motionY;
        item.motionZ = location.motionZ;
        if (location instanceof EntityItem) {
            item.setThrower(((EntityItem) location).getThrower());
            item.setOwner(((EntityItem) location).getOwner());
        }

        return item;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {

        ItemStack stack = player.getHeldItem(handIn);

        if (stack.getItemDamage() == 0) {
            stack.setItemDamage(1);
            if (player instanceof EntityPlayerMP)
                Quote.getRandom(Quote.NARRATOR_INTROS).playWithoutLimit((EntityPlayerMP) player, 60);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else
            return new ActionResult<>(EnumActionResult.FAIL, stack);
    }
}