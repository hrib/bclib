package cz.vutbr.fit.xhriba01.bc.lib;

import org.objectweb.asm.tree.AbstractInsnNode;

public class NodeInstruction extends Node {
	
	private AbstractInsnNode fAsmInsnNode;
	
	public NodeInstruction(AbstractInsnNode insnNode) {
		fAsmInsnNode = insnNode;
		fType = TYPE.INSTRUCTION;
	}
	
	public AbstractInsnNode getAsmInsnNode() {
		return fAsmInsnNode;
	}
	
}