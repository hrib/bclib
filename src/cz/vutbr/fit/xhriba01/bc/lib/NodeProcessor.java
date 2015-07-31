/*******************************************************************************
 * Copyright (c) 2014 Jaromír Hřibal.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jaromír Hřibal <jaromirhribal@gmail.com> - initial API and implementation
 *******************************************************************************/
package cz.vutbr.fit.xhriba01.bc.lib;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

/**
 * It is used for processing the ClassSourceResult.
 * It calls the visitor method whenever it should.
 *
 * @param <T> visitor class
 */
public class NodeProcessor {
	
	private Stack<Node> fNodes;
	
	private Node fRoot;
	
	private AbstractNodeVisitor fVisitor;
	
	//private NodeProcessor[] bla = {new NodeProcessor(null, null) {}, null, new NodeProcessor(null, null) {}};
	
	private NodeProcessor(Node node, AbstractNodeVisitor visitor) {
		fNodes = new Stack<>();
		fRoot = node;
		fVisitor = visitor;
	}
	
	/**
	 * It is pushed onto stack when there is a need to restore the
	 * contained node for calling visitor after method.
	 */
	static class AfterVisitNode extends Node {
		
		/**
		 * Constructor
		 * 
		 * @param node the node that will be later needed
		 */
		
		public Node fNode;
		
		public AfterVisitNode(Node node) { 
			fNode = node;
		}
		
	}
	
	public static void process(Node node, AbstractNodeVisitor visitor) {
		
		NodeProcessor processor = new NodeProcessor(node, visitor);
		
		processor._process();
		
	}
	/**
	 * When called, it process the ClassSourceResult and calls its visit methods.
	 * <b>Basic pseudocode:</b>
	 * <code>
	 * Stack stack
	 * stack.push(root)
	 * while (stack.notEmpty()) do
	 * 	node = stack.pop()
	 *  processNode(node)
	 *  foreach(node.childs() as child) do
	 *   stack.push(child) 
	 *  end
	 * end 
	 * </code>
	 * 
	 * @return visitor
	 */
	private void _process() {
		
		Node rootNode = fRoot;
		
		fNodes.push(rootNode);
		
		do {
			
			Node currentNode = fNodes.pop();
			
			if (currentNode == null) continue;
			
			boolean after = false;
			
			if (currentNode instanceof AfterVisitNode) {
				
				currentNode = ((AfterVisitNode) currentNode).fNode;
				after = true;
				
				if (currentNode.getType() == Node.TYPE.CLASS) {
					NodeClass nodeClass = ((NodeClass) currentNode);
					if (nodeClass.hasUnuseds()) {
						
						fNodes.push(new AfterVisitNode(currentNode));
						
						for(NodeMethod unusedMethod : nodeClass.getUnusedMethods()) {
							fNodes.push(unusedMethod);
						}
						
						for(NodeField unusedField : nodeClass.getUnusedFields()) {
							fNodes.push(unusedField);
						}
						
						nodeClass.cleanUnuseds(true);
						continue;
					}
				}
				
				if (currentNode.getType() != Node.TYPE.INSTRUCTION || currentNode.hasChilds()) {
						
					fVisitor.removeActiveContext();
						
				}
					
			}
			else {
				currentNode.sort();
			}
			
			switch (currentNode.getType()) {
			
				case CLASS:
					NodeClass nodeClass = (NodeClass) currentNode;
					if (after) {
						fVisitor.afterVisitNodeClass(nodeClass);
					}
					else {
						fVisitor.visitNodeClass(nodeClass);
					}
					break;
					
				case METHOD:
					NodeMethod nodeMethod = (NodeMethod) currentNode;
					if (after) {
						fVisitor.afterVisitNodeMethod(nodeMethod);
					}
					else {
						
						fVisitor.visitNodeMethod(nodeMethod);
					}
					break;
					
				case FIELD:
					NodeField nodeField = (NodeField) currentNode;
					if (after) {
						fVisitor.afterVisitNodeField(nodeField);
					}
					else {
						fVisitor.visitNodeField(nodeField);
					}
					break;
					
				case INSTRUCTION:
					NodeInstruction nodeInstruction = (NodeInstruction) currentNode;
					if (after) {
						fVisitor.afterVisitNodeInstruction(nodeInstruction);
					}
					else {
						if (nodeInstruction.getAsmInsnNode() instanceof LabelNode) {
							
							LabelNode labelNode = (LabelNode) nodeInstruction.getAsmInsnNode();
							
							NodeMethod insnMethod = (NodeMethod) nodeInstruction.getParent();
							
							List<TryCatchBlockNode> tcbBlocks = new LinkedList<TryCatchBlockNode>(insnMethod.getTryCatchBlocksForStartLabelNode(labelNode));
							
							while (tcbBlocks.size() > 0) {
								
								List<TryCatchBlockNode> tcbForVisitor = new ArrayList<TryCatchBlockNode>();
								
								int maxEndOffset = getMaxEndOffset(tcbBlocks);
								
								int size = tcbBlocks.size();
								
								for (int i = 0; i < size;) {
									
									TryCatchBlockNode tcbNode =	tcbBlocks.get(i);
									
									int endOffset = tcbNode.end.getLabel().getOffsetInMethod();
									
									if (maxEndOffset == endOffset) {
										tcbForVisitor.add(tcbNode);
										tcbBlocks.remove(i);
										size--;
									}
									else {
										i++;
									}
									
								}
								
								fVisitor.visitTryCatchBlocks(tcbForVisitor);
								
							}
							
						}
						fVisitor.visitNodeInstruction(nodeInstruction);
					}
					break;
					
				case ROOT:
					if (after) {
						fVisitor.afterVisitNodeRoot((NodeRoot) currentNode);
					}
					else {
						fVisitor.visitNodeRoot((NodeRoot) currentNode);
					}
					break;
				default:
					break;
			}
			
			if (after == false) {
					
					fNodes.push(new AfterVisitNode(currentNode));
				
					if (currentNode.getType() != Node.TYPE.INSTRUCTION || currentNode.hasChilds()) {
						
						fVisitor.setActiveContext(currentNode);
					}
					
					pushChilds(currentNode);
			}
			
		} while (!fNodes.isEmpty());
		
	}
	
	public int getMaxEndOffset(List<TryCatchBlockNode> nodes) {
		
		int max = -1;
		
		for (TryCatchBlockNode node : nodes) {
			
			int offset = node.end.getLabel().getOffsetInMethod();
			
			if (offset > max) {
				max = offset;
			}
		}
		
		return max;
	}
	
	/**
	 * Pushes node children's to node stack.
	 * 
	 * @param nodes node stack
	 * @param node node its childrens are added to stack
	 */
	private void pushChilds(Node node) {
		
		if (node.hasChilds()) {
			
			List<Node> childs = node.getChilds();
			
			for (int i = childs.size()-1; i >=0; i--) {
				fNodes.push(childs.get(i));
			}
			
		}
	}
	
}
