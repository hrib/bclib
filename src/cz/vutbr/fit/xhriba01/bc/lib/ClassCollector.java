package cz.vutbr.fit.xhriba01.bc.lib;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ImportDeclaration;

public class ClassCollector extends DefaultASTVisitor {
	
	private Map<String, ClassContext> fContextMap = new HashMap<>();
	
	private Imports fImports = new Imports();
	
	@Override
	protected void onContextActivated(Context context) {
		super.onContextActivated(context);
		
		if (context.isClassContext()) {
			
			ClassContext classContext = (ClassContext) context;
			
			fContextMap.put(classContext.getBinaryName().getClassName(), classContext);
		}
	}
	
	public ClassContext getContext(ClassContext classContext) {
		
		return fContextMap.get(classContext.getBinaryName().getClassName());
		
	}
	
	public Imports getImports() {
		return fImports;
	}
	
	public boolean visit(ImportDeclaration node) {
		super.visit(node);
		
		
		return true;
	}
	
}
