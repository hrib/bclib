package cz.vutbr.fit.xhriba01.bc.lib;

import org.objectweb.asm.tree.FieldNode;


public class NodeField extends Node {
	
	private FieldNode fAsmFieldNode;
	
	public NodeField(FieldNode fieldNode) {
		fType = TYPE.FIELD;
		fAsmFieldNode = fieldNode;
	}
	
	public FieldNode getAsmFieldNode() {
		return fAsmFieldNode;
	}
	
	public String getName() {
		
		return fAsmFieldNode.name;
		
	}
	
}