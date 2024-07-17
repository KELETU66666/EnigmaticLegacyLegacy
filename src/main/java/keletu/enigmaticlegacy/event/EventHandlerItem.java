package keletu.enigmaticlegacy.event;

import baubles.client.gui.GuiPlayerExpanded;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.api.IExtendedBauble;
import keletu.enigmaticlegacy.api.cap.ExtendedBaublesCapabilities;
import keletu.enigmaticlegacy.container.gui.GuiExtendedBaublesButton;
import keletu.enigmaticlegacy.container.gui.GuiExtraBaubles;
import keletu.enigmaticlegacy.container.gui.GuiPlayerButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
		if (stack.isEmpty() || !(stack.getItem() instanceof IExtendedBauble) || stack.hasCapability(ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)
				|| event.getCapabilities().values().stream().anyMatch(c -> c.hasCapability(ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)))
			return;

		event.addCapability(capabilityResourceLocation, new ICapabilityProvider() {

			@Override
			public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
				return capability == ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
			}

			@Nullable
			@Override
			public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
				return capability == ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE
						? ExtendedBaublesCapabilities.CAPABILITY_ITEM_BAUBLE.cast((IExtendedBauble) stack.getItem())
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
		}else if (event.getGui() instanceof GuiExtraBaubles) {
			GuiContainer gui = (GuiContainer) event.getGui();
			event.getButtonList().add(new GuiPlayerButton(55, gui, 64, 9, 10, 10));
		}
	}
}