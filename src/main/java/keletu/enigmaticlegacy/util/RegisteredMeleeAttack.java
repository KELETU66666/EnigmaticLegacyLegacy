package keletu.enigmaticlegacy.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;
import java.util.WeakHashMap;

public class RegisteredMeleeAttack {
	private static final Map<EntityPlayer, RegisteredMeleeAttack> attackRegistry = new WeakHashMap<>();

	public static boolean hasRegisteredMeleeAttack(EntityPlayer player) {
		return attackRegistry.containsKey(player);
	}

	public static RegisteredMeleeAttack getRegisteredMeleeAttack(EntityPlayer player) {
		return attackRegistry.get(player);
	}

	public static void registerAttack(EntityPlayer player) {
		attackRegistry.put(player, new RegisteredMeleeAttack(player));
	}

	public static float getRegisteredAttackStregth(EntityLivingBase attacker) {
		float attackStregth = 1F;
		if (attacker instanceof EntityPlayer && hasRegisteredMeleeAttack((EntityPlayer) attacker)) {
			RegisteredMeleeAttack attack = getRegisteredMeleeAttack((EntityPlayer)attacker);
			if (attack.ticksExisted == attacker.ticksExisted) {
				attackStregth = attack.cooledAttackStrength;
			}
		}

		return attackStregth;
	}

	public static void clearRegistry() {
		attackRegistry.clear();
	}

	public final EntityPlayer player;
	public final int ticksSinceLastSwing;
	public final int ticksExisted;
	public final float cooledAttackStrength;

	private RegisteredMeleeAttack(EntityPlayer player) {
		this.player = player;

		this.ticksExisted = player.ticksExisted;
		this.ticksSinceLastSwing = player.ticksSinceLastSwing;
		this.cooledAttackStrength = player.getCooledAttackStrength(0.5F);
	}



}