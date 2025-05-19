package keletu.enigmaticlegacy.util.compat;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

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

    public static void addThirstAmountWhenDrink(boolean worthy, EntityPlayer player) {
        IThirstCapability capability = SDCapabilities.getThirstData(player);
        if (worthy) {
            if (capability.isThirsty() || !QuickConfig.isThirstEnabled()) {
                ThirstUtil.takeDrink(player, 6, 3, 0);
            }
        } else
            player.addPotionEffect(new PotionEffect(SDPotions.thirsty, 160, 2, false, true));
    }
}
