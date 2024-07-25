package keletu.enigmaticlegacy.event;

import baubles.client.gui.GuiPlayerExpanded;
import keletu.enigmaticlegacy.ELConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.api.ExtendedBaubleType;
import keletu.enigmaticlegacy.api.IExtendedBauble;
import keletu.enigmaticlegacy.api.cap.EnigmaticCapabilities;
import keletu.enigmaticlegacy.container.gui.GuiExtendedBaublesButton;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.isTheWorthyOne;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EventHandlerItem
{
	private static ResourceLocation capabilityResourceLocation = new ResourceLocation(EnigmaticLegacy.MODID, "bauble_cap");

	/**
	* Handles backwards compatibility with items that implement IBauble instead of exposing it as a capability.
	* This adds a IBauble capability wrapper for all items, if the item:
	* - does implement the IBauble interface
	* - does not already have the capability
	* - did not get the capability by another event handler earlier in the chain
	* @param event
	*/
	@SubscribeEvent(priority = EventPriority.LOWEST)
 	public void itemCapabilityAttach(AttachCapabilitiesEvent<ItemStack> event)
 	{
		ItemStack stack = event.getObject();
		if (stack.isEmpty() || !(stack.getItem() instanceof IExtendedBauble) || stack.hasCapability(EnigmaticCapabilities.CAPABILITY_ITEM_BAUBLE, null)
				|| event.getCapabilities().values().stream().anyMatch(c -> c.hasCapability(EnigmaticCapabilities.CAPABILITY_ITEM_BAUBLE, null)))
			return;

		event.addCapability(capabilityResourceLocation, new ICapabilityProvider() {

			@Override
			public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
				return capability == EnigmaticCapabilities.CAPABILITY_ITEM_BAUBLE;
			}

			@Nullable
			@Override
			public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
				return capability == EnigmaticCapabilities.CAPABILITY_ITEM_BAUBLE
						? EnigmaticCapabilities.CAPABILITY_ITEM_BAUBLE.cast((IExtendedBauble) stack.getItem())
						: null;
			}
		});
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {

		if (event.getGui() instanceof GuiPlayerExpanded) {
			GuiContainer gui = (GuiContainer) event.getGui();
			event.getButtonList().add(new GuiExtendedBaublesButton(55, gui, 114, 69, 10, 10));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void tooltipEvent(ItemTooltipEvent event) {
		if (!event.getItemStack().isEmpty() && event.getItemStack().hasCapability(EnigmaticCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			IExtendedBauble bauble = event.getItemStack().getCapability(EnigmaticCapabilities.CAPABILITY_ITEM_BAUBLE, null);
			ExtendedBaubleType bt = bauble.getBaubleType(event.getItemStack());
			event.getToolTip().add(TextFormatting.GOLD + I18n.format("name." + bt));
		}

		TextFormatting format = TextFormatting.DARK_RED;
		EntityPlayer player = Minecraft.getMinecraft().player;

		for (ResourceLocation rl : ELConfigs.eldritchItemList) {
			if (event.getItemStack().getItem() == ForgeRegistries.ITEMS.getValue(rl)) {
				if (player != null) {
					format = isTheWorthyOne(Minecraft.getMinecraft().player) ? TextFormatting.GOLD : TextFormatting.DARK_RED;
				}

				event.getToolTip().add(I18n.format("tooltip.enigmaticlegacy.worthyOnesOnly1"));
				event.getToolTip().add(I18n.format("tooltip.enigmaticlegacy.worthyOnesOnly2"));
				event.getToolTip().add(I18n.format("tooltip.enigmaticlegacy.worthyOnesOnly3"));
				event.getToolTip().add("");
				event.getToolTip().add(TextFormatting.LIGHT_PURPLE + I18n.format("tooltip.enigmaticlegacy.worthyOnesOnly4") + format + " " + SuperpositionHandler.getSufferingTime(player));
			}
		}
	}

}