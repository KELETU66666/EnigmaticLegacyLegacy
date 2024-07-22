package keletu.enigmaticlegacy.effect;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.PotionAttackDamage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlazingStrengthEffect extends PotionAttackDamage {

	public BlazingStrengthEffect() {
		super(false, 0xFF5000, 3.0);
		this.setPotionName("potion.blazing_strength");
		this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "blazing_strength"));
		this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, 0).setBeneficial();
		this.setIconIndex(4, 0);
	}

	@SideOnly(Side.CLIENT)
	public int getStatusIconIndex() {
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("enigmaticlegacy", "textures/misc/potions.png"));
		return super.getStatusIconIndex();
	}

}