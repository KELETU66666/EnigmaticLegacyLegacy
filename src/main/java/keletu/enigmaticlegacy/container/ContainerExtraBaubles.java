package keletu.enigmaticlegacy.container;

import baubles.common.container.ContainerPlayerExpanded;
import keletu.enigmaticlegacy.api.cap.EnigmaticCapabilities;
import keletu.enigmaticlegacy.api.cap.IExtendedBaublesItemHandler;
import keletu.enigmaticlegacy.container.slot.SlotExtraBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerExtraBaubles extends ContainerPlayerExpanded {

    private final IExtendedBaublesItemHandler extra;

    public ContainerExtraBaubles(InventoryPlayer playerInv, boolean par2, EntityPlayer player) {
        super(playerInv, par2, player);
        this.extra = player.getCapability(EnigmaticCapabilities.CAPABILITY_BAUBLES, null);

        this.addSlotToContainer(new SlotExtraBauble(player, extra, 0, 115, 62));
        this.addSlotToContainer(new SlotExtraBauble(player, extra, 1, 134, 62));
        this.addSlotToContainer(new SlotExtraBauble(player, extra, 2, 153, 62));
    }
}
