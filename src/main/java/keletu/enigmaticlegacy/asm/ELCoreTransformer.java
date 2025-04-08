package keletu.enigmaticlegacy.asm;

import baubles.api.BaublesApi;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasCursed;
import keletu.enigmaticlegacy.util.interfaces.ILootingBonus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
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
        return origCode;
    }

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
