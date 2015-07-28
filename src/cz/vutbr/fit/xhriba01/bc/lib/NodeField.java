package cz.vutbr.fit.xhriba01.bc.lib;

import org.objectweb.asm.tree.FieldNode;

import cz.vutbr.fit.xhriba01.bc.lib.DefaultASTVisitor.FieldContext;


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
	
	public void setFromFieldContext(FieldContext fieldContext) {
		
		this.setSourceLine(fieldContext.getLine());
		this.setSourceOffset(fieldContext.getOffset());
		
	}
	
}