package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.core.ItemNBTHelper;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemEldritchAmulet extends ItemBaseBauble {

    public ItemEldritchAmulet() {
        super("eldritch_amulet", EnumRarity.EPIC);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String name = ItemNBTHelper.getString(stack, "Inscription", null);

        tooltip.add("");

        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchAmulet1"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchAmulet2"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchAmulet3"));
            tooltip.add("");
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchAmulet4"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchAmulet5"));
            tooltip.add("");
            //ItemLoreHelper.indicateWorthyOnesOnly(list);
        } else {
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));

            if (name != null) {
                tooltip.add("");
                tooltip.add(I18n.format("tooltip.enigmaticlegacy.enigmaticAmuletInscription") + TextFormatting.DARK_RED + name);
            }
            tooltip.add("");
            //ItemLoreHelper.indicateCursedOnesOnly(list);
        }


        tooltip.add("");
    }

    @Override
    public void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        attributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(UUID.fromString("f5bb82c7-0332-4adf-a414-2e4f03471983"), EnigmaticLegacy.MODID + ":attack_bonus", 3.0, 0));
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.AMULET;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        super.onWornTick(stack, player);
        if (player instanceof EntityPlayerMP && player.ticksExisted % 5 == 0 && SuperpositionHandler.isTheWorthyOne((EntityPlayer) player)) {
            Entity ent = SuperpositionHandler.getPointedEntity(player.world, player, 0.0F, 128D, 3F, false);

            if (ent instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) ent;
                if (entity instanceof EntityPlayerMP && BaublesApi.isBaubleEquipped((EntityPlayer) entity, this) != -1 && SuperpositionHandler.isTheWorthyOne((EntityPlayer) entity)) {
                    return;
                }

                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 10, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 10, 1));
            }
        }
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        if (player instanceof EntityPlayer)
            return /*SuperpositionHandler.isTheWorthyOne((EntityPlayer) player) && */super.canEquip(stack, player);
        else
            return false;
    }

    private Map<String, NonNullList<ItemStack>> inventoryMap(EntityPlayer player) {
        Map<String, NonNullList<ItemStack>> inventories = new HashMap<>();
        inventories.put("Armor", player.inventory.armorInventory);
        inventories.put("Main", player.inventory.mainInventory);
        inventories.put("Offhand", player.inventory.offHandInventory);
        return inventories;
    }

    public void storeInventory(EntityPlayerMP player) {
        Map<String, NonNullList<ItemStack>> inventories = this.inventoryMap(player);

        NBTTagCompound tag = new NBTTagCompound();

        inventories.entrySet().forEach(entry -> {
            NBTTagList list = new NBTTagList();

            for (int i = 0; i < entry.getValue().size(); i++) {
                ItemStack stack = entry.getValue().get(i);

                if (EnchantmentHelper.getEnchantments(stack).keySet().contains(Enchantments.VANISHING_CURSE)) {
                    stack = ItemStack.EMPTY;
                }

                list.appendTag(stack.serializeNBT());
                entry.getValue().set(i, ItemStack.EMPTY);
            }

            tag.setTag("Inventory" + entry.getKey(), list);
        });

        SuperpositionHandler.setPersistentTag(player, "ELPersistentInventory", tag);
    }

    public boolean reclaimInventory(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer) {
        Map<String, NonNullList<ItemStack>> inventories = this.inventoryMap(newPlayer);
        NBTBase maybeTag = ItemSoulCrystal.getPersistentTag(oldPlayer, "ELPersistentInventory", null);
        boolean hadTag = false;

        if (maybeTag instanceof NBTTagCompound) {
            NBTTagCompound tag = (NBTTagCompound) maybeTag;
            ItemSoulCrystal.removePersistentTag(newPlayer, "ELPersistentInventory");
            hadTag = true;

            inventories.entrySet().forEach(entry -> {
                NBTBase maybeList = tag.getTag("Inventory" + entry.getKey());

                if (maybeList instanceof NBTTagList) {
                    NBTTagList list = (NBTTagList) maybeList;
                    for (int i = 0; i < entry.getValue().size(); i++) {
                        NBTTagCompound stackTag = list.getCompoundTagAt(i);
                        ItemStack stack = new ItemStack(stackTag);
                        entry.getValue().set(i, stack);
                    }
                }
            });
        }

        return hadTag;
    }

}