package cz.vutbr.fit.xhriba01.bc.lib;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import cz.vutbr.fit.xhriba01.bc.lib.DefaultASTVisitor.ClassContext;

public class NodeClass extends Node {
	
	private ClassNode fAsmClassNode;
	
	private List<NodeMethod> fUnusedMethods = new ArrayList<>();
	
	private List<NodeField> fUnusedFields = new ArrayList<>();
	
	private List<InnerClassNode> fUnusedOwnInnerClasses = new ArrayList<>();
	
	public NodeClass(ClassNode asmClassNode) {
		
		fAsmClassNode = asmClassNode;
		fType = TYPE.CLASS;
		
		for(MethodNode methodNode : fAsmClassNode.methods) {
			this.fUnusedMethods.add(new NodeMethod(methodNode));
		}
		
		for(FieldNode fieldNode : fAsmClassNode.fields) {
			this.fUnusedFields.add(new NodeField(fieldNode));
		}
		
		for(InnerClassNode innerClass : fAsmClassNode.innerClasses) {
			
			String outerName = innerClass.outerName;
			
			if (outerName == null || outerName.equals(fAsmClassNode.name)) {
				fUnusedOwnInnerClasses.add(innerClass);
			}
			
		}
		
	}
	
	public ClassNode getAsmClassNode() {
		return fAsmClassNode;
	}
 	
	public InnerClassNode getUnusedInnerClass(String innerClassName) {
		
		for (InnerClassNode innerClass : fUnusedOwnInnerClasses) {
			if (innerClass.name.equals(innerClassName)) {
				return innerClass; 
			}
		}
		
		return null;
	}
	
	public List<NodeMethod> getUnusedMethods() {
		return fUnusedMethods;
	}
	
	public List<InnerClassNode> getUnusedInnerClasses() {
		return fUnusedOwnInnerClasses;
	}
	
	public void setAsUsed(InnerClassNode innerClass) {
		
		fUnusedOwnInnerClasses.remove(innerClass);
		
	}
	
	public void setAsUsed(Node node) {
		
		List<? extends Node> remaining;
		
		if (node instanceof NodeMethod) {
			remaining = fUnusedMethods;
		}
		else {
			remaining = fUnusedFields;
		}
		
		if (remaining.remove(node)) {
			this.add(node);
		}
		
	}

	public NodeField getUnusedNodeField(String name) {
		
		for (NodeField nodeField : fUnusedFields) {
			if (nodeField.getName().equals(name)) {
				return nodeField;
			}
		}
		
		return null;
		
	}
	
	public void setFromClassContext(ClassContext classContext) {
		this.setSourceOffset(classContext.getOffset());
		this.setSourceLine(classContext.getLine());
	}
	
}