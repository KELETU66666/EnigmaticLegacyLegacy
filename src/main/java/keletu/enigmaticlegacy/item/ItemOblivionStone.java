package keletu.enigmaticlegacy.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ItemOblivionStone extends ItemBase {

	public ItemOblivionStone() {
		super("void_stone", EnumRarity.EPIC);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
	}

	public EnumRarity getRarity(ItemStack itemstack) {
		return EnumRarity.EPIC;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);

		int damage = itemstack.getItemDamage();

		if (player.isSneaking()) {

			if (damage < 100) {
				damage += 100;
				player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
			} else {
				player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
				damage -= 100;
			}

			itemstack.setItemDamage(damage);

		} else {

			if (damage == 0 || damage == 1 || damage == 100 || damage == 101) {
				damage += 1;
			} else if (damage == 2 || damage == 102) {
				damage -= 2;
			}

			player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, (float) (0.8F + (Math.random() * 0.2F)));

			itemstack.setItemDamage(damage);
		}

		player.swingArm(hand);

		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean b) {
		if (!(entity instanceof EntityPlayer) || !(entity.ticksExisted % 10 == 0))
			return;

		EntityPlayer player = (EntityPlayer) entity;
		
		int damage = itemstack.getItemDamage();
		if (damage >= 100 || !itemstack.hasTagCompound())
			return;

		NBTTagCompound nbt = itemstack.getTagCompound();
		int[] arr = nbt.getIntArray("SupersolidID");
		int[] meta = nbt.getIntArray("SupersolidMetaID");
		
		consumeStuff(player, arr, meta, damage);

	}

	@SideOnly(Side.CLIENT)
	public void registerModels() {
		for(int i = 0; i < 105; i++)
			ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}

	public static void consumeStuff(EntityPlayer player, int[] ID, int[] meta, int mode) {
		HashMap<Integer, ItemStack> stackMap = new HashMap<Integer, ItemStack>();
		int cycleCounter = 0;
		int filledStacks = 0;
		
		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if (player.inventory.mainInventory.get(slot) != ItemStack.EMPTY) {
				filledStacks += 1;
				if (player.inventory.mainInventory.get(slot).getItem() != EnigmaticLegacy.oblivionStone)
					stackMap.put(slot, player.inventory.mainInventory.get(slot));
			}
		}
		
		if (stackMap.size() == 0)
			return;

		if (mode == 0) {
			
			for (int sID : ID) {
				for (int slot : stackMap.keySet()) {
					if (meta[cycleCounter] != -1) {
					if (stackMap.get(slot).getItem() == Item.getItemById(sID) & stackMap.get(slot).getItemDamage() == meta[cycleCounter])
						player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
					} else {
					if (stackMap.get(slot).getItem() == Item.getItemById(sID)) 
						player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
					}
					
				}
				cycleCounter++;
			}
		} else if (mode == 1) {
			
			for (int sID : ID) {
				HashMap<Integer, ItemStack> localStackMap = new HashMap<Integer, ItemStack>(stackMap);
				Multimap<Integer, Integer> stackSizeMultimap = ArrayListMultimap.create();
				
				for (int slot : stackMap.keySet()) {
					if (meta[cycleCounter] != -1) {
						if (stackMap.get(slot).getItem() != Item.getItemById(sID) || stackMap.get(slot).getItemDamage() != meta[cycleCounter])
							localStackMap.remove(slot);
					} else {
						if (stackMap.get(slot).getItem() != Item.getItemById(sID))
							localStackMap.remove(slot);
					}
				}
				
				for (int slot : localStackMap.keySet()) {
					stackSizeMultimap.put(localStackMap.get(slot).getCount(), slot);
				}
				
				while (localStackMap.size() > 1) {
					int smallestStackSize = Collections.min(stackSizeMultimap.keySet());
					Collection<Integer> smallestStacks = stackSizeMultimap.get(smallestStackSize);
					int slotWithSmallestStack = Collections.max(smallestStacks);
					
					player.inventory.setInventorySlotContents(slotWithSmallestStack, ItemStack.EMPTY);
					stackSizeMultimap.remove(smallestStackSize, slotWithSmallestStack);
					localStackMap.remove(slotWithSmallestStack);
				}
				cycleCounter++;
			}
			
		} else if (mode == 2) {
			if (filledStacks >= player.inventory.mainInventory.size()) {
				
				for (int sID : ID) {
					HashMap<Integer, ItemStack> localStackMap = new HashMap<Integer, ItemStack>(stackMap);
					Multimap<Integer, Integer> stackSizeMultimap = ArrayListMultimap.create();
					
					for (int slot : stackMap.keySet()) {
						if (meta[cycleCounter] != -1) {
							if (stackMap.get(slot).getItem() != Item.getItemById(sID) || stackMap.get(slot).getItemDamage() != meta[cycleCounter])
								localStackMap.remove(slot);
						} else {
							if (stackMap.get(slot).getItem() != Item.getItemById(sID))
								localStackMap.remove(slot);
						}
					}
					
					for (int slot : localStackMap.keySet()) {
						stackSizeMultimap.put(localStackMap.get(slot).getCount(), slot);
					}
					
					if (localStackMap.size() > 0) {
						int smallestStackSize = Collections.min(stackSizeMultimap.keySet());
						Collection<Integer> smallestStacks = stackSizeMultimap.get(smallestStackSize);
						int slotWithSmallestStack = Collections.max(smallestStacks);
						
						player.inventory.setInventorySlotContents(slotWithSmallestStack, ItemStack.EMPTY);
						return;
					}
					
					cycleCounter++;
				}
				
			}
		}
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag par4) {

		if (GuiScreen.isShiftKeyDown()) {
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone2"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone2_more"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone3"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone4"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone5"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone6"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone7"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone8"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone9"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone10"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone11"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone12"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone13"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone14"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStone15"));
		} else if (GuiScreen.isCtrlKeyDown()) {
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStoneCtrlList"));
			if (stack.hasTagCompound()) {
				NBTTagCompound nbt = stack.getTagCompound();
				int[] arr = nbt.getIntArray("SupersolidID");
				int[] meta = nbt.getIntArray("SupersolidMetaID");
				int counter = 0;

				if (arr.length <= EnigmaticConfigs.oblivionStoneSoftCap) {
				for (int s : arr) {
					Item something = Item.getItemById(s);
					if (something != null) {
						ItemStack displayStack;
						if (meta[counter] != -1)
							displayStack = new ItemStack(something, 1, meta[counter]);
						else
							displayStack = new ItemStack(something, 1, 0);

						list.add(TextFormatting.GOLD + " - "
								+ displayStack.getDisplayName());
					}
					counter++;
				}
				} else {
					for (int s = 0; s < EnigmaticConfigs.oblivionStoneSoftCap; s++) {
						int randomID = (int) (Math.random()*30.0D);
						Item something = Item.getItemById(arr[randomID]);

						if (something != null) {
							ItemStack displayStack;
							if (meta[randomID] != -1)
								displayStack = new ItemStack(something, 1, meta[randomID]);
							else
								displayStack = new ItemStack(something, 1, 0);

							list.add(TextFormatting.GOLD + " - "
									+ displayStack.getDisplayName());
						}
					}
				}
			}
		} else {
			list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
			list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStoneCtrlTooltip"));
			list.add("");

			int mode = stack.getItemDamage();
			if (mode < 100) {
				list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStoneMode") + " "
						+ I18n.format("tooltip.enigmaticlegacy.OblivionMode" + mode));
			} else {
				list.add(I18n.format("tooltip.enigmaticlegacy.OblivionStoneMode") + " "
						+ I18n.format("tooltip.enigmaticlegacy.OblivionStoneDeactivated"));
			}
		}

		list.add("");
	}

}
