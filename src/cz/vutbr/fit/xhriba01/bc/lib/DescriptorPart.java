package cz.vutbr.fit.xhriba01.bc.lib;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;


public class DescriptorPart {

	public enum TYPE {
		UNDEF,
		PRIMITIVE,
		OBJECT,
		ARRAY_OBJECT,
		ARRAY_PRIMITIVE
	}
	
	public enum HINT {
		NONE,
		EXACT
	}
	
	public HINT fHint = HINT.NONE;
	
	public TYPE fType = TYPE.UNDEF;
	
	public PRIMITIVE_TYPE fPrimitiveType;
	
	public String fName;
	
	int fDimensions = -1;
	
	public enum PRIMITIVE_TYPE {
		BYTE,
		BOOLEAN,
		SHORT,
		CHAR,
		INT,
		FLOAT,
		DOUBLE,
		LONG,
		VOID
	}
	
	public TYPE getType() {
		return fType;
	}
	
	public boolean isArray() {
		return fType == TYPE.ARRAY_PRIMITIVE || fType == TYPE.ARRAY_OBJECT;
	}
	
	public int getDimensions() {
		return fDimensions;
	}
	
	private void setPrimitiveType(PrimitiveType type) {
		PrimitiveType.Code code = type.getPrimitiveTypeCode();
		if (code == PrimitiveType.BYTE) {
			fPrimitiveType = PRIMITIVE_TYPE.BYTE;
		}
		else if (code == PrimitiveType.SHORT) {
			fPrimitiveType = PRIMITIVE_TYPE.SHORT;
		}
		else if (code == PrimitiveType.CHAR) {
			fPrimitiveType = PRIMITIVE_TYPE.CHAR;
		}
		else if (code == PrimitiveType.INT) {
			fPrimitiveType = PRIMITIVE_TYPE.INT;
		}
		else if (code == PrimitiveType.LONG) {
			fPrimitiveType = PRIMITIVE_TYPE.LONG;
		}
		else if (code == PrimitiveType.FLOAT) {
			fPrimitiveType = PRIMITIVE_TYPE.FLOAT;
		}
		else if (code == PrimitiveType.DOUBLE) {
			fPrimitiveType = PRIMITIVE_TYPE.DOUBLE;
		}
		else if (code == PrimitiveType.BOOLEAN) {
			fPrimitiveType = PRIMITIVE_TYPE.BOOLEAN;
		}
		else if (code == PrimitiveType.VOID) {
			fPrimitiveType = PRIMITIVE_TYPE.VOID;
		}
	}
	
	public static DescriptorPart fromAsmType(org.objectweb.asm.Type type) {
		
		DescriptorPart mdp = new DescriptorPart();
		
		mdp.fHint = HINT.EXACT;
		
		int sort = type.getSort();
		
		if (sort == org.objectweb.asm.Type.ARRAY) {
			mdp.fDimensions = type.getDimensions();
			mdp.setFromAsmArrayType(type);
		}
		else if (sort == org.objectweb.asm.Type.OBJECT) {
			mdp.setFromAsmObjectType(type);
		}
		else {
			mdp.setFromAsmPrimitiveType(type);
		}
		
		return mdp;
	}
	
	private void setFromAsmArrayType(org.objectweb.asm.Type type) {
		
		org.objectweb.asm.Type elementType = type.getElementType();
		
		if (elementType.getSort() == org.objectweb.asm.Type.OBJECT) {
			setFromAsmObjectType(elementType);
			fType = TYPE.ARRAY_OBJECT;
			fName = fName.substring(fDimensions+1, fName.length());  // asm vraci pro pole tu formu [Ljava/lang/Object;
			fName = fName.substring(0, fName.length()-1);
		}
		else {
			setFromAsmPrimitiveType(elementType);
			fType = TYPE.ARRAY_PRIMITIVE;
		}
	}
	
	private void setFromAsmObjectType(org.objectweb.asm.Type type) {
		
		fType = TYPE.OBJECT;
		
		fName = type.getInternalName();
		
	}
	
	private void setFromAsmPrimitiveType(org.objectweb.asm.Type type) {
		
		fType = TYPE.PRIMITIVE;
		
		switch(type.getSort()) {
		case org.objectweb.asm.Type.BYTE:
			fPrimitiveType = PRIMITIVE_TYPE.BYTE;
			break;
		case org.objectweb.asm.Type.SHORT:
			fPrimitiveType = PRIMITIVE_TYPE.SHORT;
			break;
		case org.objectweb.asm.Type.CHAR:
			fPrimitiveType = PRIMITIVE_TYPE.CHAR;
			break;
		case org.objectweb.asm.Type.INT:
			fPrimitiveType = PRIMITIVE_TYPE.INT;
			break;
		case org.objectweb.asm.Type.LONG:
			fPrimitiveType = PRIMITIVE_TYPE.LONG;
			break;
		case org.objectweb.asm.Type.FLOAT:
			fPrimitiveType = PRIMITIVE_TYPE.FLOAT;
			break;
		case org.objectweb.asm.Type.DOUBLE:
			fPrimitiveType = PRIMITIVE_TYPE.DOUBLE;
			break;
		case org.objectweb.asm.Type.BOOLEAN:
			fPrimitiveType = PRIMITIVE_TYPE.BOOLEAN;
			break;
		case org.objectweb.asm.Type.VOID:
			fPrimitiveType = PRIMITIVE_TYPE.VOID;
			break;
		}
		
	}
	
	public static DescriptorPart fromPrimitiveType(PrimitiveType type) {
		
		DescriptorPart mdp = new DescriptorPart();
		
		mdp.fType = TYPE.PRIMITIVE;
		mdp.fHint = HINT.EXACT;
		
		mdp.setPrimitiveType(type);
		
		return mdp;
	}
	
	public static DescriptorPart fromArrayType(ArrayType type, BinaryNameResolver resolver) {
		DescriptorPart mdp = new DescriptorPart();
		
		mdp.fDimensions = type.getDimensions();
		
		Type elementType = type.getElementType(); 
		
		if (elementType.isPrimitiveType()) {
			mdp.fType = TYPE.ARRAY_PRIMITIVE;
			mdp.fHint = HINT.EXACT;
			mdp.setPrimitiveType((PrimitiveType)elementType);
		}
		else {
			mdp.fType = TYPE.ARRAY_OBJECT;
			mdp.setSimpleType((SimpleType)elementType, resolver);
		}
		
		return mdp;
	}
	
	private void setSimpleType(SimpleType type, BinaryNameResolver resolver) {
		
		String found = resolver.findBinaryNameForSimpleName(type.getName().toString());
		
		if (found != null) {
			fHint = HINT.EXACT;
			fName = found;
		}
		else {
			fName = type.getName().toString();
		}
		
	}
	
	public static DescriptorPart fromSimpleType(SimpleType type, BinaryNameResolver resolver) {
		
		DescriptorPart mdp = new DescriptorPart();
		mdp.fType = TYPE.OBJECT;
		
		mdp.setSimpleType(type, resolver);
		
		return mdp;
	}
	
	public String getTypeString() {
		
		if (fType == TYPE.PRIMITIVE || fType == TYPE.ARRAY_PRIMITIVE) {
			
			switch(fPrimitiveType) {
				case BYTE:
					return "byte";
				case BOOLEAN:
					return "boolean";
				case SHORT:
					return "short";
				case CHAR:
					return "char";
				case INT:
					return "int";
				case FLOAT:
					return "float";
				case DOUBLE:
					return "double";
				case LONG:
					return "long";
				case VOID:
					return "void";
			}
			
			return "";
		}
		else {
			return fName;
		}
		
	}
	
	public String getDimensionsString() {
		
		if (fDimensions == -1) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < fDimensions; i++) {
			sb.append("[]");
		}
		
		return sb.toString();
		
	}
	
	public String toString(boolean dimensions) {
		
		String type = getTypeString();
		
		if (dimensions) {
			type += getDimensionsString();
		}
		
		return type;
	}
	
}
