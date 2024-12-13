package keletu.enigmaticlegacy.util.compat;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import net.minecraft.entity.player.EntityPlayer;

public class CompatSimpledDifficulty {

    public static void ClearTempurature(EntityPlayer player) {
        final ITemperatureCapability temp = SDCapabilities.getTemperatureData(player);
        temp.setTemperatureLevel(12);
        if (player.isPotionActive(SDPotions.hyperthermia)) {
            player.removePotionEffect(SDPotions.hyperthermia);
        }
        if (player.isPotionActive(SDPotions.hypothermia)) {
            player.removePotionEffect(SDPotions.hypothermia);
        }
    }
}
