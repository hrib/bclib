package cz.vutbr.fit.xhriba01.bc.lib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

public class NodeMethod extends Node {
	
	private MethodNode fAsmMethodNode;
	
	private Map<LabelNode, List<TryCatchBlockNode>> fTryCatchBlocks;
	
	private Map<TryCatchBlockNode, Integer> fTcbIndexMap;
	
	private int fNextTcb = 0;
	
	public NodeMethod(MethodNode asmMethodNode) {
		
		fAsmMethodNode = asmMethodNode;
		fType = TYPE.METHOD;
		
		addInstructions();
		
		handleTryCatchBlocks();
	}
	
	public int getNextTcbIndexAndInc() {
		return ++fNextTcb;
	}
	
	public void addToTcbIndexMap(TryCatchBlockNode tbcNode, int index) {
		
		
		
	}
	
	private void addInstructions() {
		
		int currentLine = -1;
		
		for (Iterator<AbstractInsnNode> it = fAsmMethodNode.instructions.iterator() ; it.hasNext(); ) {
			
			AbstractInsnNode insnNode = it.next();
			
			if (insnNode.getType() == AbstractInsnNode.LINE) {
				
				currentLine = ((LineNumberNode)insnNode).line;
			}
			
			NodeInstruction nodeInstruction = new NodeInstruction(insnNode);
			
			nodeInstruction.setStartLine(currentLine);
			
			this.add(nodeInstruction);
		}
	}
	
	public List<TryCatchBlockNode> getTryCatchBlocksForStartLabelNode(LabelNode start) {
		if (fTryCatchBlocks == null) {
			return Collections.emptyList();
		}
		else {
			List<TryCatchBlockNode> found = fTryCatchBlocks.get(start);
			if (found == null) {
				return Collections.emptyList();
			}
			return found;
		}
	}
	
	private void handleTryCatchBlocks() {
		
		if (fAsmMethodNode.tryCatchBlocks != null && fAsmMethodNode.tryCatchBlocks.size() > 0) {
			
			fTryCatchBlocks = new HashMap<LabelNode, List<TryCatchBlockNode>>();
		}
		else {
			return;
		}
		
		for (TryCatchBlockNode tcbNode : fAsmMethodNode.tryCatchBlocks) {
			
			List<TryCatchBlockNode> list = fTryCatchBlocks.get(tcbNode.start);
			
			if (list == null) {
				
				list = new LinkedList<TryCatchBlockNode>();
				
				list.add(tcbNode);
				
				fTryCatchBlocks.put(tcbNode.start, list);
			}
			else {
				list.add(tcbNode);
			}
			
		}
		
	}
	
	public MethodNode getAsmMethodNode() {
		return fAsmMethodNode;
	}
	
	public boolean isStatic() {
		return ((fAsmMethodNode.access & Opcodes.ACC_STATIC) != 0);
	}
	
}