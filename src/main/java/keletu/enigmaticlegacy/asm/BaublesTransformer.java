package keletu.enigmaticlegacy.asm;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.container.SlotBauble;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.api.bmtr.ASMException;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.item.ItemScrollBauble;
import keletu.enigmaticlegacy.item.ItemSpellstoneBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.function.Predicate;

public class BaublesTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("baubles.api.BaubleType".equals(transformedName)) {
            return transformBaubleType(basicClass);
        }
        if ("baubles.api.cap.BaublesContainer".equals(transformedName)) {
            return transformBaublesContainer(basicClass);
        }
        if ("baubles.common.container.ContainerPlayerExpanded".equals(transformedName)) {
            return transformContainerPlayerExpanded(basicClass);
        }
        if ("baubles.common.event.EventHandlerEntity".equals(transformedName)) {
            return transformEventHandlerEntity(basicClass);
        }

        if ("baubles.client.gui.GuiPlayerExpanded".equals(transformedName)) {
            try {
                return transformGuiPlayerExpanded(basicClass);
            } catch (ASMException e) {
                System.err.println("Failed to transform GuiPlayerExpanded, ignoring as this is normal on a server");
                e.printStackTrace();
                return basicClass;
            }
        }

        if ("baubles.common.CommonProxy".equals(transformedName)) {
            return transformCommonProxy(basicClass);
        }
        if ("baubles.common.container.SlotBauble".equals(transformedName)) {
            return patchSlotBauble(basicClass);
        }
        return basicClass;
    }

    private byte[] patchSlotBauble(byte[] origCode) {
        final String methodToPatch2 = "isItemValid";
        final String methodToPatch_srg2 = "func_75214_a";

        ClassReader cr = new ClassReader(origCode);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if ((methodNode.name.equals(methodToPatch2) || methodNode.name.equals(methodToPatch_srg2)) && methodNode.desc.equals("(Lnet/minecraft/item/ItemStack;)Z")) {
                Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
                while (insnNodes.hasNext()) {
                    AbstractInsnNode insn = insnNodes.next();
                    if (insn.getOpcode() == Opcodes.IRETURN) {
                        InsnList endList = new InsnList();
                        endList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                        endList.add(new VarInsnNode(Opcodes.ALOAD, 1)); // stack
                        endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keletu/enigmaticlegacy/asm/BaublesTransformer", "slotBauble_isItemValid", "(Lbaubles/common/container/SlotBauble;Lnet/minecraft/item/ItemStack;)Z", false));
                        methodNode.instructions.insertBefore(insn, endList);
                    }
                }
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);

        return cw.toByteArray();
    }

    public static boolean slotBauble_isItemValid(SlotBauble slot, ItemStack stack) throws IllegalAccessException {
        Field field = ReflectionHelper.findField(SlotBauble.class, "baubleSlot");
        Field field1 = ReflectionHelper.findField(SlotBauble.class, "player");
        field.setAccessible(true);
        field1.setAccessible(true);
        int baubleSlot = (int) field.get(slot);
        EntityPlayer player = (EntityPlayer) field1.get(slot);
        boolean isScroll = stack.getItem() instanceof ItemScrollBauble;
        boolean isSpellstone = stack.getItem() instanceof ItemSpellstoneBauble;
        if ((isScroll || isSpellstone) && baubleSlot < 10)
            return false;
        switch (baubleSlot) {
            case 7:
                return SuperpositionHandler.getPersistentBoolean(player, "ConsumedIchorBottle", true);
            case 8:
                return SuperpositionHandler.getPersistentBoolean(player, "ConsumedAstralFruit", true);
            case 9:
                return BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.enigmaticEye) != -1;
            case 10:
                return !isScroll;
            case 11:
                return !isSpellstone;
            case 12:
                return /*!(stack.getItem() instanceof ItemSpellstoneBauble)*/ false;
        }

        return ((IBaublesItemHandler) slot.getItemHandler()).isItemValidForSlot(baubleSlot, stack, player);
    }

    private byte[] transformEventHandlerEntity(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);


        MethodNode mn = locateMethod(cn, "attachCapabilitiesPlayer");
        AbstractInsnNode inst = locateTargetInsn(mn, n -> Opcodes.NEW == n.getOpcode() && ((TypeInsnNode) n).desc.equals("baubles/api/cap/BaublesContainer") && n.getNext().getNext().getOpcode() == Opcodes.INVOKESPECIAL);
        AbstractInsnNode constructor = inst.getNext().getNext();
        mn.instructions.insert(constructor, new MethodInsnNode(Opcodes.INVOKESPECIAL, "keletu/enigmaticlegacy/api/bmtr/BaublesStackHandler", "<init>", "()V", false));
        mn.instructions.remove(constructor);
        mn.instructions.insert(inst, new TypeInsnNode(Opcodes.NEW, "keletu/enigmaticlegacy/api/bmtr/BaublesStackHandler"));
        mn.instructions.remove(inst);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS) {
            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                System.out.println();
                return super.getCommonSuperClass(type1, type2);
            }
        };
        cn.accept(cw);
        return cw.toByteArray();
    }

    private byte[] transformCommonProxy(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        MethodNode mn = locateMethod(cn, "getServerGuiElement");
        AbstractInsnNode node = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.NEW && n.getNext().getOpcode() == Opcodes.DUP);
        mn.instructions.insert(node, new TypeInsnNode(Opcodes.NEW, "keletu/enigmaticlegacy/api/bmtr/ContainerBaubles"));
        mn.instructions.remove(node);

        AbstractInsnNode initContainer = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.INVOKESPECIAL && ((MethodInsnNode) n).owner.equals("baubles/common/container/ContainerPlayerExpanded") && ((MethodInsnNode) n).name.equals("<init>"));
        mn.instructions.insert(initContainer, new MethodInsnNode(Opcodes.INVOKESPECIAL, "keletu/enigmaticlegacy/api/bmtr/ContainerBaubles", "<init>", new String(((MethodInsnNode) initContainer).desc), false));
        mn.instructions.remove(initContainer);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private byte[] transformGuiPlayerExpanded(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        MethodNode mn = locateMethod(cn, "<init>");
        AbstractInsnNode node = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.NEW && n.getNext().getOpcode() == Opcodes.DUP);
        mn.instructions.insert(node, new TypeInsnNode(Opcodes.NEW, "keletu/enigmaticlegacy/api/bmtr/ContainerBaubles"));
        mn.instructions.remove(node);
        AbstractInsnNode initContainer = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.INVOKESPECIAL && ((MethodInsnNode) n).owner.equals("baubles/common/container/ContainerPlayerExpanded") && ((MethodInsnNode) n).name.equals("<init>"));
        mn.instructions.insert(initContainer, new MethodInsnNode(Opcodes.INVOKESPECIAL, "keletu/enigmaticlegacy/api/bmtr/ContainerBaubles", "<init>", new String(((MethodInsnNode) initContainer).desc), false));
        mn.instructions.remove(initContainer);


        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private byte[] transformContainerPlayerExpanded(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        MethodNode mn = locateMethod(cn, "<init>");
        AbstractInsnNode ain = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.BIPUSH && n.getNext().getOpcode() == Opcodes.BIPUSH && n.getNext().getNext().getOpcode() == Opcodes.BIPUSH && ((IntInsnNode) n).operand == 6);
        while (ain.getOpcode() != Opcodes.POP) {
            ain = ain.getNext();
        }
        InsnList list = new InsnList();
        list.add(new IntInsnNode(Opcodes.ALOAD, 0));
        list.add(new IntInsnNode(Opcodes.ALOAD, 3));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keletu/enigmaticlegacy/api/bmtr/Snippets", "addSlotsToContainer", "(Lbaubles/common/container/ContainerPlayerExpanded;Ljava/lang/Object;)V", false));
        mn.instructions.insert(ain, list);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private byte[] transformBaublesContainer(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        transformConstant(cn);
        transformConstructor(cn);
        transformSetSize(cn);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private void transformSetSize(ClassNode cn) {
        MethodNode mn = locateMethod(cn, "(I)V", "setSize");
        AbstractInsnNode l0 = locateTargetInsn(mn, n -> n instanceof LabelNode);
        AbstractInsnNode l1 = locateTargetInsn(mn, n -> n instanceof LabelNode && !((LabelNode) n).getLabel().equals(((LabelNode) l0).getLabel()));
        while (!l0.getNext().equals(l1)) {
            mn.instructions.remove(l0.getNext());
        }
    }

    private void transformConstructor(ClassNode cn) {
        MethodNode mn = locateMethod(cn, "()V", "<init>");
        AbstractInsnNode bipush = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.BIPUSH && n.getNext().getOpcode() == Opcodes.INVOKESPECIAL);
        mn.instructions.insert(bipush, new IntInsnNode(Opcodes.BIPUSH, 7 + 6));
        mn.instructions.remove(bipush);

        AbstractInsnNode arraySizeNode = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.BIPUSH && n.getNext().getOpcode() == Opcodes.NEWARRAY);
        mn.instructions.insert(arraySizeNode, new IntInsnNode(Opcodes.BIPUSH, 7 + 6));
        mn.instructions.remove(arraySizeNode);
    }

    private void transformConstant(ClassNode cn) {
        FieldNode fn = cn.fields.parallelStream()
                .filter(f -> "BAUBLE_SLOTS".equals(f.name))
                .findFirst().orElseThrow(() -> new ASMException("Can't find field BAUBLE_SLOTS"));
        fn.value = 6;
    }

    private byte[] transformBaubleType(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        //new rings will have index 7, 8, 9, 10, 11, 12, 13, 14
        MethodNode mn = locateMethod(cn, "()V", "<clinit>");
        addAmulet(mn);
        addRings(mn);
        addCharm(mn);
        addTrinkets(mn);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private void addTrinkets(MethodNode mn) {
        AbstractInsnNode arraysizeNode = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.LDC && ((LdcInsnNode) n).cst.equals("TRINKET")).getNext().getNext();
        mn.instructions.insert(arraysizeNode, new IntInsnNode(Opcodes.BIPUSH, 7 + 6));
        mn.instructions.remove(arraysizeNode);
        AbstractInsnNode init = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.INVOKESPECIAL && ((MethodInsnNode) n).name.equals("<init>") && ((MethodInsnNode) n).desc.equals("(Ljava/lang/String;I[I)V") && n.getNext().getOpcode() == Opcodes.PUTSTATIC && ((FieldInsnNode) n.getNext()).name.equals("TRINKET"));
        InsnList initArrayInsns = new InsnList();
        for (int i = 7; i < 7 + 6; i++) {
            initArrayInsns.add(new InsnNode(Opcodes.DUP));
            initArrayInsns.add(new IntInsnNode(Opcodes.BIPUSH, i));
            initArrayInsns.add(new IntInsnNode(Opcodes.BIPUSH, i));
            initArrayInsns.add(new InsnNode(Opcodes.IASTORE));
        }
        mn.instructions.insertBefore(init, initArrayInsns);
    }

    private void addRings(MethodNode mn) {
        AbstractInsnNode arraysizeNode = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.LDC && ((LdcInsnNode) n).cst.equals("RING")).getNext().getNext();

        mn.instructions.insert(arraysizeNode, new IntInsnNode(Opcodes.BIPUSH, 2 + 1));
        mn.instructions.remove(arraysizeNode);
        AbstractInsnNode init = locateTargetInsn(mn, n -> n instanceof MethodInsnNode && ((MethodInsnNode) n).name.equals("<init>") && ((MethodInsnNode) n).desc.equals("(Ljava/lang/String;I[I)V") && n.getNext().getOpcode() == Opcodes.PUTSTATIC && ((FieldInsnNode) n.getNext()).name.equals("RING"));
        InsnList initArrayInsns = new InsnList();
        for (int i = 2; i < 2 + 1; i++) {
            initArrayInsns.add(new InsnNode(Opcodes.DUP));
            initArrayInsns.add(new IntInsnNode(Opcodes.BIPUSH, i));
            initArrayInsns.add(new IntInsnNode(Opcodes.BIPUSH, i + 6));
            initArrayInsns.add(new InsnNode(Opcodes.IASTORE));
        }
        mn.instructions.insertBefore(init, initArrayInsns);
    }

    private void addAmulet(MethodNode mn) {
        AbstractInsnNode arraysizeNode = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.LDC && ((LdcInsnNode) n).cst.equals("AMULET")).getNext().getNext();

        mn.instructions.insert(arraysizeNode, new IntInsnNode(Opcodes.BIPUSH, 1 + 1));
        mn.instructions.remove(arraysizeNode);
        AbstractInsnNode init = locateTargetInsn(mn, n -> n instanceof MethodInsnNode && ((MethodInsnNode) n).name.equals("<init>") && ((MethodInsnNode) n).desc.equals("(Ljava/lang/String;I[I)V") && n.getNext().getOpcode() == Opcodes.PUTSTATIC && ((FieldInsnNode) n.getNext()).name.equals("AMULET"));
        InsnList initArrayInsns = new InsnList();
        for (int i = 1; i < 1 + 1; i++) {
            initArrayInsns.add(new InsnNode(Opcodes.DUP));
            initArrayInsns.add(new IntInsnNode(Opcodes.BIPUSH, i));
            initArrayInsns.add(new IntInsnNode(Opcodes.BIPUSH, i + 6));
            initArrayInsns.add(new InsnNode(Opcodes.IASTORE));
        }
        mn.instructions.insertBefore(init, initArrayInsns);
    }

    private void addCharm(MethodNode mn) {
        AbstractInsnNode arraysizeNode = locateTargetInsn(mn, n -> n.getOpcode() == Opcodes.LDC && ((LdcInsnNode) n).cst.equals("CHARM")).getNext().getNext();

        mn.instructions.insert(arraysizeNode, new IntInsnNode(Opcodes.BIPUSH, 1 + 1));
        mn.instructions.remove(arraysizeNode);
        AbstractInsnNode init = locateTargetInsn(mn, n -> n instanceof MethodInsnNode && ((MethodInsnNode) n).name.equals("<init>") && ((MethodInsnNode) n).desc.equals("(Ljava/lang/String;I[I)V") && n.getNext().getOpcode() == Opcodes.PUTSTATIC && ((FieldInsnNode) n.getNext()).name.equals("CHARM"));
        InsnList initArrayInsns = new InsnList();
        for (int i = 1; i < 1 + 1; i++) {
            initArrayInsns.add(new InsnNode(Opcodes.DUP));
            initArrayInsns.add(new IntInsnNode(Opcodes.BIPUSH, i));
            initArrayInsns.add(new IntInsnNode(Opcodes.BIPUSH, i + 8));
            initArrayInsns.add(new InsnNode(Opcodes.IASTORE));
        }
        mn.instructions.insertBefore(init, initArrayInsns);
    }

    private static MethodNode locateMethod(ClassNode cn, String desc, String nameIn) {
        return cn.methods.parallelStream()
                .filter(n -> n.desc.equals(desc) && (n.name.equals(nameIn)))
                .findAny().orElseThrow(() -> new ASMException(nameIn + ": " + desc + " cannot be found in " + cn.name, cn));
    }

    private static MethodNode locateMethod(ClassNode cn, String nameIn) {
        return cn.methods.parallelStream()
                .filter(n -> n.name.equals(nameIn))
                .findAny()
                .orElseThrow(() -> new ASMException(nameIn + " cannot be found in " + cn.name, cn));
    }

    private static AbstractInsnNode locateTargetInsn(MethodNode mn, Predicate<AbstractInsnNode> filter) {
        AbstractInsnNode target = null;
        Iterator<AbstractInsnNode> i = mn.instructions.iterator();
        while (i.hasNext() && target == null) {
            AbstractInsnNode n = i.next();
            if (filter.test(n)) {
                target = n;
            }
        }
        if (target == null) {
            throw new ASMException("Can't locate target instruction in " + mn.name, mn);
        }
        return target;
    }
}