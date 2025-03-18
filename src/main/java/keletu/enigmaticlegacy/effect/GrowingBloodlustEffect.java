package keletu.enigmaticlegacy.effect;

import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GrowingBloodlustEffect extends Potion {

    public GrowingBloodlustEffect() {
        super(false, 0xC30018);
        this.setPotionName("potion.growing_bloodlust");
        this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "growing_bloodlust"));
        this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "d88f6930-fefb-4bf7-a418-f368458355ff", EnigmaticConfigs.bloodLustDamageBoost, 2);
        this.setIconIndex(5, 1);
    }

    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        if (living instanceof EntityPlayerMP && !((EntityPlayerMP) living).isCreative() && !((EntityPlayerMP) living).isSpectator()) {
            EntityPlayerMP player = (EntityPlayerMP) living;
            if ((player.getHealth() / player.getMaxHealth()) > EnigmaticConfigs.bloodLustHealthLossLimit) {
                player.setHealth(player.getHealth() - (float) (1 + amplifier / (EnigmaticConfigs.bloodLustHealthLossTicks)));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("enigmaticlegacy", "textures/misc/potions.png"));
        return super.getStatusIconIndex();
    }
}