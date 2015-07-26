package cz.vutbr.fit.xhriba01.bc.lib;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

import cz.vutbr.fit.xhriba01.bc.lib.DefaultASTVisitor.ClassContext;
import cz.vutbr.fit.xhriba01.bc.lib.DefaultASTVisitor.Context;

public class MethodDescriptor {
	
	public List<DescriptorPart> fParameters = new ArrayList<>();
	
	public DescriptorPart fReturnType;
	
	public static MethodDescriptor fromAsmMethodDescriptor(String desc) {
		
		MethodDescriptor md = new MethodDescriptor();
		
		org.objectweb.asm.Type[] asmTypes = org.objectweb.asm.Type.getArgumentTypes(desc);
		
		for(org.objectweb.asm.Type asmType : asmTypes) {
			 md.fParameters.add(DescriptorPart.fromAsmType(asmType));
		}
		
		md.fReturnType = DescriptorPart.fromAsmType(org.objectweb.asm.Type.getReturnType(desc));
		
		return md;
		
	}
	
	public int match(MethodDescriptor other, BinaryNameResolver resolver) {
		
		if (other.fParameters.size() != fParameters.size()) {
			// different argument count -> fail
			return -1;
		}
		
		// check every pair of arguments
		for (int i = other.fParameters.size()-1; i>=0; i--) {
			DescriptorPart mdpOther = other.fParameters.get(i);
			DescriptorPart mdpMy = fParameters.get(i);
			
			if (mdpOther.fType != mdpMy.fType) {
				// different basic types (for instance PRIMITIVE and OBJECT) -> fail
				return -1;
			}
			
			// now we know they have same basic type
			
			if(mdpOther.fType == DescriptorPart.TYPE.PRIMITIVE) {
				if (mdpOther.fPrimitiveType != mdpMy.fPrimitiveType) {
					// different primitive types -> fail
					return -1;
				}
				// ok, same primitive types
			}
			else if(mdpOther.fType == DescriptorPart.TYPE.ARRAY_PRIMITIVE) {
				if (mdpOther.fDimensions != mdpMy.fDimensions) {
					// different dimensions -> fail
					return -1;
				}
				if (mdpOther.fPrimitiveType != mdpMy.fPrimitiveType) {
					// different array element primitive types -> fail
					return -1;
				}
				// ok, same array element primitive types
			}
			else if (mdpOther.fType == DescriptorPart.TYPE.OBJECT) {
				DescriptorPart.HINT otherHint = mdpOther.fHint;
				DescriptorPart.HINT myHint = mdpMy.fHint;
				
				if (otherHint == DescriptorPart.HINT.EXACT) {
					if (myHint == DescriptorPart.HINT.EXACT) {
						if (mdpOther.fName.equals(mdpMy.fName) == false) {
							// both exact but binaryname doesn't match -> fail
							return -1; 
						}
						// ok exact binarynames matched
					}
					else {
						int exactNonExactMatch = matchExactNonExactMdp(mdpOther, mdpMy, resolver);
						if (exactNonExactMatch < 0) return exactNonExactMatch; 
					}
				}
				else {
					int exactNonExactMatch = matchExactNonExactMdp(mdpMy, mdpOther, resolver);
					if (exactNonExactMatch < 0) return exactNonExactMatch; 
				}
			}
			else {
				//must be object array
				if (mdpOther.fDimensions != mdpMy.fDimensions) {
					// different dimensions -> fail
					return -1;
				}
				// dimensions are ok
				DescriptorPart.HINT otherHint = mdpOther.fHint;
				DescriptorPart.HINT myHint = mdpMy.fHint;
				
				if (otherHint == DescriptorPart.HINT.EXACT) {
					if (myHint == DescriptorPart.HINT.EXACT) {
						if (mdpOther.fName.equals(mdpMy.fName) == false) {
							// both exact but binaryname doesn't match -> fail
							return -1; 
						}
						// ok exact binarynames matched
					}
					else {
						int exactNonExactMatch = matchExactNonExactMdp(mdpOther, mdpMy, resolver);
						if (exactNonExactMatch < 0) return exactNonExactMatch; 
					}
				}
				else {
					int exactNonExactMatch = matchExactNonExactMdp(mdpMy, mdpOther, resolver);
					if (exactNonExactMatch < 0) return exactNonExactMatch; 
				}
			}
		}
		
		return 0;
	}
	
	private static int matchExactNonExactMdp(DescriptorPart exactMdp, DescriptorPart nonExactMdp, BinaryNameResolver resolver) {
		
		Imports imps = resolver.getImports();
		
		BinaryName exactName = new BinaryName(exactMdp.fName);
		
		String[] nonExactNames = nonExactMdp.fName.split("\\.");
		
		boolean isSimpleName = (nonExactNames.length == 1);
		
		String firstName = nonExactNames[0];
		
		if (exactName.isNonExactLocal()) {
			// jestli je trida nejaka lokalni, uz je to fail, protoze to by jinak bylo exact to exact
			return -1;
		}
		
		if (exactName.isExactInnerOrStaticNested() && exactName.getPackage().equals(imps.fPackage)) {
			// vnitrni nebo staticka vnorena je ze stejneho balicku -> nemela by ale byt z CU, kterou zpracovavam
			for (Context rootClassContext : resolver.getRootContext()) {
				
				ClassContext classInCU = (ClassContext) rootClassContext;
				
				if (classInCU.getBinaryName().getClassName().equals(exactName.getClassName())) {
					return -1;
				}
			}
		}
		
		String exactFullNameReplaced = exactName.getFullName().replace('$', '/');
		
		String nonExactNameReplaced = nonExactMdp.fName.replace('.', '/');
		
		if (exactFullNameReplaced.endsWith(nonExactNameReplaced) == false) {
			// pokud FQN nema jako suffix nonExactName (napr. org/objectweb/asm$Top$Inner a Inner, nebo Top.Inner apod)
			// tak uz je to spatne
			return -1;
		}
		
		// buď má CU přímo  single import (import + vsechny casti krome prvni musi dat FQN)
		
		String singleStaticNonStaticImport = imps.getExplicitStaticNonStaticImportEndsWith(firstName);
		String singleNonStaticImport = imps.getExplicitNonStaticImportEndsWith(firstName);
		
		
		if (exactName.isTopLevel()) {
			if (singleNonStaticImport == null && singleStaticNonStaticImport != null) {
				// pokud se je FQN top level trida a zaroven ma CU staticky single import
				// na firstName, tak uz je to chyba
				return -1;
			}
		}
		
		
		if (singleStaticNonStaticImport != null) {
			
			String singleStaticNonStaticImportWithNonExactName = singleStaticNonStaticImport.replace('.', '/');
			
			if (exactName.getPackage().startsWith(singleStaticNonStaticImportWithNonExactName)) {
				// single import na balicek? FAIL
				return -1;
			}
			
			if (nonExactNames.length > 1) {
				singleStaticNonStaticImportWithNonExactName += "/";
			}
			
			// nepripojovat prvni cast, protoze ta uz je v tom single importu
			singleStaticNonStaticImportWithNonExactName += Utils.join(nonExactNames, "/", 1);
			
			if (singleStaticNonStaticImportWithNonExactName.equals(exactFullNameReplaced)) {
				return 0;
			}
			
			// import na neco jineho
			return -1;
		}
		
		// ted uz vime, ze tam neni single import -> dalsi moznost je trida z aktualniho balicku
		
		
		String cuPackage = imps.getPackage();
		
		if (cuPackage.equals(exactName.getPackage())) {
			
			// pouze pro stejne balicky, aby se predeslo tomu,
			// ze soucasti nonExactName bude balicek
			String packageAndNonExactName = cuPackage; 
			
			boolean isCUDefaultPackage = cuPackage.equals(BinaryName.DEFAULT_PACKAGE);
			
			if (!isCUDefaultPackage) {
				packageAndNonExactName += "/";
			}
			
			packageAndNonExactName += nonExactNameReplaced;
			
			if (packageAndNonExactName.equals(exactFullNameReplaced)) {
				// automaticky import z aktualniho balicku
				return 0;
			}
		}
		
		// mohlo by to byt take primo FQN
		if (exactFullNameReplaced.equals(nonExactNameReplaced)) {
			return 0;
		}
		
		
		// Ted uz vime, ze tam neni ani single import ani trida neni automaticky importovana
		// z CU balicku, ani to neni primo FQN. Dalsi moznost je, ze je to automaticky import java/lang 
		
		if (exactName.getPackage().equals("java/lang")) {
			if (exactFullNameReplaced.equals("java/lang/" + nonExactNameReplaced)) {
				// automaticky import z java/lang balicku
				return 0;
			}
		}
		
		// posledni moznost je import on demand, tzn. nektery z ondemand importu plus nonExactNameReplaced 
		// musi dat tu tridu
		
		if (imps.hasExplicitStaticNonStaticOnDemand(exactFullNameReplaced, nonExactNameReplaced)) {
			if (!exactName.getClassName().replace('$', '/').endsWith(nonExactNameReplaced)) {
				// nonExactName nesmi zacinat nejakym balickem
				return -1;
			}
			return 0;
		}
		
		return -1;
		
		/*
		if (isSimpleName) {
			
			if (exactName.isTopLevel()) {
				// top level trida
				if (!firstName.equals(exactName.getClassName())) {
					// nazev tridy se nerovna jednoduchemu jmenu (java.lang.Object -> jmeno musi byt Object)
					return -1;
				}
				
				if (exactName.hasDefaultPackage()) {
					if (!imps.hasDefaultPackage()) {
						// pokud je exact z defaultniho balikcu, pak kompilacni jednotka (CU) musi mit taky 
						// defaultni balicek, jinak by se to nezkompilovalo (nelze importovat tridy z defaultniho balicku)
						return -1;
					}
					
					if (imps.hasExplicitNonStaticImportEndsWith(firstName)) {
						// defaultni balicky, ale CU ma explicitni import neceho jineho
						return -1;
					}
					// ok defaultni balicky, firstName sedi a CU neimportuje neco jineho
					return 0;
				}
				
				if (exactName.getPackage().equals("java/lang")) {
					// top level trida z balicku java.lang (ty jsou importovane automaticky 
					// jako kdyby tam byl import java.lang.*)
					String explicitImp = imps.getExplicitNonStaticImportEndsWith(firstName);
					if (explicitImp == null) {
						// ok CU neimportuje neco jineho
						return 0;
					}
					if (explicitImp.equals("java/lang/" + firstName)) {
						// CU explicitne importuje tu tridu z java.lang
						return 0;
					}
					// fail, protoze CU importuje nejakou jinou tridu (napr. org.neco.Object)
					return -1;
				}
				
				if (imps.getPackage().equals(exactName.getPackage())) {
					// jsou ve stejnem balicku
					String explicitImp = imps.getExplicitNonStaticImportEndsWith(firstName);
					if (explicitImp == null) {
						// trida z jine CU ve stejnem balicku -> automaticky import
						return 0;
					}
					if (explicitImp.equals(exactName.getPackage() + '/' + firstName)) {
						// je tam explicitni import tridy z jine CU ve stejnem balicku
						return 0;
					}
					// CU importuje pod firstName neco jineho
					return -1;
				}
				
				// jiny balicek 
				
				if (imps.hasExplicitNonStaticImport(exactName)) {
					// pro neco z jineho balicku musi cely nazev odpovidat
					return 0;
				}
				if (imps.hasExplicitNonStaticOnDemandImport(exactName)) {
					//ok trida je importovana pres on demand import
					return 0;
				}
				return -1;
			}
			
			if (exactName.isExactInnerOrStaticNested()) {
			
				if (!exactName.getClassName().endsWith("$" + firstName)) {
					// vnorena trida nekonci na $firstName, fail
					return -1;
				}
				
				if (imps.hasExplicitStaticNonStaticImport(exactName)) {
					// v CU je primo import
					return 0;
				}
				
				if (imps.getExplicitStaticNonStaticImportEndsWith(firstName) != null) {
					// trida importuje neco jineho
					return -1;
				}
				else {
					if (imps.hasExplicitStaticNonStaticOnDemandImport(exactName)) {
						// on demand import
						return 0;
					}
					// nic, fail
					return -1;
				}
				
			}
			
		}
		else {
			//slozeny typ
			
			String imp = imps.getExplicitStaticNonStaticImportEndsWith(firstName);
			
			if(imp != null) {
				// CU importuje nejakou tridu firtsName
				StringBuilder sb = new StringBuilder(imp);
				sb.append('/');
				
				for (int i = 1; i < nonExactNames.length-1; i++) {
					sb.append(nonExactNames[i]);
					sb.append('/');
				}
				
				sb.append(nonExactNames[nonExactNames.length-1]);
				
				if (exactName.getFullName().replace('$',  '/').equals(sb.toString())) {
					return 0;
				}
				
				return -1;
			}
			
			// CU zadnout tridu firstName neimportuje, tzn. musi importovat onDemand nebo je nonExactNames plne kvalifikovane
			
			// jeste ze stejneho balicku
			
			// tak jeste jedine plne kvalifikovane jmeno 
			if (exactName.getFullName().replace('$', '/').equals(nonExactMdp.fName.replace('.', '/'))) {
				return 0;
			}
			
			if (imps.hasExplicitStaticNonStaticOnDemand(exactName.getFullName(), nonExactNames)) {
				return 0;
			}
			
			
			
			if (exactName.isTopLevel()) {
				
				// slozeny typ musi byt plne kvalifikovane jmeno
				if (nonExactMdp.fName.replace('.', '/').equals(exactName.getFullName())) {
					// Cu nesmi mit zadny import koncici na firstName
					if (imps.hasExplicitStaticNonStaticImportEndsWith(firstName)) {
						return -1;
					}
					return 0;
				}
				return -1;
			}
			if (exactName.isExactInnerOrStaticNested()) {
				// slozeny typ byt suffixem te vnorene tridy, tecky jsou nahrazeny dolarem
				if (exactName.getFullName().endsWith(nonExactMdp.fName.replace('.', '$'))) {
					String imp = imps.getExplicitStaticNonStaticImportEndsWith(firstName);
					if (exactName.getPackage().equals(imps.getPackage())) {
						// stejny balicek
						if (exactName.getFullName().equals(imps.getPackage() + '/' + nonExactMdp.fName.replace('.', '$'))) {
							// automaticky import vnorene tridy ze stejneho balicku?
							if (imp != null) {
								if (imp.equals(exactName.getFullName().replace('$', '/'))) {
									// explicitni import 
									return 0;
								}
								return -1; // importuje neco jineho
							}
							return 0;
						}
						else {
							// stejny balicek ale nemuze byt automaticky import, protoze firstName neidentifikuje top-level tridu
							// tzn. bud single import nebo on demand
							if (imps.hasExplicitStaticNonStaticImport(exactName.getFullWithoutParts(nonExactNames.length - 1))) {
								return 0;
							}
							else {
								if (imps.hasExplicitStaticNonStaticOnDemandImport(exactName.getFullWithoutParts(nonExactNames.length))) {
									if (imp != null) {
										return -1;
									}
									return 0;
								}
								return -1;
							}
						}
 					}
					else {
						// ruzne balicky
						if (imps.hasExplicitStaticNonStaticImport(exactName.getFullWithoutParts(nonExactNames.length - 1))) {
							return 0;
						}
						else {
							if (imps.hasExplicitStaticNonStaticOnDemandImport(exactName.getFullWithoutParts(nonExactNames.length))) {
								if (imp != null) {
									return -1;
								}
								return 0;
							}
							return -1;
						}
					}
				}
				return -1;
			}
			
		}
			
		return -1;
		*/
	}
	
	public static MethodDescriptor fromMethodDeclaration(MethodDeclaration md, BinaryNameResolver resolver) {
		
		MethodDescriptor desc = new MethodDescriptor();
		
		for (Object fragment : md.parameters()) {
			
			SingleVariableDeclaration parameter = (SingleVariableDeclaration) fragment;
			
			Type t = parameter.getType();
			
			if (t.isPrimitiveType()) {
				PrimitiveType type = (PrimitiveType) t;
				desc.fParameters.add(DescriptorPart.fromPrimitiveType(type));
			}
			else if (t.isArrayType()) {
				ArrayType type = (ArrayType) t;
				desc.fParameters.add(DescriptorPart.fromArrayType(type, resolver));
			}
			else if (t.isSimpleType()) {
				SimpleType type = (SimpleType) t;
				desc.fParameters.add(DescriptorPart.fromSimpleType(type, resolver));
			}
			else if (t.isQualifiedType()) {
				/*
				QualifiedType type = (QualifiedType) t;
				System.out.println("DESCRIPTOR QUALIFIED");
				*/
			}
			else if (t.isNameQualifiedType()) {
				/*
				NameQualifiedType type = (NameQualifiedType) t;
				System.out.println("DESCRIPTOR NAME_QUALIFIED");
				*/
			}
			
		}
		
		return desc;
	}
	
}
