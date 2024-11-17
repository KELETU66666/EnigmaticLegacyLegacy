package keletu.enigmaticlegacy.asm;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.container.SlotBauble;
import keletu.enigmaticlegacy.ELConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.api.bmtr.ASMException;
import keletu.enigmaticlegacy.event.ELEvents;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasCursed;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasPearl;
import keletu.enigmaticlegacy.item.ItemScrollBauble;
import keletu.enigmaticlegacy.item.ItemSpellstoneBauble;
import keletu.enigmaticlegacy.packet.PacketEnchantedWithPearl;
import keletu.enigmaticlegacy.util.interfaces.ILootingBonus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.function.Predicate;

public class ELCoreTransformer implements IClassTransformer {
    static boolean isDeobfEnvironment;

    @Override
    public byte[] transform(String className, String newClassName, byte[] origCode) {
        isDeobfEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        /*if (newClassName.equals("net.minecraft.block.Block")) {
            byte[] newCode = patchGetFortuneModifier(origCode);
            return newCode;
        }*/
        if ("net.minecraft.enchantment.EnchantmentHelper".equals(newClassName)) {
            return patchGetLootModifier(origCode);
        }
        if ("net.minecraft.inventory.ContainerEnchantment".equals(newClassName)) {
            return patchEnchantmentMethods(origCode);
        }
        if ("net.minecraft.entity.EntityLivingBase".equals(newClassName)) {
            return patchEntityLivingBase(origCode);
        }
        if ("baubles.api.BaubleType".equals(newClassName)) {
            return transformBaubleType(origCode);
        }
        if ("baubles.api.cap.BaublesContainer".equals(newClassName)) {
            return transformBaublesContainer(origCode);
        }
        if ("baubles.common.container.ContainerPlayerExpanded".equals(newClassName)) {
            return transformContainerPlayerExpanded(origCode);
        }
        if ("baubles.common.event.EventHandlerEntity".equals(newClassName)) {
            return transformEventHandlerEntity(origCode);
        }

        if ("baubles.client.gui.GuiPlayerExpanded".equals(newClassName)) {
            try {
                return transformGuiPlayerExpanded(origCode);
            } catch (ASMException e) {
                System.err.println("Failed to transform GuiPlayerExpanded, ignoring as this is normal on a server");
                e.printStackTrace();
                return origCode;
            }
        }

        if ("baubles.common.CommonProxy".equals(newClassName)) {
            return transformCommonProxy(origCode);
        }
        if ("baubles.common.container.SlotBauble".equals(newClassName)) {
            return patchSlotBauble(origCode);
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

    private byte[] patchEntityLivingBase(byte[] origCode) {
        final String methodToPatch2 = "canBlockDamageSource";
        final String methodToPatch_srg2 = "func_184583_d";
        final String methodToPatch3 = "isActiveItemStackBlocking";
        final String methodToPatch_srg3 = "func_184585_cz";

        ClassReader cr = new ClassReader(origCode);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if ((methodNode.name.equals(methodToPatch2) || methodNode.name.equals(methodToPatch_srg2)) && methodNode.desc.equals("(Lnet/minecraft/util/DamageSource;)Z")) {
                Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
                while (insnNodes.hasNext()) {
                    AbstractInsnNode insn = insnNodes.next();
                    if (insn.getOpcode() == Opcodes.IRETURN) {
                        InsnList endList = new InsnList();
                        endList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        endList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keletu/enigmaticlegacy/asm/ELCoreTransformer", "elb_canBlockDamageSource", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;)Z", false));
                        methodNode.instructions.insertBefore(insn, endList);
                    }
                }
            } /*else if ((methodNode.name.equals(methodToPatch3) || methodNode.name.equals(methodToPatch_srg3)) && methodNode.desc.equals("()Z")) {
                InsnList startList = new InsnList();
                startList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                startList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keletu/enigmaticlegacy/asm/ELCoreTransformer", "elb_isActiveItemBlocking", "(Lnet/minecraft/entity/EntityLivingBase;)Z", false));
                methodNode.instructions.insert(startList);
            }*/
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);

        return cw.toByteArray();
    }

    public static boolean elb_canBlockDamageSource(EntityLivingBase elb, DamageSource damageSourceIn) {
        return SuperpositionHandler.onDamageSourceBlocking(elb, elb.getActiveItemStack(), damageSourceIn);
    }

    public static boolean elb_isActiveItemBlocking(EntityLivingBase elb) {
        if (elb.isHandActive() && !elb.getActiveItemStack().isEmpty()) {
            Item item = elb.getActiveItemStack().getItem();

            if (item.getItemUseAction(elb.getActiveItemStack()) != EnumAction.BLOCK) {
                return false;
            } else {
                return item.getMaxItemUseDuration(elb.getActiveItemStack()) - elb.getItemInUseCount() >= 0;
            }
        } else {
            return false;
        }
    }

    public static int enchantment_getLootingLevel(EntityLivingBase living) {
        int base = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, living.getHeldItemMainhand());
        if (living instanceof EntityPlayer) {
            if (ELConfigs.lootingBonus > 0 && hasCursed((EntityPlayer) living))
                base += ELConfigs.lootingBonus;

            for (int i = 0; i < BaublesApi.getBaubles((EntityPlayer) living).getSizeInventory(); i++) {
                ItemStack bStack = BaublesApi.getBaubles((EntityPlayer) living).getStackInSlot(i);
                if (!bStack.isEmpty() && bStack.getItem() instanceof ILootingBonus) {
                    base += ((ILootingBonus) bStack.getItem()).bonusLevelLooting();
                    break;
                }
            }
        }
        return base;
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
                        endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keletu/enigmaticlegacy/asm/ELCoreTransformer", "slotBauble_isItemValid", "(Lbaubles/common/container/SlotBauble;Lnet/minecraft/item/ItemStack;)Z", false));
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
                return player.getTags().contains("hasIchorBottle");
            case 8:
                return player.getTags().contains("hasAstralFruit");
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
