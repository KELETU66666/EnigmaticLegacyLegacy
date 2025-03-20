package keletu.enigmaticlegacy.item;

import com.google.common.collect.Multimap;
import static keletu.enigmaticlegacy.EnigmaticConfigs.*;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.api.cap.IForbiddenConsumed;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.packet.PacketForceArrowRotations;
import keletu.enigmaticlegacy.packet.PacketPlayerMotion;
import keletu.enigmaticlegacy.util.Vector3;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
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
			list.add(I18n.format("tooltip.enigmaticlegacy.angelBlessing8"));
			list.add(I18n.format("tooltip.enigmaticlegacy.angelBlessing9"));
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

	@Override
	public void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
		if (IForbiddenConsumed.get(player).getSpellstoneCooldown() > 0)
			return;

		Vector3 accelerationVec = new Vector3(player.getLookVec());
		Vector3 motionVec = new Vector3(player.motionX, player.motionY, player.motionZ);

		if (player.isElytraFlying()) {
			accelerationVec = accelerationVec.multiply(angelBlessingAccelerationModifierElytra);
			accelerationVec = accelerationVec.multiply(1 / (Math.max(0.15D, motionVec.mag()) * 2.25D));
		} else {
			accelerationVec = accelerationVec.multiply(angelBlessingAccelerationModifier);
		}

		Vector3 finalMotion = new Vector3(motionVec.x + accelerationVec.x, motionVec.y + accelerationVec.y, motionVec.z + accelerationVec.z);

		EnigmaticLegacy.packetInstance.sendTo(new PacketPlayerMotion(finalMotion.x, finalMotion.y, finalMotion.z), player);
		player.motionX = finalMotion.x;
		player.motionY = finalMotion.y;
		player.motionZ = finalMotion.z;

		world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ENDEREYE_LAUNCH, SoundCategory.PLAYERS, 1.0F, (float) (0.6F + (Math.random() * 0.1D)));

		IForbiddenConsumed.get(player).setSpellstoneCooldown(angelBlessingSpellstoneCooldown);
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase living) {
		if (living.world.isRemote)
			return;

		List<EntityArrow> projectileEntities = living.world.getEntitiesWithinAABB(EntityArrow.class, new AxisAlignedBB(living.posX - this.range, living.posY - this.range, living.posZ - this.range, living.posX + this.range, living.posY + this.range, living.posZ + this.range));
		List<EntityThrowable> potionEntities = living.world.getEntitiesWithinAABB(EntityThrowable.class, new AxisAlignedBB(living.posX - this.range, living.posY - this.range, living.posZ - this.range, living.posX + this.range, living.posY + this.range, living.posZ + this.range));

		for (EntityArrow entity : projectileEntities) {
			this.redirect(living, entity);
		}
		
		for (EntityThrowable entity : potionEntities) {
			this.redirect(living, entity);
		}
	}

	public void redirect(EntityLivingBase bearer, Entity redirected) {
		if (redirected instanceof EntityWitherSkull)
			return;

		Vector3 entityPos = Vector3.fromEntityCenter(redirected);
		Vector3 bearerPos = Vector3.fromEntityCenter(bearer);

		Vector3 redirection = entityPos.subtract(bearerPos);
		redirection = redirection.normalize();

		if ((redirected instanceof EntityArrow && ((EntityArrow) redirected).shootingEntity == bearer) || (redirected instanceof EntityThrowable && ((EntityThrowable) redirected).getThrower() == bearer)) {
			if (redirected.getTags().contains("AB_ACCELERATED")) {
				if (redirected.getEntityWorld() instanceof WorldServer) {
					//ServerChunkCache cache = level.getChunkSource();
					//cache.broadcastAndSend(redirected, new ClientboundSetEntityMotionPacket(redirected));
					//cache.broadcastAndSend(redirected, new ClientboundTeleportEntityPacket(redirected));
					//EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(bearer.getX(), bearer.getY(), bearer.getZ(), 64.0D, bearer.level.dimension())), new PacketForceArrowRotations(redirected.getId(), redirected.getYRot(), redirected.getXRot(), redirected.getDeltaMovement().x, redirected.getDeltaMovement().y, redirected.getDeltaMovement().z, redirected.getX(), redirected.getY(), redirected.getZ()));
				}

				return;
			}

			if (redirected.getTags().stream().anyMatch(tag -> tag.startsWith("AB_DEFLECTED")))
				return;

			//if (redirected instanceof ThrownTrident) {
			//	ThrownTrident trident = (ThrownTrident) redirected;
//
			//	if (trident.clientSideReturnTridentTickCount > 0)
			//		return;
			//}

			if (redirected.addTag("AB_ACCELERATED")) {
				redirected.motionX *= 1.75D;
				redirected.motionY *= 1.75D;
				redirected.motionZ *= 1.75D;

				if (redirected.getEntityWorld() instanceof WorldServer) {
					EnigmaticLegacy.packetInstance.sendToAllAround(new PacketForceArrowRotations(redirected.getEntityId(), redirected.rotationYaw, redirected.rotationPitch, redirected.motionX, redirected.motionY, redirected.motionZ, redirected.posX, redirected.posY, redirected.posZ), new NetworkRegistry.TargetPoint(bearer.world.provider.getDimension(), bearer.posX, bearer.posY, bearer.posZ, 64.0D));
				}

				//EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(bearer.getX(), bearer.getY(), bearer.getZ(), 64.0D, bearer.level.dimension())), new PacketForceArrowRotations(redirected.getId(), redirected.getYRot(), redirected.getXRot(), redirected.getDeltaMovement().x, redirected.getDeltaMovement().y, redirected.getDeltaMovement().z, redirected.getX(), redirected.getY(), redirected.getZ()));
			}
		} else {
			// redirected.setDeltaMovement(redirection.x, redirection.y, redirection.z);
		}

		/*
		if (redirected instanceof AbstractHurtingProjectile) {
			AbstractHurtingProjectile redirectedProjectile = (AbstractHurtingProjectile) redirected;
			redirectedProjectile.xPower = (redirection.x / 4.0);
			redirectedProjectile.yPower = (redirection.y / 4.0);
			redirectedProjectile.zPower = (redirection.z / 4.0);
		}
		 */
	}
	
	@Override
	void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
		
	}
}