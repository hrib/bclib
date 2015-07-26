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

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;


/**
 * Abstract class that clients can subclass to implement their own
 * ClassSourceResult processing logic.
 * For nodes that are instances of NodeComposite, the after method
 * is called after all their child nodes are processed.
 */
public abstract class AbstractNodeVisitor {
	
	private Node fContext;
	
	public void setActiveContext(Node newContext) {
		fContext = newContext;
	}
	
	public void removeActiveContext() {
		fContext = fContext.getParent();
	}
	
	public Node getActiveContext() {
		return fContext;
	}
	
	public void visitNodeRoot(NodeRoot nodeRoot) {}
	
	public void afterVisitNodeRoot(NodeRoot nodeRoot) {}
	
	/**
	 * Called for each node that represents a class (class, interface, enum).
	 * 
	 * @param nodeClass the class node
	 */
	public void visitNodeClass(NodeClass nodeClass) {}
	
	/**
	 * Called after the visitNodeClass and after all NodeClass childrens
	 * are processed.
	 * 
	 * @param nodeClass the class node
	 */
	public void afterVisitNodeClass(NodeClass nodeClass) {}
	
	/**
	 * Called for each node that represents a method.
	 * 
	 * @param nodeMethod the method node
	 */
	public void visitNodeMethod(NodeMethod nodeMethod) {}
	
	/**
	 * Called after the visitNodeMethod and after all NodeMethod childrens
	 * are processed.
	 * 
	 * @param nodeMethod the method node
	 */
	public void afterVisitNodeMethod(NodeMethod nodeMethod) {}
	
	/**
	 * Called for each node that represents a field.
	 * 
	 * @param nodeField the field node
	 */
	public void visitNodeField(NodeField nodeField) {}
	
	
	
	public void afterVisitNodeField(NodeField nodeField) {}
	
	/**
	 * This method detects what type of instruction the NodeInstruction is
	 * representing and calls appropriate before/visit/after methods.
	 * Subclasses could rather override methods specific for particular
	 * instruction type.
	 * 
	 * @param nodeInstruction the instruction node
	 */
	public void visitNodeInstruction(NodeInstruction nodeInstruction) {
		AbstractInsnNode asmInsn = nodeInstruction.getAsmInsnNode();
		switch (asmInsn.getType()) {
			case AbstractInsnNode.LINE:
				visitLineNumberNode((LineNumberNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.LABEL:
				visitLabelNode((LabelNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.FIELD_INSN:
				visitFieldInsn((FieldInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.IINC_INSN:
				visitIincInsn((IincInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.INSN:
				visitInsn((InsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.INT_INSN:
				visitIntInsn((IntInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
				visitInvokeDynamicInsn((InvokeDynamicInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.JUMP_INSN:
				visitJumpInsn((JumpInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.LDC_INSN:
				visitLdcInsn((LdcInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.LOOKUPSWITCH_INSN:
				visitLookupSwitchInsn((LookupSwitchInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.METHOD_INSN:
				visitMethodInsn((MethodInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.MULTIANEWARRAY_INSN:
				visitMultiANewArrayInsn((MultiANewArrayInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.TABLESWITCH_INSN:
				visitTableSwitchInsn((TableSwitchInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.TYPE_INSN:
				visitTypeInsn((TypeInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.VAR_INSN:
				visitVarInsn((VarInsnNode) asmInsn, nodeInstruction);
				break;
			default:
				// shouldn't happen
				break;
		}
	}
	
	public void afterVisitNodeInstruction(NodeInstruction nodeInstruction) {
		
		AbstractInsnNode asmInsn = nodeInstruction.getAsmInsnNode();
		
		switch (asmInsn.getType()) {
			case AbstractInsnNode.LINE:
				afterVisitLineNumberNode((LineNumberNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.LABEL:
				afterVisitLabelNode((LabelNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.FIELD_INSN:
				afterVisitFieldInsn((FieldInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.IINC_INSN:
				afterVisitIincInsn((IincInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.INSN:
				afterVisitInsn((InsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.INT_INSN:
				afterVisitIntInsn((IntInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
				afterVisitInvokeDynamicInsn((InvokeDynamicInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.JUMP_INSN:
				afterVisitJumpInsn((JumpInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.LDC_INSN:
				afterVisitLdcInsn((LdcInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.LOOKUPSWITCH_INSN:
				afterVisitLookupSwitchInsn((LookupSwitchInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.METHOD_INSN:
				afterVisitMethodInsn((MethodInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.MULTIANEWARRAY_INSN:
				afterVisitMultiANewArrayInsn((MultiANewArrayInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.TABLESWITCH_INSN:
				afterVisitTableSwitchInsn((TableSwitchInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.TYPE_INSN:
				afterVisitTypeInsn((TypeInsnNode) asmInsn, nodeInstruction);
				break;
			case AbstractInsnNode.VAR_INSN:
				afterVisitVarInsn((VarInsnNode) asmInsn, nodeInstruction);
				break;
			default:
				// shouldn't happen
				break;
		}
		
	}
	
	/**
	 * When called, indicates that subsequent instructions are generated
	 * from line that is represented by this LineNumberNode.
	 * It is not real jvm instruction.
	 * 
	 * @param node the asm node
	 * @param nodeInstruction the node
 	 */
	protected void visitLineNumberNode(LineNumberNode node, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitLineNumberNode(LineNumberNode node, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each start of new instruction.
	 * 
	 * @param node the asm node
	 * @param nodeInstruction the node
	 */
	protected void visitLabelNode(LabelNode node, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitLabelNode(LabelNode node, NodeInstruction nodeInstruction) {}
	
	
	/**
	 * Called for each instruction that manipulates object fields.
	 * <b>Possible instructions:</b>
	 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.getstatic">getstatic</a> 
	 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.putstatic">putstatic</a>
	 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.getfield">getfield</a>
	 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.putfield">putfield</a>
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitFieldInsn(FieldInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitFieldInsn(FieldInsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each instruction that increments local int variable.
	 * <b>Possible instructions:</b>
	 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.iinc">iinc</a>
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitIincInsn(IincInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitIincInsn(IincInsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each instruction that has zero operands.
	 * <b>Possible instructions:</b>
	 * @see <a href="">nop</a>
	 * @see <a href="">aconst_null</a>
	 * @see <a href="">iconst_m1</a>
	 * @see <a href="">iconst_0</a>
	 * @see <a href="">iconst_1</a>
	 * @see <a href="">iconst_2</a>
	 * @see <a href="">iconst_3</a>
	 * @see <a href="">iconst_4</a>
	 * @see <a href="">iconst_5</a>
	 * @see <a href="">lconst_0</a>
	 * @see <a href="">lconst_1</a>
	 * @see <a href="">fconst_0</a>
	 * @see <a href="">fconst_1</a>
	 * @see <a href="">fconst_2</a>
	 * @see <a href="">dconst_0</a>
	 * @see <a href="">dconst_1</a>
	 * @see <a href="">iaload</a>
	 * @see <a href="">laload</a>
	 * @see <a href="">faload</a>
	 * @see <a href="">daload</a>
	 * @see <a href="">aaload</a>
	 * @see <a href="">baload</a>
	 * @see <a href="">caload</a>
	 * @see <a href="">saload</a>
	 * @see <a href="">iastore</a>
	 * @see <a href="">lastore</a>
	 * @see <a href="">fastore</a>
	 * @see <a href="">dastore</a>
	 * @see <a href="">aastore</a>
	 * @see <a href="">bastore</a>
	 * @see <a href="">castore</a>
	 * @see <a href="">sastore</a>
	 * @see <a href="">pop</a>
	 * @see <a href="">pop2</a>
	 * @see <a href="">dup</a>
	 * @see <a href="">dup_x1</a>
	 * @see <a href="">dup_x2</a>
	 * @see <a href="">dup2</a>
	 * @see <a href="">dup2_x1</a>
	 * @see <a href="">dup2_x2</a>
	 * @see <a href="">swap</a>
	 * @see <a href="">iadd</a>
	 * @see <a href="">ladd</a>
	 * @see <a href="">fadd</a>
	 * @see <a href="">dadd</a>
	 * @see <a href="">isub</a>
	 * @see <a href="">lsub</a>
	 * @see <a href="">fsub</a>
	 * @see <a href="">dsub</a>
	 * @see <a href="">imul</a>
	 * @see <a href="">lmul</a>
	 * @see <a href="">fmul</a>
	 * @see <a href="">dmul</a>
	 * @see <a href="">idiv</a>
	 * @see <a href="">ldiv</a>
	 * @see <a href="">ddiv</a>
	 * @see <a href="">irem</a>
	 * @see <a href="">lrem</a>
	 * @see <a href="">frem</a>
	 * @see <a href="">drem</a>
	 * @see <a href="">ineg</a>
	 * @see <a href="">lneg</a>
	 * @see <a href="">fneg</a>
	 * @see <a href="">dneg</a>
	 * @see <a href="">ishl</a>
	 * @see <a href="">lshl</a>
	 * @see <a href="">ishr</a>
	 * @see <a href="">lshr</a>
	 * @see <a href="">iushr</a>
	 * @see <a href="">lushr</a>
	 * @see <a href="">iand</a>
	 * @see <a href="">land</a>
	 * @see <a href="">ior</a>
	 * @see <a href="">lor</a>
	 * @see <a href="">ixor</a>
	 * @see <a href="">lxor</a>
	 * @see <a href="">i2l</a>
	 * @see <a href="">i2f</a>
	 * @see <a href="">i2d</a>
	 * @see <a href="">l2i</a>
	 * @see <a href="">l2f</a>
	 * @see <a href="">l2d</a>
	 * @see <a href="">f2i</a>
	 * @see <a href="">f2l</a>
	 * @see <a href="">f2d</a>
	 * @see <a href="">d2i</a>
	 * @see <a href="">d2l</a>
	 * @see <a href="">d2f</a>
	 * @see <a href="">i2b</a>
	 * @see <a href="">i2c</a>
	 * @see <a href="">i2s</a>
	 * @see <a href="">lcmp</a>
	 * @see <a href="">fcmpl</a>
	 * @see <a href="">fcmpg</a>
	 * @see <a href="">dcmpl</a>
	 * @see <a href="">dcmpg</a>
	 * @see <a href="">ireturn</a>
	 * @see <a href="">lreturn</a>
	 * @see <a href="">freturn</a>
	 * @see <a href="">dreturn</a>
	 * @see <a href="">areturn</a>
	 * @see <a href="">return</a>
	 * @see <a href="">arraylenght</a>
	 * @see <a href="">athrow</a>
	 * @see <a href="">monitorenter</a>
	 * @see <a href="">monitorexit</a>
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitInsn(InsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitInsn(InsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each instruction that has single int operand.
	 * <b>Possible instructions:</b>
	 * @see <a href="">bipush</a>
	 * @see <a href="">sipush</a>
	 * @see <a href="">newarray</a>
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitIntInsn(IntInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitIntInsn(IntInsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each invokedynamic instruction.
	 * <b>Possible instructions:</b>
	 * @see <a href="">invokedynamic</a>
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitInvokeDynamicInsn(InvokeDynamicInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitInvokeDynamicInsn(InvokeDynamicInsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each instruction that may jump to another instruction.
	 * <b>Possible instructions:</b>
	 * @see <a href="">ifeq</a>
	 * @see <a href="">ifne</a>
	 * @see <a href="">iflt</a>
	 * @see <a href="">ifge</a>
	 * @see <a href="">ifgt</a>
	 * @see <a href="">ifle</a>
	 * @see <a href="">if_icmpeq</a>
	 * @see <a href="">if_icmpne</a>
	 * @see <a href="">if_icmplt</a>
	 * @see <a href="">if_icmpge</a>
	 * @see <a href="">if_icmpgt</a>
	 * @see <a href="">if_icmple</a>
	 * @see <a href="">if_acmpeq</a>
	 * @see <a href="">if_acmpne</a>
	 * @see <a href="">goto</a>
	 * @see <a href="">jsr</a>
	 * @see <a href="">ifnull</a>
	 * @see <a href="">ifnonnull</a>
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */	
	protected void visitJumpInsn(JumpInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitJumpInsn(JumpInsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each ldc instruction.
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitLdcInsn(LdcInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitLdcInsn(LdcInsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each lookupswitch instruction.
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitLookupSwitchInsn(LookupSwitchInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitLookupSwitchInsn(LookupSwitchInsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each instruction that represents instruction for method invocation.
	 * <b>Possible instructions:</b>
	 * @see <a href="">invokevirtual</a>
	 * @see <a href="">invokespecial</a>
	 * @see <a href="">invokestatic</a>
	 * @see <a href="">invokeinterface</a>
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitMethodInsn(MethodInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitMethodInsn(MethodInsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each multianewarray instruction.
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitMultiANewArrayInsn(MultiANewArrayInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitMultiANewArrayInsn(MultiANewArrayInsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each tableswitch instruction.
	 * 
	 * @param insn the asm instriction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitTableSwitchInsn(TableSwitchInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitTableSwitchInsn(TableSwitchInsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each instruction that takes a type descriptor as parameter.
	 * <b>Possible instructions:</b>
	 * @see <a href="">new</a>
	 * @see <a href="">anewarray</a>
	 * @see <a href="">checkcast</a>
	 * @see <a href="">instanceof</a>
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitTypeInsn(TypeInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitTypeInsn(TypeInsnNode insn, NodeInstruction nodeInstruction) {}
	
	/**
	 * Called for each instruction that loads or stores the value of a local variable.
	 * <b>Possible instructions:</b>
	 * @see <a href="">iload</a>
	 * @see <a href="">lload</a>
	 * @see <a href="">fload</a>
	 * @see <a href="">dload</a>
	 * @see <a href="">aload</a>
	 * @see <a href="">istore</a>
	 * @see <a href="">lstore</a>
	 * @see <a href="">fstore</a>
	 * @see <a href="">dstore</a>
	 * @see <a href="">astore</a>
	 * @see <a href="">ret</a>
	 * 
	 * @param insn the asm instruction node
	 * @param nodeInstruction the instruction node
	 */
	protected void visitVarInsn(VarInsnNode insn, NodeInstruction nodeInstruction) {}
	
	protected void afterVisitVarInsn(VarInsnNode insn, NodeInstruction nodeInstruction) {}
	
	
	public void visitTryCatchBlocks(List<TryCatchBlockNode> tcbBlocks) {}
}
