package keletu.enigmaticlegacy.item;

import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.ELConfigs;
import static keletu.enigmaticlegacy.ELConfigs.bossDamageBonus;
import static keletu.enigmaticlegacy.ELConfigs.knockbackBonus;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasCursed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class ItemTheTwist extends ItemBaseFireProof {
    private static final ResourceLocation bookID = new ResourceLocation(EnigmaticLegacy.MODID, "the_acknowledgment");

    public ItemTheTwist() {
        super("the_twist", EnumRarity.EPIC);
        this.maxStackSize = 1;
    }

    public static boolean isOpen() {
        return bookID.equals(PatchouliAPI.instance.getOpenBookGui());
    }

    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", ELConfigs.twistAttackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ELConfigs.twistAttackSpeed, 0));
        }

        return multimap;
    }
    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {
        target.setFire(4);

        return super.onLeftClickEntity(stack, player, target);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 24;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if(playerIn.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemShield)
            return new ActionResult<>(EnumActionResult.PASS, stack);

        if (playerIn instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) playerIn;
            PatchouliAPI.instance.openBookGUI((EntityPlayerMP) playerIn, bookID);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        Map<Enchantment, Integer> list = EnchantmentHelper.getEnchantments(book);

        if (list.size() == 1 && list.containsKey(Enchantments.BANE_OF_ARTHROPODS))
            return true;
        else
            return super.isBookEnchantable(stack, book);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.BANE_OF_ARTHROPODS || super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        list.add(I18n.format("tooltip.enigmaticlegacy.theTwist1"));
        list.add(I18n.format("tooltip.enigmaticlegacy.theTwist2"));
        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            if (Minecraft.getMinecraft().player != null && hasCursed(Minecraft.getMinecraft().player)) {
                list.add(I18n.format("tooltip.enigmaticlegacy.theTwist4"));
                list.add(I18n.format("tooltip.enigmaticlegacy.theTwist5"));
                list.add("");
            }
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + bossDamageBonus * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.theTwist6"));
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + knockbackBonus * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.theTwist7"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        list.add("");

        try {
            //list.add(new StringTextComponent("").append(TheAcknowledgment.getEdition()).mergeStyle(TextFormatting.DARK_PURPLE));
        } catch (Exception ex) {
            // Just don't do it lol
        }
    }


}