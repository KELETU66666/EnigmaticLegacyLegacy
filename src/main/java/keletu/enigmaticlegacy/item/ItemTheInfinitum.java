package keletu.enigmaticlegacy.item;

import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import static keletu.enigmaticlegacy.EnigmaticConfigs.*;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemTheInfinitum extends ItemBaseFireProof {
    private static final ResourceLocation bookID = new ResourceLocation(EnigmaticLegacy.MODID, "the_acknowledgment");

    public ItemTheInfinitum() {
        super("the_infinitum", EnumRarity.EPIC);
        this.maxStackSize = 1;
    }

    public static boolean isOpen() {
        return bookID.equals(PatchouliAPI.instance.getOpenBookGui());
    }

    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", infinitumAttackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", EnigmaticConfigs.infinitumAttackSpeed, 0));
        }

        return multimap;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer attacker, Entity target) {
        if (attacker instanceof EntityPlayerMP && SuperpositionHandler.isTheWorthyOne(attacker) && target instanceof EntityLivingBase) {
            EntityLivingBase livingBase = (EntityLivingBase) target;
            livingBase.addPotionEffect(new PotionEffect(MobEffects.WITHER, 160, 3, false, true));
            livingBase.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 500, 3, false, true));
            livingBase.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 300, 3, false, true));
            livingBase.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 3, false, true));
            livingBase.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 300, 3, false, true));
            livingBase.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 3, false, true));
        }

        return false;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 24;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (playerIn.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemShield)
            return new ActionResult<>(EnumActionResult.PASS, stack);

        if (Loader.isModLoaded("patchouli")) {
            if (playerIn instanceof EntityPlayerMP)
                PatchouliAPI.instance.openBookGUI((EntityPlayerMP) playerIn, bookID);
        } else
            return new ActionResult<>(EnumActionResult.FAIL, stack);

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return !EnchantmentHelper.getEnchantments(book).keySet().contains(Enchantments.UNBREAKING) &&
                Items.DIAMOND_SWORD.isBookEnchantable(new ItemStack(Items.DIAMOND_SWORD), book);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != Enchantments.UNBREAKING && Items.DIAMOND_SWORD.canApplyAtEnchantingTable(new ItemStack(Items.DIAMOND_SWORD), enchantment);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            if (Minecraft.getMinecraft().player != null && SuperpositionHandler.hasCursed(Minecraft.getMinecraft().player)) {
                list.add(I18n.format("tooltip.enigmaticlegacy.theInfinitum2"));
                list.add(I18n.format("tooltip.enigmaticlegacy.theInfinitum3"));
                list.add("");
            }

            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + infinitumBossDamageBonus * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.theInfinitum4"));
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + infinitumKnockbackBonus * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.theInfinitum5"));
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + infinitumLifestealBonus * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.theInfinitum6"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.theInfinitum7"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.theInfinitum8"));
            list.add("");
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.theInfinitum9") + infinitumUndeadProbability * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.theInfinitum9_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.theInfinitum10"));
            list.add("");
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.theInfinitum1"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
            list.add("");
        }

        if (stack.isItemEnchanted()) {
            list.add("");
        }
    }
}