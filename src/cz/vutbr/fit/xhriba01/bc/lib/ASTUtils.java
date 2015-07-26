package cz.vutbr.fit.xhriba01.bc.lib;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ASTUtils {

	public static String getFieldName(VariableDeclarationFragment node) {
		
		return node.getName().toString();
	}
	
	public static String getMethodName(MethodDeclaration node) {
		
		return node.getName().toString();
	}
	
	public static String getAnnotationName(AnnotationTypeDeclaration node) {
		
		return node.getName().toString();
	}
	
	public static String getEnumName(EnumDeclaration node) {
		
		return node.getName().toString();
	}
	
	public static String getTypeName(TypeDeclaration node) {
		
		return node.getName().toString();
	}
	
	/**
	 * Returns first parent that is instance of class0
	 * 
	 * @param class0 expected class of ancestor
	 * @param start start element exclusive
	 * @param stop stop element exclusive
	 * @return T when found or null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAstAncestor(Class<T> class0, ASTNode start, ASTNode stop) {
		
		while(((start = start.getParent()) != null) && stop != start) {
			
			if (start.getClass().equals(class0)) {
				return (T) start;
			}
			
		}
		
		return null;
	}
}
