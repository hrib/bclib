package cz.vutbr.fit.xhriba01.bc.lib;

import org.eclipse.jdt.core.dom.FieldDeclaration;

public class FieldDescriptor {
	
	public DescriptorPart fPart;
	
	public FieldDescriptor[] a, b[] = new FieldDescriptor[][]{{null}, {null}, {null}, {null}};
	
	public static FieldDescriptor fromFieldDeclaration(FieldDeclaration fieldDeclaration, BinaryNameResolver resolver) {
		
		FieldDescriptor fieldDescriptor = new FieldDescriptor();
		
		//fieldDeclaration.ge
		
		return null;
		
	}
	
	public static FieldDescriptor fromAsmFieldDescriptor(String asmDescriptor) {
		
		FieldDescriptor fieldDescriptor = new FieldDescriptor();
		
		fieldDescriptor.fPart = DescriptorPart.fromAsmType(org.objectweb.asm.Type.getType(asmDescriptor));
		
		return fieldDescriptor;
		
	}
	
	
}
