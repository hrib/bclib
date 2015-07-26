package cz.vutbr.fit.xhriba01.bc.lib;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ImportDeclaration;

public class Imports {
	
	String fPackage = BinaryName.DEFAULT_PACKAGE;
	
	List<ImportDeclaration> fImports = new ArrayList<>();
	
	public Imports() {
		
	}
	
	public boolean hasDefaultPackage() {
		return fPackage.equals(BinaryName.DEFAULT_PACKAGE);
	}
	
	public void setPackage(String packagee) {
		fPackage = packagee;
	}
	
	public String getPackage() {
		return fPackage;
	}
	
	public void addImport(ImportDeclaration importt) {
		fImports.add(importt);
	}
	
	public boolean hasExplicitStaticNonStaticOnDemand(String exactName, String nonExactName) {
		
		for(ImportDeclaration imp : fImports) {
			if (imp.isOnDemand()) {
				if ((imp.getName().toString().replace('.', '/') + "/" + nonExactName).equals(exactName)) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	public boolean hasExplicitNonStaticImportEndsWith(String part) {
		for(ImportDeclaration imp : fImports) {
			if (!imp.isOnDemand() && !imp.isStatic()) {
				if (imp.getName().toString().replace('.', '/').endsWith(part)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getExplicitNonStaticImportEndsWith(String part) {
		for(ImportDeclaration imp : fImports) {
			if (!imp.isOnDemand() && !imp.isStatic()) {
				String replaced = imp.getName().toString().replace('.', '/');
				if (replaced.endsWith(part)) {
					return replaced;
				}
			}
		}
		return null;
	}
	
	public boolean hasExplicitStaticNonStaticImportEndsWith(String part) {
		for(ImportDeclaration imp : fImports) {
			if (!imp.isOnDemand()) {
				if (imp.getName().toString().replace('.', '/').endsWith(part)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getExplicitStaticNonStaticImportEndsWith(String part) {
		for(ImportDeclaration imp : fImports) {
			if (!imp.isOnDemand()) {
				String replaced = imp.getName().toString().replace('.', '/');
				if (replaced.endsWith(part)) {
					return replaced;
				}
			}
		}
		return null;
	}
	
	public boolean hasExplicitStaticNonStaticImport(BinaryName name) {
		
		for(ImportDeclaration imp : fImports) {
			if (!imp.isOnDemand()) {
				if (imp.getName().toString().replace('.', '/').equals(name.getFullName().replace('$', '/'))) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	public boolean hasExplicitStaticNonStaticImport(String name) {
		
		for(ImportDeclaration imp : fImports) {
			if (!imp.isOnDemand()) {
				if (imp.getName().toString().replace('.', '/').equals(name.replace('$', '/'))) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	public boolean hasExplicitNonStaticImport(BinaryName name) {
		
		for(ImportDeclaration imp : fImports) {
			if (!imp.isOnDemand() && !imp.isStatic()) {
				if (imp.getName().toString().replace('.', '/').equals(name.getFullName().replace('$', '/'))) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	public boolean hasExplicitNonStaticOnDemandImport(BinaryName name) {
		
		String _name = null;
		
		if (name.isTopLevel()) {
			_name = name.getPackage();
		}
		else {
			int lastIndex = name.getFullName().lastIndexOf('$');
			_name = name.getFullName().substring(0, lastIndex);
		}
		
		for(ImportDeclaration imp : fImports) {
			if (imp.isOnDemand() && !imp.isStatic()) {
				if (imp.getName().toString().replace('.', '/').equals(_name.replace('$', '/'))) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	public boolean hasExplicitStaticNonStaticOnDemandImport(BinaryName name) {
		
		String _name = null;
		
		if (name.isTopLevel()) {
			_name = name.getPackage();
		}
		else {
			int lastIndex = name.getFullName().lastIndexOf('$');
			_name = name.getFullName().substring(0, lastIndex);
		}
		
		for(ImportDeclaration imp : fImports) {
			if (imp.isOnDemand()) {
				if (imp.getName().toString().replace('.', '/').equals(_name.replace('$', '/'))) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	public boolean hasExplicitStaticNonStaticOnDemandImport(String name) {
		
		for(ImportDeclaration imp : fImports) {
			if (imp.isOnDemand()) {
				if (imp.getName().toString().replace('.', '/').equals(name.replace('$', '/'))) {
					return true;
				}
			}
		}
		
		return false;
		
	}
 }
