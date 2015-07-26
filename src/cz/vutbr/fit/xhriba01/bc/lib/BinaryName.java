package cz.vutbr.fit.xhriba01.bc.lib;

import java.util.HashMap;
import java.util.Map;

//http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.1
public class BinaryName {
	
	public static String DEFAULT_PACKAGE = "";
	
	public static String ANONYM_SIMPLE_NAME = "";
	
	private String fPackage;
	
	private String fClassName;
	
	private String fFullName;
	
	private String fSimpleName;
	
	protected Map<String, Integer> fNextLocals;
	
	protected int fNextAnonym = 1;
	
	public BinaryName(String binary) {
		fFullName = binary;
		fPackage = BinaryName.getPackage(binary);
		fClassName = BinaryName.getClass(binary);
		fSimpleName = BinaryName.getSimple(fClassName);
	}
	
	public static BinaryName fromDotStyle(String javaFQDN) {
		return new BinaryName(javaFQDN.replace('.', '/'));
	}
	
	private int getNextLocalInc(String declarationName) {
		if (fNextLocals == null) {
			fNextLocals = new HashMap<>();
		}
		
		Integer val = fNextLocals.get(declarationName);
		
		if (val == null) {
			fNextLocals.put(declarationName, 1);
			return 1;
		}
		else {
			fNextLocals.put(declarationName, val+1);
			return val++;
		}
	}
	
	public String getNextAnonymName() {
		return BinaryName.formatAnonymClassName(fClassName, fNextAnonym++);
	}
	
	public String getNextLocalName(String simpleClassName) {
		return BinaryName.formatLocalClassName(fClassName, getNextLocalInc(simpleClassName), simpleClassName);
	}
	
	public String getInnerOrStaticNestedName(String simpleClassName) {
		return BinaryName.formatInnerOrStaticNestedClassName(fClassName, simpleClassName);
	}
	
	public String getSimpleName() {
		return fSimpleName;
	}
	
	public String getClassName() {
		return fClassName;
	}
	
	public String getPackage() {
		return fPackage;
	}
	
	public boolean hasDefaultPackage() {
		return fPackage.equals(DEFAULT_PACKAGE);
	}
	
	public boolean isTopLevel() {
		return !fClassName.contains("$");
	}
	
	public String getFullName() {
		return fFullName;
	}
	
	public boolean isNonExactAnonym() {
		return fClassName.matches("^(.*?\\$[0-9]+\\$.*?)|.*?\\$[0-9]+$");
	}
	
	public boolean isNonExactLocal() {
		return fClassName.matches("^.*?\\$[0-9]+[a-zA-Z].*?$");
	}
	
	public boolean isExactLocal() {
		return fClassName.matches("^.*?\\$[0-9]+[a-zA-Z]+$");
	}
	
	public String getFullWithoutParts(int count) {
		String[] names = fFullName.replace('$', '/').split("/");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= (names.length - count - 2); i++) {
			sb.append(names[i]);
			sb.append('/');
		}
		sb.append(names[names.length-count-1]);
		return sb.toString();
	}
	
	public boolean isExactInnerOrStaticNested() {
		return !isNonExactLocal() && !isTopLevel() && !isNonExactAnonym();
	}
	
	public String getDotPackage() {
		return fPackage.replace('/', '.');
	}
	
	public static String formatAnonymClassName(String parent, int index) {
		return parent + "$" + index;
	}
	
	public static String formatLocalClassName(String parent, int index, String name) {
		return parent + "$" + index + name;
	}
	
	public static String formatInnerOrStaticNestedClassName(String parent, String name) {
		return parent + "$" + name;
	}
	
	public static String formatClassName(String packagee, String name) {
		if (packagee.isEmpty()) {
			return name;
		}
		else {
			if (packagee.endsWith("/")) {
				return packagee + name;
			}
			else {
				return packagee + "/" + name;
			}
		}
	}
	
	public static String getSimple(String className) {
		
		int lastDollarIndex = className.lastIndexOf('$');
		
		if (lastDollarIndex == -1) {
			return className;
		}
		
		String binarySimpleName = className.substring(lastDollarIndex+1, className.length());
		
		int len = binarySimpleName.length();
		
		int i = 0;
		
		for(; i < len && Character.isDigit(binarySimpleName.charAt(0)); i++);
		
		if (i == 0) {
			return binarySimpleName;
		}
		else if (i == len){
			return ANONYM_SIMPLE_NAME;
		}
		else {
			return binarySimpleName.substring(i, len);
		}
		
	}
	
	public static boolean isAnonymSimpleName(String name) {
		return name.equals(ANONYM_SIMPLE_NAME);
	}
	
	public static String getPackage(String binaryName) {
		
		String[] names = binaryName.split("/");
		
		int len = names.length;
		
		if (len == 1) {
			return DEFAULT_PACKAGE;
		}
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i <= len-3; i++) {
			sb.append(names[i]);
			sb.append('/');
		}
		
		sb.append(names[len-2]);
		
		return sb.toString();
	}
	
	public static String getClass(String binaryName) {
		
		String[] names = binaryName.split("/");
		
		int len = names.length;
		
		if (len == 1) return binaryName;
		
		return names[len-1];
	}
	
}
