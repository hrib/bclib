package cz.vutbr.fit.xhriba01.bc.lib;

import cz.vutbr.fit.xhriba01.bc.lib.DefaultASTVisitor.ClassContext;
import cz.vutbr.fit.xhriba01.bc.lib.DefaultASTVisitor.Context;
import cz.vutbr.fit.xhriba01.bc.lib.DefaultASTVisitor.RootContext;

public class BinaryNameResolver {
	
	private ClassContext fClassContext;
	
	private Imports fImports;
	
	private Context fRootContext;
	
	public BinaryNameResolver(ClassContext classContext, Imports imports) {
		
		fClassContext = classContext;
		fImports = imports;
		
		fRootContext = classContext;
		
		do {
			
			fRootContext = fRootContext.getParent();
			
		} while(!(fRootContext instanceof RootContext));
		
	}
	
	public String findBinaryNameForSimpleName(String simpleName) {
		
		ClassContext found = fClassContext.findClass(simpleName);
		
		if (found != null) {
			return found.getBinaryName().getFullName();
		}
		
		
		return null;
		
	}
	
	public Imports getImports() {
		return fImports;
	}
	
	public Context getRootContext() {
		return fRootContext;
	}
 	
}
