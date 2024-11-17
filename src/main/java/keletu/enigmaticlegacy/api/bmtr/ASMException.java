package keletu.enigmaticlegacy.api.bmtr;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class ASMException extends RuntimeException {
	private static final long serialVersionUID = -8581611883691404427L;
	
	public ASMException(String message) {
		super("MaxPotionIDExtender - Class transformation error\n"+message);
	}
	
	public ASMException(String message, ClassNode node) {
		this(message+"\n"+getStringDescriptor(node));
	}
	
	public ASMException(String message, MethodNode node) {
		this(message+"\n"+getStringDescriptor(node));
	}

	private static String getStringDescriptor(MethodNode node) {
		StringBuilder sb = new StringBuilder();
		Iterator<AbstractInsnNode> i = node.instructions.iterator();
		while (i.hasNext()) {
			AbstractInsnNode n = i.next();
			sb.append("["+n.getOpcode()+"]\t"+getInsnDesc(n)+"\n");
		}
		return sb.toString();
	}

	private static String getInsnDesc(AbstractInsnNode n) {
		if (n instanceof LdcInsnNode) {
			return "LDC\t"+((LdcInsnNode)n).cst.toString();
		}
		if (n instanceof LabelNode) {
			return "Label\t"+((LabelNode)n).getLabel().toString();
		}
		if (n instanceof LineNumberNode) {
			return "Line\t"+((LineNumberNode)n).line;
		}
		if (n instanceof IntInsnNode) {
			if (n.getOpcode() == Opcodes.NEWARRAY) {
				return "New primitive array";
			}
			if (n.getOpcode() == Opcodes.ANEWARRAY) {
				return "New object array";
			}
			return "Int\t"+((IntInsnNode)n).operand;
		}
		if (n instanceof MethodInsnNode) {
			MethodInsnNode m = (MethodInsnNode)n;
			return "Method\t"+m.name+", "+m.desc+", "+m.owner;
		}
		if (n instanceof FieldInsnNode) {
			FieldInsnNode f = (FieldInsnNode) n;
			return "Field\t"+f.name+", "+f.desc;
		}
		if (n instanceof InsnNode) {
			if (n.getOpcode() == Opcodes.ICONST_0) {
				return "C-Int 0";
			}
			if (n.getOpcode() == Opcodes.ICONST_1) {
				return "C-Int 1";
			}
			if (n.getOpcode() == Opcodes.ICONST_2) {
				return "C-Int 2";
			}
			if (n.getOpcode() == Opcodes.ICONST_3) {
				return "C-Int 3";
			}
			if (n.getOpcode() == Opcodes.ICONST_4) {
				return "C-Int 4";
			}
			if (n.getOpcode() == Opcodes.ICONST_5) {
				return "C-Int 5";
			}
			if (n.getOpcode() == Opcodes.DUP) {
				return "DUP";
			}
			if (n.getOpcode() == Opcodes.IASTORE) {
				return "Store int in array";
			}
		}
		return n.getClass().getName();
	}

	private static String getStringDescriptor(ClassNode node) {
		StringBuilder sb = new StringBuilder();
		sb.append("Methods:\n");
		node.methods.forEach(m -> sb.append(m.name+": "+m.desc+"\n"));
		sb.append("Fields:\n");
		node.fields.forEach(f -> sb.append(f.name+": "+f.desc+"\n"));
		return sb.toString();
	}
}