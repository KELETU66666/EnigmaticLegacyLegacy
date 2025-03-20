package keletu.enigmaticlegacy.item;

import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import static keletu.enigmaticlegacy.EnigmaticConfigs.eyeOfNebulaDodgeProbability;
import static keletu.enigmaticlegacy.EnigmaticConfigs.eyeOfNebulaMagicResistance;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.api.cap.IForbiddenConsumed;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.packet.PacketPlayerSetlook;
import keletu.enigmaticlegacy.packet.PacketPortalParticles;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ItemEyeOfNebula extends ItemSpellstoneBauble {

	public ItemEyeOfNebula() {
		super("eye_of_nebula", EnumRarity.EPIC);

		Supplier<Float> magicResistanceSupplier = () -> eyeOfNebulaMagicResistance;
		this.resistanceList.put(DamageSource.MAGIC.damageType, magicResistanceSupplier);
		this.resistanceList.put(DamageSource.DRAGON_BREATH.damageType, magicResistanceSupplier);
		this.resistanceList.put(DamageSource.WITHER.damageType, magicResistanceSupplier);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {

		list.add("");

		if (GuiScreen.isShiftKeyDown()) {
			list.add(I18n.format("tooltip.enigmaticlegacy.eyeOfNebula1"));
			list.add(I18n.format("tooltip.enigmaticlegacy.eyeOfNebula2"));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.eyeOfNebulaCooldown", EnigmaticConfigs.eyeOfNebulaSpellstoneCooldown / 20.0F));
			list.add("");
			list.add(I18n.format("tooltip.enigmaticlegacy.eyeOfNebula3"));
			list.add(I18n.format("tooltip.enigmaticlegacy.eyeOfNebula4", Math.round(eyeOfNebulaMagicResistance * 100) + "%"));
			list.add(I18n.format("tooltip.enigmaticlegacy.eyeOfNebula5", Math.round(eyeOfNebulaDodgeProbability* 100) + "%"));
			list.add(I18n.format("tooltip.enigmaticlegacy.eyeOfNebula6"));
		} else {
			list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
		}

		//try {
		//	list.add("");
		//	list.add(I18n.format("tooltip.enigmaticlegacy.currentKeybind", TextFormatting.LIGHT_PURPLE, KeyBinding.createNameSupplier("key.spellstoneAbility").get().getString().toUpperCase());
		//} catch (NullPointerException ex) {
		//	// Just don't do it lol
		//}
	}

	@Override
	public void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
		if (IForbiddenConsumed.get(player).getSpellstoneCooldown() > 0)
			return;

		EntityLivingBase target = SuperpositionHandler.getObservedEntity(player, world, 3.0F, (int) EnigmaticConfigs.eyeOfNebulaPhaseRange);

		if (target != null) {
			Vec3d targetPos = new Vec3d(target.getPosition());
			Vec3d chaserPos = new Vec3d(player.getPosition());

			Vec3d dir = targetPos.subtract(chaserPos);
			dir = dir.normalize();
			dir = dir.add(dir.x * 0.5D, dir.y * 0.5D, dir.z * 0.5D);

			dir = targetPos.add(dir);

			world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2D)));
			EnigmaticLegacy.packetInstance.sendToAllAround(new PacketPortalParticles(player.posX, player.posY + (player.getEyeHeight() / 2), player.posZ, 72, 1.0D, false), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 128));

			player.attemptTeleport(dir.x, target.posY + 0.25D, dir.z);
			EnigmaticLegacy.packetInstance.sendTo(new PacketPlayerSetlook(target.posX, target.posY - 1.0D + (target.getEyeHeight() / 2), target.posZ), player);

			world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2D)));
			EnigmaticLegacy.packetInstance.sendToAllAround(new PacketPortalParticles(player.posX, player.posY + (player.getEyeHeight() / 2), player.posZ, 72, 1.0D, false), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 128));

			IForbiddenConsumed.get(player).setSpellstoneCooldown(EnigmaticConfigs.eyeOfNebulaSpellstoneCooldown);
		}

	}

	@Override
	void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {

	}
}