package keletu.enigmaticlegacy.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import static keletu.enigmaticlegacy.ELConfigs.*;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import static keletu.enigmaticlegacy.EnigmaticLegacy.tabEnigmaticLegacy;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import static net.minecraftforge.common.util.Constants.NBT.TAG_LIST;
import static net.minecraftforge.common.util.Constants.NBT.TAG_STRING;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ItemEldritchPan extends ItemShield {
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("2d5cac0e-598f-475b-97cb-c7ab4741d0f5");

    public static final Map<EntityPlayer, Integer> HOLDING_DURATIONS = new WeakHashMap<>();
    //private final float panAttackDamage, panAttackSpeed, panArmorValue;

    public ItemEldritchPan() {
        this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "eldritch_pan"));
        this.setTranslationKey("eldritch_pan");
        this.setMaxDamage(4000);
        this.setCreativeTab(tabEnigmaticLegacy);

        this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, ItemArmor.DISPENSER_BEHAVIOR);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.format("item.eldritch_pan.name");
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        if (slot != EntityEquipmentSlot.MAINHAND && slot != EntityEquipmentSlot.OFFHAND)
            return super.getItemAttributeModifiers(slot);

        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);

        if (slot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", panAttackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", panAttackSpeed, 0));
            multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIER_UUID, "Weapon modifier", panArmorValue, 0));
        } else {
            multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIER_UUID, "Weapon modifier", panArmorValue, 0));
        }

        return multimap;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        if (slot != EntityEquipmentSlot.MAINHAND && slot != EntityEquipmentSlot.OFFHAND)
            return super.getItemAttributeModifiers(slot);

        int kills = getKillCount(stack);

        if (kills <= 0)
            return super.getAttributeModifiers(slot, stack);

        double armor = panArmorValue + (panUniqueArmorGain * kills);
        ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIER_UUID, "Weapon modifier",
                armor, 0));

        if (slot != EntityEquipmentSlot.MAINHAND)
            return builder.build();

        double damage = panAttackDamage + (panUniqueDamageGain * kills);

        builder.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier",
                panAttackSpeed, 0));
        builder.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier",
                damage, 0));

        return builder.build();
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (attacker.world.rand.nextDouble() < 0.0001) {
            attacker.world.playSound(null, attacker.posX, attacker.posY, attacker.posZ,
                    /*EnigmaticSounds.PAN_CLANG_FR*/SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.PLAYERS, 1F,
                    attacker.world.rand.nextFloat() * 0.2F + 0.8F);
        } else {
            attacker.world.playSound(null, attacker.posX, attacker.posY, attacker.posZ,
                    /*EnigmaticSounds.PAN_CLANG*/SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 0.5F,
                    attacker.world.rand.nextFloat() * 0.1F + 0.9F);
        }

        stack.damageItem(1, attacker);

        return true;
    }

    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (state.getBlockHardness(world, pos) != 0.0F) {
            stack.damageItem(2, entityLiving);
        }

        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // NO-OP
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            String life = "+" + Math.round(100 * panLifeSteal) + "%";
            String hunger = "+" + this.toString(panHungerSteal);
            String damageGain = "+" + this.toString(panUniqueDamageGain);
            String armorGain = "+" + this.toString(panUniqueArmorGain);

            //boolean noHunger = SuperpositionHandler.cannotHunger((EnigmaticLegacy.PROXY.getClientPlayer()));

            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan1"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan2"));
            tooltip.add("");
            tooltip.add(TextFormatting.GOLD + life + I18n.format("tooltip.enigmaticlegacy.eldritchPan3"));

            //  if (!noHunger) {
            tooltip.add(TextFormatting.GOLD + hunger + I18n.format("tooltip.enigmaticlegacy.eldritchPan4"));
            //  } else {
            //     tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan4_alt") + TextFormatting.GOLD + hunger);
            // }

            tooltip.add("");
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan5"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan6"));
            tooltip.add("");

            //if (!noHunger) {
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan7"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan8"));
            //} else {
            //    tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan7_alt"));
            //    tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan8_alt"));
            //    tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan8p_alt"));
            //}
            tooltip.add("");
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan9") + TextFormatting.GOLD + damageGain);
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPan10") + TextFormatting.GOLD + armorGain + I18n.format("tooltip.enigmaticlegacy.eldritchPan10_1"));
            tooltip.add("");
        } else {
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPanLore1"));
            tooltip.add("");
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
            tooltip.add("");
            this.writeKillCount(tooltip, stack);
            tooltip.add("");
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

   /* @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        if (hand == EnumHand.MAIN_HAND) {
            ItemStack offhandStack = player.getHeldItemOffhand();

            if (offhandStack.getItem().getItemUseAction(offhandStack) == EnumAction.BLOCK)
                return EnumActionResult.PASS;
        }

        if (SuperpositionHandler.hasCursed(player)) {
            player.swingArm(hand);
            return EnumActionResult.SUCCESS;
        } else
            return EnumActionResult.PASS;
    }*/

   /* @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        // TODO: make it consume projectiles for hunger
        return ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }*/

    private void writeKillCount(List<String> tooltip, ItemStack pan) {
        int kills = getKillCount(pan);

        tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPanKills1") + TextFormatting.GOLD + kills);

        if (kills >= panUniqueGainLimit) {
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.eldritchPanKillsMax"));
        }
    }

    private String toString(double value) {
        if (Math.floor(value) == value) {
            return Integer.toString((int) value);
        } else {
            return Double.toString(value);
        }
    }

    public static int getKillCount(ItemStack pan) {
        NBTTagCompound tag = pan.getTagCompound();

        if (tag == null || !tag.hasKey("PanUniqueKills", TAG_LIST))
            return 0;

        NBTTagList list = tag.getTagList("PanUniqueKills", TAG_STRING);

        return list.tagCount();
    }

    public static List<NBTBase> convertNBTTagListToList(NBTTagList nbtTagList) {
        List<NBTBase> list = new ArrayList<>();

        for (int i = 0; i < nbtTagList.tagCount(); i++) {

            list.add(nbtTagList.get(i));
        }

        return list;
    }

    public static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    public static List<ResourceLocation> getUniqueKills(ItemStack pan) {
        NBTTagCompound tag = pan.getTagCompound();

        if (tag == null || !tag.hasKey("PanUniqueKills", TAG_LIST))
            return Collections.emptyList();

        NBTTagList list = tag.getTagList("PanUniqueKills", TAG_STRING);
        List<NBTBase> tag1 = convertNBTTagListToList(list);
        return tag1.stream().map(e -> new ResourceLocation(((NBTTagString) e).getString())).collect(Collectors.toList());
    }

    public static void setUniqueKills(ItemStack pan, List<ResourceLocation> mobs) {
        NBTTagCompound tag = pan.getTagCompound();
        if (tag == null)
            tag = new NBTTagCompound();

        NBTTagList list = new NBTTagList();
        mobs.forEach(entity -> list.set(list.tagCount(), new NBTTagString(entity.toString())));
        tag.setTag("PanUniqueKills", list);

        pan.setTagCompound(tag);
    }

    //todo Warn
    public static void addUniqueKill(ItemStack pan, ResourceLocation mob) {
        NBTTagCompound tag = getOrCreateTag(pan);

        NBTTagList list;

        if (!tag.hasKey("PanUniqueKills", TAG_LIST)) {
            list = new NBTTagList();
        } else {
            list = tag.getTagList("PanUniqueKills", TAG_STRING);
        }

        List<NBTBase> lst = new ArrayList<>();
        lst.add(new NBTTagString(mob.getPath()));

        for (NBTBase nbtBase : lst) {
            list.appendTag(nbtBase);
        }

        tag.setTag("PanUniqueKills", list);
        pan.setTagCompound(tag);
    }

    public static boolean addKillIfNotPresent(ItemStack pan, ResourceLocation mob) {
        List<ResourceLocation> kills = getUniqueKills(pan);

        if (kills.size() < 100 && !kills.contains(mob)) {
            addUniqueKill(pan, mob);
            return true;
        }

        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == Items.BEEF || repair.getItem() == Items.PORKCHOP || repair.getItem() == Items.ROTTEN_FLESH || repair.getItem() == Items.APPLE || repair.getItem() == Items.GOLDEN_APPLE || repair.getItem() == Items.POISONOUS_POTATO || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 24;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return !EnchantmentHelper.getEnchantments(book).keySet().contains(Enchantments.UNBREAKING) &&
                Items.DIAMOND_SWORD.isBookEnchantable(new ItemStack(Items.DIAMOND_SWORD), book);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != Enchantments.UNBREAKING && Items.DIAMOND_SWORD.canApplyAtEnchantingTable(new ItemStack(Items.DIAMOND_SWORD), enchantment);
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
}