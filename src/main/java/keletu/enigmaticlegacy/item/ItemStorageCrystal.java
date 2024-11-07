package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.util.helper.ExperienceHelper;
import keletu.enigmaticlegacy.util.helper.ItemNBTHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;

public class ItemStorageCrystal extends ItemBase {

	public ItemStorageCrystal() {
		super("storage_crystal", EnumRarity.EPIC);
		this.maxStackSize = 1;

		this.setCreativeTab(null);
	}

	public ItemStack storeDropsOnCrystal(Collection<EntityItem> drops, EntityPlayer player, @Nullable ItemStack embeddedSoulCrystal) {
		ItemStack crystal = new ItemStack(this);
		NBTTagCompound crystalNBT = ItemNBTHelper.getNBT(crystal);
		int counter = 0;

		for (EntityItem drop : drops) {
			ItemStack dropStack = drop.getItem();
			NBTTagCompound nbt = dropStack.serializeNBT();
			crystalNBT.setTag("storedStack" + counter, nbt);

			counter++;
		}

		if (embeddedSoulCrystal != null) {
			NBTTagCompound deserializedCrystal = new NBTTagCompound();
			embeddedSoulCrystal.deserializeNBT(deserializedCrystal);

			crystalNBT.setTag("embeddedSoul", deserializedCrystal);
		}

		ItemNBTHelper.setInt(crystal, "storedStacks", counter);

		int exp = ExperienceHelper.getPlayerXP(player);
		ExperienceHelper.drainPlayerXP(player, exp);

		ItemNBTHelper.setInt(crystal, "storedXP", (int) (exp/* * EnigmaticAmulet.savedXPFraction.getValue()*/));
		ItemNBTHelper.setBoolean(crystal, "isStored", true);

		return crystal;
	}

	public ItemStack retrieveDropsFromCrystal(ItemStack crystal, EntityPlayer player, ItemStack retrieveSoul) {
		NBTTagCompound crystalNBT = ItemNBTHelper.getNBT(crystal);
		int counter = crystalNBT.getInteger("storedStacks")-1;
		int exp = crystalNBT.getInteger("storedXP");

		for (int c = counter; c >= 0; c--) {
			NBTTagCompound nbt = crystalNBT.getCompoundTag("storedStack" + c);
			ItemStack stack = new ItemStack(nbt);
			if (!player.inventory.addItemStackToInventory(stack)) {
				EntityItem drop = new EntityItem(player.world, player.posX, player.posY, player.posZ, stack);
				player.world.spawnEntity(drop);
			}
			crystalNBT.removeTag("storedStack" + c);
		}

		ExperienceHelper.addPlayerXP(player, exp);

		if (retrieveSoul != null) {
			EnigmaticLegacy.soulCrystal.retrieveSoulFromCrystal(player, retrieveSoul);
		} else {
			player.world.playSound(null, new BlockPos(player.getPosition()), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
		}

		ItemNBTHelper.setBoolean(crystal, "isStored", false);
		ItemNBTHelper.setInt(crystal, "storedStacks", 0);
		ItemNBTHelper.setInt(crystal, "storedXP", 0);

		return crystal;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.swingArm(handIn);

		if (!worldIn.isRemote) {}

		return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

}