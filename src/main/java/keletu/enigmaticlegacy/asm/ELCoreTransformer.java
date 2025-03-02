package keletu.enigmaticlegacy.asm;

import baubles.api.BaublesApi;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasCursed;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasPearl;
import keletu.enigmaticlegacy.packet.PacketEnchantedWithPearl;
import keletu.enigmaticlegacy.util.interfaces.ILootingBonus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class ELCoreTransformer implements IClassTransformer {
    static boolean isDeobfEnvironment;

    @Override
    public byte[] transform(String className, String newClassName, byte[] origCode) {
        isDeobfEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        /*if (newClassName.equals("net.minecraft.block.Block")) {
            byte[] newCode = patchGetFortuneModifier(origCode);
            return newCode;
        }*/
        if (newClassName.equals("net.minecraft.enchantment.EnchantmentHelper")) {
            byte[] newCode = patchGetLootModifier(origCode);
            return newCode;
        }
        if (newClassName.equals("net.minecraft.inventory.ContainerEnchantment")) {
            byte[] newCode = patchEnchantmentMethods(origCode);
            return newCode;
        }
        return origCode;
    }
/*
    private byte[] patchGetFortuneModifier(byte[] origCode) {
        final String methodToPatch1 = "quantityDropped";
        final String methodToPatch_srg1 = "func_149745_a";

        final String desc = "(Lnet/minecraft/block/state/IBlockState;FI)I";

        ClassReader cr = new ClassReader(origCode);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if ((methodNode.name.equals(methodToPatch1) || methodNode.name.equals(methodToPatch_srg1)) && methodNode.desc.equals(desc)) {
                Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
                while (insnNodes.hasNext()) {
                    AbstractInsnNode insn = insnNodes.next();

                    if (insn.getOpcode() == Opcodes.RETURN) {
                        InsnList endList = new InsnList();
                        endList.add(new VarInsnNode(Opcodes.ALOAD, 1)); // IBlockState
                        endList.add(new VarInsnNode(Opcodes.ILOAD, 2)); // Fortune
                        endList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Random
                        endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keletu/enigmaticlegacy/asm/ELCoreTransformer", "block_quantityDropped", "(Lnet/minecraft/block/state/IBlockState;ILjava/util/Random;)I", false));
                        methodNode.instructions.insertBefore(insn, endList);
                    }
                }
            }
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);

        return cw.toByteArray();
    }
*/
    private byte[] patchGetLootModifier(byte[] origCode) {
        final String methodToPatch2 = "getLootingModifier";
        final String methodToPatch_srg2 = "func_185283_h";

        final String desc = "(Lnet/minecraft/entity/EntityLivingBase;)I";

        ClassReader cr = new ClassReader(origCode);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if ((methodNode.name.equals(methodToPatch2) || methodNode.name.equals(methodToPatch_srg2)) && methodNode.desc.equals(desc)) {
                Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
                while (insnNodes.hasNext()) {
                    AbstractInsnNode insn = insnNodes.next();

                    if (insn.getOpcode() == Opcodes.IRETURN) {
                        InsnList endList = new InsnList();
                        endList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keletu/enigmaticlegacy/asm/ELCoreTransformer", "enchantment_getLootingLevel", desc, false));
                        methodNode.instructions.insertBefore(insn, endList);
                    }
                }
            }
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);

        return cw.toByteArray();
    }

    public byte[] patchEnchantmentMethods(byte[] origCode) {
        final String methodToPatch2 = "getLapisAmount";
        final String methodToPatch_srg2 = "func_178147_e";
        final String methodToPatch3 = "enchantItem";
        final String methodToPatch_srg3 = "func_75140_a";
        final String getLapisAmountDesc = "()I";
        final String enchantItemDesc = "(Lnet/minecraft/entity/player/EntityPlayer;I)Z";
        final String staticMethodDesc = "(Lnet/minecraft/inventory/ContainerEnchantment;Lnet/minecraft/entity/player/EntityPlayer;I)Z";

        ClassReader cr = new ClassReader(origCode);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if ((methodNode.name.equals(methodToPatch2) || methodNode.name.equals(methodToPatch_srg2)) && methodNode.desc.equals(getLapisAmountDesc)) {
                Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
                while (insnNodes.hasNext()) {
                    AbstractInsnNode insn = insnNodes.next();
                    if (insn.getOpcode() == Opcodes.IRETURN) {
                        InsnList endList = new InsnList();
                        endList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keletu/enigmaticlegacy/asm/ELCoreTransformer", "containerEnchantment_getLapisAmount", "(Lnet/minecraft/inventory/ContainerEnchantment;)I", false));
                        methodNode.instructions.insertBefore(insn, endList);
                    }
                }
            } else if ((methodNode.name.equals(methodToPatch3) || methodNode.name.equals(methodToPatch_srg3)) && methodNode.desc.equals(enchantItemDesc)) {
                InsnList startList = new InsnList();
                startList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                startList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                startList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                startList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keletu/enigmaticlegacy/asm/ELCoreTransformer", "containerEnchantment_enchantItem", staticMethodDesc, false));
                methodNode.instructions.insert(startList);
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);

        return cw.toByteArray();
    }
/*
    public static int block_quantityDropped(IBlockState state, int fortune, Random random) {
        try {
            Field field = ReflectionHelper.findField(Block.class, "harvesters");
            field.setAccessible(true);
            ThreadLocal<EntityPlayer> stupidForgeMethod = (ThreadLocal<EntityPlayer>) field.get(state.getBlock());

            if (stupidForgeMethod.get() != null) {
                int baseFortune = fortune + ELConfigs.fortuneBonus;
                return state.getBlock().quantityDroppedWithBonus(baseFortune, random);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state.getBlock().quantityDroppedWithBonus(fortune, random);
    }
*/
    public static int containerEnchantment_getLapisAmount(ContainerEnchantment container) {
        EntityPlayer containerUser = null;

        for (Slot slot : container.inventorySlots) {
            if (slot.inventory instanceof InventoryPlayer) {
                InventoryPlayer playerInv = (InventoryPlayer) slot.inventory;
                containerUser = playerInv.player;
                break;
            }
        }

        if (containerUser != null) {
            if (hasPearl(containerUser))
                return 64;
        }

        ItemStack itemstack = container.tableInventory.getStackInSlot(1);
        return itemstack.isEmpty() ? 0 : itemstack.getCount();
    }

    public static boolean containerEnchantment_enchantItem(ContainerEnchantment container, EntityPlayer playerIn, int id) {
        ItemStack itemstack = container.tableInventory.getStackInSlot(0);
        ItemStack itemstack1 = container.tableInventory.getStackInSlot(1);  // Lapis lazuli stack
        int i = id + 1;

        if ((itemstack1.isEmpty() || itemstack1.getCount() < i) && !playerIn.capabilities.isCreativeMode && !hasPearl(playerIn)) {
            return false;
        }
        // Remove the check for lapis lazuli stack and its count
        else if ((container.enchantLevels[id] > 0 && !itemstack.isEmpty()
                && (playerIn.experienceLevel >= i && playerIn.experienceLevel >= container.enchantLevels[id] || playerIn.capabilities.isCreativeMode))) {
            EnigmaticLegacy.packetInstance.sendToServer(new PacketEnchantedWithPearl(id));
            return true;
        } else {
            return false;
        }
    }

    public static int enchantment_getLootingLevel(EntityLivingBase living) {
        int base = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, living.getHeldItemMainhand());
        if (living instanceof EntityPlayer) {
            if (EnigmaticConfigs.lootingBonus > 0 && hasCursed((EntityPlayer) living))
                base += EnigmaticConfigs.lootingBonus;

            for (int i = 0; i < BaublesApi.getBaublesHandler((EntityPlayer) living).getSlots(); i++) {
                ItemStack bStack = BaublesApi.getBaubles((EntityPlayer) living).getStackInSlot(i);
                if (!bStack.isEmpty() && bStack.getItem() instanceof ILootingBonus) {
                    base += ((ILootingBonus) bStack.getItem()).bonusLevelLooting();
                }
            }
        }
        return base;
    }


}
