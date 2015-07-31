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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.objectweb.asm.Opcodes;

/**
 * Contains useful utility methods.
 */
public class Utils {
	
	public static int INVALID_LINE = -1;
	public static int INVALID_OFFSET = -1;
	
	public static byte[] inputStreamToBytes(InputStream stream) {
		
		try {
			
			return IOUtils.toByteArray(stream);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String inputStreamToString(InputStream stream) {
		
		try {
			
			return IOUtils.toString(stream);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Returns the package from full class name.
	 * The className is always in format with slash as exists in .class files.
	 * <b>Example:</b>
	 * className: <code>java/util/ArrayList</code>
	 * returns: <code>java.util</code>
	 * 
	 * @param className FQNT (for instance java/util/ArrayList)
	 * @return the package from className (java.util from className example)
	 */
	public static String getPackageFromClassName(String className) {
		IPath path = new Path(className);
		return convertClassPackageToJavaPackage(path.removeLastSegments(1).removeTrailingSeparator().toString());
	}
	
	/**
	 * Converts package format used in .class files to package
	 * format used in .java files (s/\//\./g).
	 * <b>Example:</b>
	 * classPackage: <code>java/util</code>
	 * returns: <code>java.util</code>
	 * 
	 * @param path .class package format
	 * @return package
	 */
	public static String convertClassPackageToJavaPackage(String classPackage) {
		return classPackage.replace('/', '.');
	}
	
	/**
	 * Returns the name of .class file from the .class file 
	 * formatted class name.
	 * <b>Example:</b>
	 * className: <code>java/util/ArrayList</code>
	 * returns: <code>ArrayList.class</code>
	 * 
	 * @param className .class formated class name (java/util/ArrayList)
	 * @return .class file name 
	 */
	public static String getClassFileFromClassName(String className) {
		IPath path = new Path(className);
		path = path.addFileExtension("class");
		return path.lastSegment();
	}
	
	/**
	 * Creates text representation of asm method access flag.
	 * 
	 * @param flag asm method access flag
	 * @return formatted string representing the access flag
	 */
	public static String formatMethodAccessFlags(int flag) {
		List<String> accesses = new ArrayList<>();
		if ((flag & Opcodes.ACC_PUBLIC) != 0) {
			accesses.add("public");
		}
		if ((flag & Opcodes.ACC_PROTECTED) != 0) {
			accesses.add("protected");
		}
		if ((flag & Opcodes.ACC_PRIVATE) != 0) {
			accesses.add("private");
		}
		if ((flag & Opcodes.ACC_STATIC) != 0) {
			accesses.add("static");
		}
		if ((flag & Opcodes.ACC_ABSTRACT) != 0) {
			accesses.add("abstract");
		}
		if ((flag & Opcodes.ACC_FINAL) != 0) {
			accesses.add("final");
		}
		return join(accesses.toArray(new String[accesses.size()]), " ");
	}
	
	/**
	 * Creates text representation of asm class access flag.
	 * 
	 * @param flag asm class access flag
	 * @return formatted string representing the access flag
	 */
	public static String formatClassAccessFlags(int flag) {
		List<String> accesses = new ArrayList<>();
		if ((flag & Opcodes.ACC_PUBLIC) != 0) {
			accesses.add("public");
		}
		if ((flag & Opcodes.ACC_PROTECTED) != 0) {
			accesses.add("protected");
		}
		if ((flag & Opcodes.ACC_PRIVATE) != 0) {
			accesses.add("private");
		}
		if ((flag & Opcodes.ACC_STATIC) != 0) {
			accesses.add("static");
		}
		if ((flag & Opcodes.ACC_ABSTRACT) != 0) {
			accesses.add("abstract");
		}
		if ((flag & Opcodes.ACC_FINAL) != 0) {
			accesses.add("final");
		}
		if ((flag & Opcodes.ACC_ENUM) != 0) {
			accesses.add("enum");
		}
		else if ((flag & Opcodes.ACC_INTERFACE) != 0) {
			accesses.add("interface");
		}
		else {
			accesses.add("class");
		}
		return join(accesses.toArray(new String[accesses.size()]), " ");
	}
	
	public static String join(String[] elements, String separator) {
		return join(elements, separator, 0);
	}
	
	/**
	 * Join strings with separator.
	 * <b>Example:</b>
	 * elements: <code>java, util, ArrayList</code>
	 * separator: <code>.</code>
	 * returns: <code>java.util.ArrayList</code>
	 * 
	 * @param elements elements to join
	 * @param separator placed between subsequent elements
	 * @return joined string elements
	 */
	public static String join(String[] elements, String separator, int startIndex) {
		
		StringBuilder sb = new StringBuilder();
		
		int len = elements.length;
		
		if (len-startIndex <= 0) return "";
		
		int cond = len - 1;
		
		for (int i = startIndex; i < cond; i++) {
			sb.append(elements[i]);
			sb.append(separator);
		}
		
		sb.append(elements[cond]);
		
		return sb.toString();	
	}
	
}
