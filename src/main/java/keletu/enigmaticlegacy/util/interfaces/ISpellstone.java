package keletu.enigmaticlegacy.util.interfaces;

import keletu.enigmaticlegacy.api.cap.IForbiddenConsumed;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ISpellstone {

    default void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
        if (IForbiddenConsumed.get(player).getSpellstoneCooldown() > 0)
            return;
    }
}
