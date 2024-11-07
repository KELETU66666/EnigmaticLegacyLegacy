package keletu.enigmaticlegacy.item;

import com.google.common.collect.Multimap;
import static keletu.enigmaticlegacy.ELConfigs.angelBlessingDeflectChance;
import static keletu.enigmaticlegacy.ELConfigs.angelBlessingSpellstoneCooldown;
import keletu.enigmaticlegacy.util.Vector3;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemAngelBlessing extends ItemSpellstoneBauble {

	protected double range = 4.0D;

	public ItemAngelBlessing() {

		super("angel_blessing", EnumRarity.RARE);
		this.immunityList.add(DamageSource.FALL.damageType);
		this.immunityList.add(DamageSource.FLY_INTO_WALL.damageType);

		this.resistanceList.put(DamageSource.WITHER.damageType, () -> 2F);
		this.resistanceList.put(DamageSource.OUT_OF_WORLD.damageType, () -> 2F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {

		list.add("");

		if (GuiScreen.isShiftKeyDown()) {
			list.add(I18n.format("tooltip.enigmaticlegacy.angelBlessing1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.angelBlessing2"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.angelBlessingCooldown") + TextFormatting.GOLD + angelBlessingSpellstoneCooldown / 20.0F + I18n.format("tooltip.enigmaticlegacy.angelBlessingCooldown_1"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.angelBlessing3"));
			list.add(I18n.format("tooltip.enigmaticlegacy.angelBlessing4"));
			list.add(I18n.format("tooltip.enigmaticlegacy.angelBlessing5") + TextFormatting.GOLD + angelBlessingDeflectChance * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.angelBlessing5_1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.angelBlessing6"));
			list.add(I18n.format("tooltip.enigmaticlegacy.angelBlessing7"));
		} else {
			list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
		}

		//try {
		//	list.add("";
		//	list.add(I18n.format("tooltip.enigmaticlegacy.currentKeybind", TextFormatting.LIGHT_PURPLE, KeyBinding.createNameSupplier("key.spellstoneAbility").get().getString().toUpperCase());
		//} catch (NullPointerException ex) {
		//	// Just don't do it lol
		//}
	}

	public void redirect(EntityLivingBase bearer, Entity redirected) {
		if (redirected instanceof EntityWitherSkull)
			return;

		/*
		 * if (redirected instanceof TridentEntity) if
		 * (((TridentEntity)redirected).getShooter() == bearer) return;
		 */

		Vector3 entityPos = Vector3.fromEntityCenter(redirected);
		Vector3 bearerPos = Vector3.fromEntityCenter(bearer);

		Vector3 redirection = entityPos.subtract(bearerPos);
		redirection = redirection.normalize();

		if (redirected instanceof EntityArrow && ((EntityArrow) redirected).shootingEntity == bearer) {

			//if (redirected instanceof TridentEntity) {
			//	TridentEntity trident = (TridentEntity) redirected;

			//	if (trident.clientSideReturnTridentTickCount > 0)
			//		return;
			//}

			redirected.motionX *= 1.75D;
			redirected.motionY *= 1.75D;
			redirected.motionZ *= 1.75D;
		} else {
			redirected.motionX = redirection.x;
			redirected.motionY = redirection.y;
			redirected.motionZ = redirection.z;
		}

		if (redirected instanceof EntityArrow) {
			EntityArrow redirectedProjectile = (EntityArrow) redirected;
			redirectedProjectile.posX = (redirection.x / 4.0);
			redirectedProjectile.posY = (redirection.y / 4.0);
			redirectedProjectile.posZ = (redirection.z / 4.0);
		}
	}

	@Override
	void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
		
	}
}