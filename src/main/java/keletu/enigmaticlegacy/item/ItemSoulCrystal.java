package keletu.enigmaticlegacy.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.packet.PacketRecallParticles;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class ItemSoulCrystal extends Item {
	public Map<EntityPlayer, Multimap<String, AttributeModifier>> attributeDispatcher = new WeakHashMap<>();

	public ItemSoulCrystal() {
		this.maxStackSize = 1;
		this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "soul_crystal"));
		this.setTranslationKey("soul_crystal");

		this.setCreativeTab(CreativeTabs.MISC);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
		if (GuiScreen.isShiftKeyDown()) {
			list.add(I18n.format("tooltip.enigmaticlegacy.soulCrystal1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.soulCrystal2"));
		} else {
			list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	public ItemStack createCrystalFrom(EntityPlayer player) {
		int lostFragments = this.getLostCrystals(player);
		this.setLostCrystals(player, lostFragments + 1);

		return new ItemStack(this);
	}

	public boolean retrieveSoulFromCrystal(EntityPlayer player, ItemStack stack) {
		int lostFragments = this.getLostCrystals(player);

		if (lostFragments > 0) {
			this.setLostCrystals(player, lostFragments - 1);

			if (!player.world.isRemote) {
				player.world.playSound(null, new BlockPos(player.getPosition()), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
			}

			return true;
		} else
			return false;
	}

	public static void setPersistentTag(EntityPlayer player, String tag, NBTBase value) {
		NBTTagCompound data = player.getEntityData();
		NBTTagCompound persistent;

		if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
			data.setTag(EntityPlayer.PERSISTED_NBT_TAG, (persistent = new NBTTagCompound()));
		} else {
			persistent = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		}

		persistent.setTag(tag, value);
	}

	public static NBTBase getPersistentTag(EntityPlayer player, String tag, NBTBase expectedValue) {
		NBTTagCompound data = player.getEntityData();
		NBTTagCompound persistent;

		if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
			data.setTag(EntityPlayer.PERSISTED_NBT_TAG, (persistent = new NBTTagCompound()));
		} else {
			persistent = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		}

		if (persistent.hasKey(tag))
			return persistent.getTag(tag);
		else {
			persistent.setTag(tag, expectedValue);
			return expectedValue;
		}

	}
	
	public static void setPersistentInteger(EntityPlayer player, String tag, int value) {
		setPersistentTag(player, tag, new NBTTagInt(value));
	}

	public static int getPersistentInteger(EntityPlayer player, String tag, int expectedValue) {
		NBTBase theTag = getPersistentTag(player, tag, new NBTTagInt(expectedValue));
		return theTag instanceof NBTTagInt ? ((NBTTagInt) theTag).getInt() : expectedValue;
	}

	public void setLostCrystals(EntityPlayer player, int lost) {
		setPersistentInteger(player, "enigmaticlegacy.lostsoulfragments", lost);
		this.updatePlayerSoulMap(player);
	}

	public int getLostCrystals(EntityPlayer player) {
		return getPersistentInteger(player, "enigmaticlegacy.lostsoulfragments", 0);
	}

	public Multimap<String, AttributeModifier> getOrCreateSoulMap(EntityPlayer player) {
		if (this.attributeDispatcher.containsKey(player))
			return this.attributeDispatcher.get(player);
		else {
			Multimap<String, AttributeModifier> playerAttributes = HashMultimap.create();
			this.attributeDispatcher.put(player, playerAttributes);
			return playerAttributes;
		}
	}

	public void applyPlayerSoulMap(EntityPlayer player) {
		Multimap<String, AttributeModifier> soulMap = this.getOrCreateSoulMap(player);
		AbstractAttributeMap attributeManager = player.getAttributeMap();
		attributeManager.applyAttributeModifiers(soulMap);
	}

	public void updatePlayerSoulMap(EntityPlayer player) {
		Multimap<String, AttributeModifier> soulMap = this.getOrCreateSoulMap(player);
		AbstractAttributeMap attributeManager = player.getAttributeMap();

		// Removes former attributes
		attributeManager.removeAttributeModifiers(soulMap);

		soulMap.clear();

		int lostFragments = getPersistentInteger(player, "enigmaticlegacy.lostsoulfragments", 0);

		if (lostFragments > 0) {
			soulMap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(UUID.fromString("66a2aa2d-7e3c-4af4-882f-bd2b2ded8e7b"), "Lost Soul Health Modifier", -0.1F * lostFragments, 2));
		}

		// Applies new attributes
		attributeManager.applyAttributeModifiers(soulMap);

		this.attributeDispatcher.put(player, soulMap);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		//player.startUsingItem(hand);

		if (this.retrieveSoulFromCrystal(player, stack)) {
			Vec3d playerCenter = new Vec3d(player.getPosition());
			if (!player.world.isRemote) {
				EnigmaticLegacy.packetInstance.sendToAllAround(new PacketRecallParticles(playerCenter.x, playerCenter.y, playerCenter.z, 48, false), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), playerCenter.x, playerCenter.y, playerCenter.z, 64));
			}

			player.swingArm(hand);
			stack.setCount(0);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		} else
			return new ActionResult<>(EnumActionResult.PASS, stack);
	}

}